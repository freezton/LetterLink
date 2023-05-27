package org.bsuir.letterlink.classes;

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
            Pattern pattern = Pattern.compile("[A-z0-9._]+@[A-z0-9._]+");
            Matcher matcher = pattern.matcher(message.getFrom()[0].toString());
            String from;
            if (matcher.find())
                from = matcher.group();
            else
                from = "unknown";
            messagesList.add(new MessageEntity(
                    message,
                    message.getSubject(),
                    from,
                    message.getSentDate(),
//                    message.isSet(Flags.Flag.SEEN)
                    false
            ));
        }
        return FXCollections.observableList(messagesList);
    }
}
