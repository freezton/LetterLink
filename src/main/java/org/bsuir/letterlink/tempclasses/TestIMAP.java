package org.bsuir.letterlink.tempclasses;

import jakarta.mail.*;


public class TestIMAP {
    public static void main(String[] args) throws MessagingException {
        String host = DataClass.imapHost;
        String address = DataClass.address;

        String password = DataClass.password;
        String port = DataClass.imapPort;
//        EmailEntity entity = new EmailEntity(address, password, )

//        FolderHandler folderHandler = new FolderHandler(host, port, );

//        for (Folder folder : folderHandler.getFolders()) {
//            folder.open(Folder.READ_WRITE);
//
//            System.out.println(folder.getName());
//            for (Message message: folder.getMessages()) {
//                System.out.println("  " + message.getSubject());
//            }
//        }
    }
}
