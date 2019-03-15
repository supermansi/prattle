package edu.northeastern.ccs.im.services;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.northeastern.ccs.im.model.Message;

public class MessageServiceTest {
		
	@Test
	public void testSend() {
		assertTrue(MessageServices.addMessage(Message.MsgType.PVT, "admin", "Karl", "hello Karl"));
	}

}
