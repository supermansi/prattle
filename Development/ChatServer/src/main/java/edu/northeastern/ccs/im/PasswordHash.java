package edu.northeastern.ccs.im;

import org.apache.commons.codec.binary.Hex;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Class for encoding a given string for storage in the database.
 */
public class PasswordHash {


    private PasswordHash(){
        //empty constructor
    }

    /**
     * This method will return a string that represents the encoded version of the string passed
     * in as input.
     *
     * @param pass string to be passed in as password
     * @return string representing the encoded version of the password
     */
    public static String hashPassword(String pass) {

        String salt = "219";
        int iterations = 1000;
        int keyLength = 200;

        char[] passChars = pass.toCharArray();
        byte[] saltBytes = salt.getBytes();
        SecretKeyFactory skf;
        PBEKeySpec spec;
        SecretKey key;

        try{
            skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            spec = new PBEKeySpec( passChars, saltBytes, iterations, keyLength );
            key = skf.generateSecret( spec );
            return Hex.encodeHexString(key.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NullPointerException e) {
            ChatLogger.error(e.getMessage());
        }
        return "pass hash failed";
    }
}
