package com.dat.event.encryptor;

import org.jasypt.util.text.BasicTextEncryptor;

public class EncryptorTool {
    public static void main(String[] args) {
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword("admin"); // master password

        // values to encrypt
        String username = "myomyintaung@diracetechnology.com";
        String password = "Mm@d@td!race";

        // encrypt
        String encUser = encryptor.encrypt(username);
        String encPass = encryptor.encrypt(password);

        // print results
        System.out.println("Username -> ENC(" + encUser + ")");
        System.out.println("Password -> ENC(" + encPass + ")");

        // quick test: decrypt back
        System.out.println("Decrypt username -> " + encryptor.decrypt(encUser));
        System.out.println("Decrypt password -> " + encryptor.decrypt(encPass));
    }
}
