package org.bsuir.letterlink.entities;

import jakarta.mail.Authenticator;
import org.bsuir.letterlink.classes.MailAuthenticator;

public class EmailEntity {
    private String address;
    private String password;
    private Authenticator auth;

    public Authenticator getAuth() {
        return auth;
    }

    public EmailEntity(String address, String password) {
        this.address = address;
        this.password = password;
        this.auth = new MailAuthenticator(extractLoginFromEmail(address), password);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String extractLoginFromEmail(String email) {
        int index = email.indexOf('@');
        if (index > 0) {
            return email.substring(0, index);
        } else {
            throw new IllegalArgumentException("Invalid email address format");
        }
    }

}
