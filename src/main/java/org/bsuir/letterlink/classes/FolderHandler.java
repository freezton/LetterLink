package org.bsuir.letterlink.classes;

import jakarta.mail.*;

import org.bsuir.letterlink.controllers.MainWindowController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FolderHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FolderHandler.class);
    private Store store;


    public Boolean isFolderExists(Set<Folder> folderSet, String name) {
        for (Folder folder: folderSet) {
            if (folder.getName().toLowerCase().equals(name.toLowerCase()))
                return true;
        }
        return false;
    }

    public FolderHandler(String host, String port, String address, String password) {
        Authenticator auth = new MailAuthenticator(address, password);
        Session session;
        if (MainWindowController.isLetterlink(address))
            session = SessionHandler.getSession("imap", host, port, auth);
        else
            session = SessionHandler.getSession("imaps", host, port, auth);
        try {
            store = session.getStore("imap");
        } catch (MessagingException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public FolderHandler(String host, String port, Authenticator auth) {
        Session session;
        if (MainWindowController.isLetterlink(MainWindowController.email.getAddress()))
            session = SessionHandler.getSession("imap", host, port, auth);
        else
            session = SessionHandler.getSession("imaps", host, port, auth);
        try {
            store = session.getStore("imap");
        } catch (MessagingException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public Folder getTrashFolder(Set<Folder> folderSet) {
        for (Folder folder: folderSet) {
            if (folder.getName().toLowerCase().equals("trash") || folder.getName().toLowerCase().equals("deleted") || folder.getName().toLowerCase().equals("корзина")) {
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

//    public List<Folder> getFolders() throws MessagingException {
    public Folder[] getFolders() throws MessagingException {
        try {
//            Folder[] folders = store.getDefaultFolder().list();
            return store.getDefaultFolder().list();
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


