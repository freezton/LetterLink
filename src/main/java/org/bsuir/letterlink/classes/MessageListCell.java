package org.bsuir.letterlink.classes;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import javafx.scene.control.ListCell;

public class MessageListCell extends ListCell<Message> {
    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if (empty || message == null) {
            setText(null);
        } else {
            try {
                setText(message.getSubject());
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
