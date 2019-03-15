package edu.northeastern.ccs.im;

import java.util.Base64;

public class PasswordHash {

    //https://stackoverflow.com/questions/19743851/base64-java-encode-and-decode-a-string

    public static String hashPassword(String pass){
        String encoded = Base64.getEncoder().encodeToString(pass.getBytes());
        return encoded;
    }
}
