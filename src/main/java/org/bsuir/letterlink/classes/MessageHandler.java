package org.bsuir.letterlink.classes;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bsuir.letterlink.entities.MessageEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MessageHandler {

    public static ObservableList<MessageEntity> getMessageEntityList(Message[] messages) throws MessagingException {
        List<MessageEntity> messagesList = new ArrayList<>();
        for (Message message: messages) {
//            Pattern pattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}");
//            Matcher matcher = pattern.matcher(message.getFrom()[0].toString());
//            while (matcher.find())
//                System.out.println(matcher.group());
//            String from = Pattern.compile().matcher(message.getFrom()[0].toString()).group();
            messagesList.add(new MessageEntity(
                    message,
                    message.getSubject(),
                    message.getFrom()[0].toString(),
//                    message.getAllRecipients()[0].toString(),
                    message.getSentDate(),
                    message.isSet(Flags.Flag.SEEN)
            ));
//            for (Address address: message.getFrom()) {
//                System.out.println(message.getFrom()[0]);
//            }
            System.out.println();
        }
        return FXCollections.observableList(messagesList);
    }
}
