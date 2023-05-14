package org.bsuir.letterlink.tempclasses;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.bsuir.letterlink.classes.FolderHandler;
import org.bsuir.letterlink.classes.MailAuthenticator;
import org.bsuir.letterlink.classes.SessionHandler;
import org.bsuir.letterlink.tempclasses.DataClass;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class TestSMTP {
    static final String ENCODING = "UTF-8";

    public static void main(String args[]) throws MessagingException, IOException {
        String subject = "Letterlink test oleg mongolenko";
        String content = "Тут написан просто какой-то текст2";

        String smtpHost = DataClass.smtpHost;
        String address = DataClass.address;
        String login = DataClass.login;
        String to = "qwjvwjl@gmail.com";
        String password = DataClass.password;
        String smtpPort = DataClass.smtpPort;

        String attachment = "d:\\pic.jpg";
        sendMultiMessage(login, password, address, to, content, subject, attachment, smtpPort, smtpHost);
//        sendSimpleMessage (login, password, address, to, content, subject, smtpPort, smtpHost);

    }

    public static void sendSimpleMessage(String login, String password, String from, String to, String content, String subject, String smtpPort, String smtpHost)
            throws MessagingException {

        Authenticator auth = new MailAuthenticator(login, password);
        Session session = SessionHandler.getSession("smtp", smtpHost, smtpPort, auth);

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject);
        msg.setText(content);
        Transport.send(msg);
    }

    public static void sendMultiMessage(String login, String password, String from, String to, String content, String subject, String attachment, String smtpPort, String smtpHost) throws MessagingException, UnsupportedEncodingException, UnsupportedEncodingException {
//        props.put("mail.mime.charset", ENCODING);

        Authenticator auth = new MailAuthenticator(login, password);
        Session session = SessionHandler.getSession("smtp", smtpHost, smtpPort, auth, ENCODING);

        MimeMessage msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(from));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        msg.setSubject(subject, ENCODING);

        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(content, "text/plain; charset=" + ENCODING + "");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(attachment); 
        attachmentBodyPart.setDataHandler(new DataHandler(source));
        attachmentBodyPart.setFileName(MimeUtility.encodeText(source.getName()));
        multipart.addBodyPart(attachmentBodyPart);

        msg.setContent(multipart);
        System.out.println(msg);


        FolderHandler handler = new FolderHandler(DataClass.imapHost, DataClass.imapPort, auth);

        Transport.send(msg);
    }

}

