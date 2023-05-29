package org.bsuir.letterlink.classes;

import jakarta.mail.Authenticator;
import jakarta.mail.Session;

import java.util.Properties;

public abstract class SessionHandler {

    public static String getSmtpHost(String address) {
        return "smtp." + address.substring(address.lastIndexOf('@')+1);
    }

    public static String getImapHost(String address) {
        return "imap." + address.substring(address.lastIndexOf('@')+1);
    }

    public static String getSmtpPort(String address) {
        return "587";
    }

    public static String getImapPort(String address) {
        return "993";
    }

    public static Session getSession(String protocol, String host, String port, Authenticator authenticator, String ... params) {
        Properties props = new Properties();
        switch (protocol) {
            case "smtp":
                props.setProperty("mail.smtp.auth", "true");
                props.setProperty("mail.smtp.port", port);
                props.setProperty("mail.smtp.host", host);
                break;
            case "imap":
                props.setProperty("mail.imap.auth", "true");
                props.setProperty("mail.imap.host", host);
                props.setProperty("mail.imap.port", port);
                props.setProperty("mail.store.protocol", protocol);
                break;
            case "smtps":
                props.setProperty("mail.smtp.starttls.enable", "true");
                props.setProperty("mail.smtp.auth", "true");
                props.setProperty("mail.smtp.port", port);
                props.setProperty("mail.smtp.host", host);
                break;
            case "imaps":
                props.setProperty("mail.imap.ssl.enable", "true");
                props.setProperty("mail.imap.auth", "true");
                props.setProperty("mail.imap.host", host);
                props.setProperty("mail.imap.port", port);
                props.setProperty("mail.store.protocol", protocol);
                break;
        }
        if (params.length > 0)
            props.setProperty("mail.mime.charset", params[0]);
        return Session.getInstance(props, authenticator);
    }

}
