package org.bsuir.letterlink.classes;

import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;
import org.bsuir.letterlink.entities.MessageEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;


public class MessageRendererService extends Service {

    private MessageEntity emailMessage;
    private WebEngine webEngine;
    private StringBuffer stringBuffer;

    public MessageRendererService(WebEngine webEngine) {
        this.webEngine = webEngine;
        this.stringBuffer = new StringBuffer();
        this.setOnSucceeded(event -> {
            displayMessage();
        });
    }

    public void setEmailMessage(MessageEntity emailMessage){
        this.emailMessage = emailMessage;
    }

    private void displayMessage(){
        String data = "";
        data = stringBuffer.toString();
        webEngine.loadContent(data);
    }

    @Override
    protected Task createTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                try{
                    getMessageData();
                } catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    private void getMessageData() {
        stringBuffer.setLength(0);
        Message message = emailMessage.getMessage();
        try {
            String contentType = message.getContentType();
            if (isMultipartType(contentType)) {
                Multipart multipart = (Multipart) message.getContent();
                loadMultipart(multipart, stringBuffer);
            } else if (isSimpleType(contentType)) {
                stringBuffer.append(message.getContent().toString());
            }
        } catch (Exception e) {
            stringBuffer.append("Unable to load message...");
        }
    }

    private String convertToBase64(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private void loadMultipart(Multipart multipart, StringBuffer stringBuffer) throws MessagingException, IOException {
        for (int i = multipart.getCount() - 1; i >= 0; i--){
            BodyPart bodyPart = multipart.getBodyPart(i);
            String contentType = bodyPart.getContentType();
            if (contentType.toLowerCase().startsWith("image/")) {
                String base64Data = convertToBase64(bodyPart.getInputStream());
                String htmlContent = "<div style=\"max-width: 100%; height: auto;\"><img src=\"data:image/png;base64," + base64Data + "\" style=\"max-width: 100%; height: auto;\"></div>";
                stringBuffer.append(htmlContent);
            }
            if (isSimpleType(contentType)) {
                if (!contentType.contains("text/plain"))
                    stringBuffer.append(bodyPart.getContent().toString());
            } else if (isMultipartType(contentType)) {
                Multipart multipart2 = (Multipart) bodyPart.getContent();
                loadMultipart(multipart2, stringBuffer);
            } else if (!isTextPlain(contentType)) {
                MimeBodyPart mbp = (MimeBodyPart) bodyPart;
                if (Part.ATTACHMENT.equalsIgnoreCase(mbp.getDisposition())) {
                    emailMessage.addAttachment(mbp);
                }
            }
        }
    }

    private boolean isTextPlain(String contentType){
        return contentType.contains("TEXT/PLAIN");
    }

    private boolean isSimpleType(String contentType){
        if(contentType.contains("TEXT/HTML") || contentType.contains("mixed") || contentType.contains("text")){
            return true;
        }else{
            return false;
        }
    }

    private boolean isMultipartType(String contentType){
        if(contentType.contains("multipart")){
            return true;
        }else{
            return false;
        }
    }
}
