package org.bsuir.letterlink.classes;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator {
    private String user;
    private String password;

     public MailAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        String user = this.user;
        String password = this.password;
        return new PasswordAuthentication(user, password);
    }
}
