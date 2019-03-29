package edu.northeastern.ccs.im;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.*;

/**
 * Test class for the PasswordHash class.
 */
public class PasswordHashTest {

    /**
     * Tests for the hashPassword method.
     */
    @Test
    public void testOne() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String test1 = "Hello";
        String test2 = "hello";
        String test3 = "Hello";

        String Hello = "c4097eb69254aaab618948ffc24d416da038e891bdd9cf791e";
        String hello = "34da57c3a07fbb15f19b38eebe2b476ae35d372f0562bdb2ad";

        assertEquals(Hello, PasswordHash.hashPassword(test1));
        assertEquals(hello, PasswordHash.hashPassword(test2));
        assertEquals(Hello, PasswordHash.hashPassword(test3));
    }



}