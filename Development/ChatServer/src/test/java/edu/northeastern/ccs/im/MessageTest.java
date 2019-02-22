package edu.northeastern.ccs.im;

import edu.northeastern.ccs.im.server.ClientRunnable;
import org.junit.Test;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.rmi.MarshalException;
import java.util.Iterator;

import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void makeQuitMessage() {

        Message q = Message.makeQuitMessage("user");
        assertEquals("BYE 4 user 2 --", q.toString());
        assertEquals(true, q.terminate());
        assertEquals("user",q.getName());
    }

    @Test
    public void makeBroadcastMessage() {
        Message b = Message.makeBroadcastMessage("user", "Hello World.");
        assertEquals("BCT 4 user 12 Hello World.", b.toString());
    }

    @Test
    public void makeHelloMessage() {
        Message h = Message.makeHelloMessage("user");
        assertEquals("HLO 2 -- 4 user", h.toString());
    }

    @Test
    public void makeMessage() {
        Message m1 = Message.makeMessage("BYE", "user", "goodbye");
        Message m3 = Message.makeMessage("BCT", "user", "how are you?");
        Message m2 = Message.makeMessage("HLO", "user", "Hello World.");
        Message m4 = Message.makeMessage("hi", null, null);
        assertEquals("BYE 4 user 2 --", m1.toString());

        assertEquals("HLO 4 user 2 --", m2.toString());

        assertEquals("BCT 4 user 12 how are you?", m3.toString());


    }

    @Test
    public void makeSimpleLoginMessage() {
        Message lm = Message.makeSimpleLoginMessage("New User");
        assertEquals("HLO 8 New User 2 --", lm.toString());
    }

    @Test
    public void getName() {
        Message userM = Message.makeMessage("BCT", "user", "hello");
        assertEquals("user", userM.getName());
    }

    @Test
    public void getText() {
        Message text = Message.makeMessage("BCT", "user1", "Hello World.");
        assertEquals("Hello World.", text.getText());
    }

    @Test
    public void isBroadcastMessage() {
        Message bm = Message.makeMessage("BCT", "user", "hello");
        Message im = Message.makeMessage("HLO", "user", null);
        assertEquals(true, bm.isBroadcastMessage());
        assertEquals(false, im.isBroadcastMessage());
    }

    @Test
    public void isInitialization() {
        Message bm = Message.makeMessage("BCT", "user", "hello");
        Message im = Message.makeMessage("HLO", "user", null);
        assertEquals(true, im.isInitialization());
        assertEquals(false, bm.isInitialization());
    }

    @Test
    public void terminate() {
        Message qm = Message.makeMessage("BYE", "user", "hello");
        Message im = Message.makeMessage("HLO", "user", null);
        assertEquals(true, qm.terminate());
        assertEquals(false, im.terminate());
    }

    @Test
    public void toStringTest() {
        Message qmnn = Message.makeMessage("BYE", "user", "goodbye");
        Message qmn = Message.makeMessage("BYE", null, "goodbye");
        Message hm = Message.makeMessage("HLO", "user", "Hello World.");
        Message bmnn = Message.makeMessage("BCT", "user", "how are you?");
        Message bmn = Message.makeMessage("BCT", "user", null);

        assertEquals("BYE 4 user 2 --", qmnn.toString());
        assertEquals("BYE 2 -- 2 --", qmn.toString());

        assertEquals("HLO 4 user 2 --", hm.toString());

        assertEquals("BCT 4 user 12 how are you?", bmnn.toString());
        assertEquals("BCT 4 user 2 --", bmn.toString());
    }
}