package edu.northeastern.ccs.im;

import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordHashTest {


    @Test
    public void testOne(){
        String test1 = "Hello";
        String test2 = "hello";
        String test3 = "Hello";

        assertEquals("SGVsbG8=", PasswordHash.hashPassword(test1));
        assertEquals("aGVsbG8=", PasswordHash.hashPassword(test2));
        assertEquals("SGVsbG8=", PasswordHash.hashPassword(test3));
    }



}