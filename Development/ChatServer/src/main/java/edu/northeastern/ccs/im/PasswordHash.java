package edu.northeastern.ccs.im;

import java.util.Base64;

/**
 * Class for encoding a given string for storage in the database.
 */
public class PasswordHash {

    /**
     * This method will return a string that represents the encoded version of the string passed
     * in as input.
     *
     * @param pass string to be passed in as password
     * @return string representing the encoded version of the password
     */
    public static String hashPassword(String pass){
        String encoded = Base64.getEncoder().encodeToString(pass.getBytes());
        return encoded;
    }
}
