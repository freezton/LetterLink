package org.bsuir.letterlink.classes;

import jakarta.mail.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FolderHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FolderHandler.class);
    private Store store;

    public FolderHandler(String host, String port, String address, String password) {
        Authenticator auth = new MailAuthenticator(address, password);
        Session session = SessionHandler.getSession("imap", host, port, auth);
        try {
            store = session.getStore("imap");
        } catch (MessagingException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public FolderHandler(String host, String port, Authenticator auth) {
        Session session = SessionHandler.getSession("imap", host, port, auth);
        try {
            store = session.getStore("imap");
        } catch (MessagingException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Folder getTrashFolder(Set<Folder> folderSet) {
        for (Folder folder: folderSet) {
            if (folder.getName().toLowerCase().equals("Trash") || folder.getName().toLowerCase().equals("Deleted") || folder.getName().toLowerCase().equals("Корзина")) {
                return folder;
            }
        }
        return null;
    }

    public void openStore() throws MessagingException {
        if (!store.isConnected()) {
            store.connect();
        }
    }

    public void closeStore() throws MessagingException {
        if (store.isConnected()) {
            store.close();
        }
    }

    public List<Folder> getFolders() throws MessagingException {
        try {
            Folder[] folders = store.getDefaultFolder().list();
            return Arrays.asList(folders);
        } catch (MessagingException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    public Folder getFolder(String name) throws MessagingException {
        try {
            return store.getFolder(name);
        } catch (MessagingException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

}


