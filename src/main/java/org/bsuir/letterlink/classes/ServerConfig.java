package org.bsuir.letterlink.classes;

import org.bsuir.letterlink.controllers.MainWindowController;

public class ServerConfig {

    private static final String IP = "26.155.93.159";
    public static final String imapPort = "993";
    public static final String smtpPort = "587";

    public static String getImapHost(String address) {
        if (MainWindowController.isLetterlink(address)) {
            return IP;
        } else {
            return "imap." + address.substring(address.lastIndexOf('@')+1);
        }
    }

    public static String getSmtpHost(String address) {
        if (MainWindowController.isLetterlink(address)) {
            return IP;
        } else {
            return "smtp." + address.substring(address.lastIndexOf('@')+1);
        }
    }
}
