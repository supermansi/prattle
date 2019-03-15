package edu.northeastern.ccs.im.services;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.northeastern.ccs.im.model.Message;

public class MessageServiceTest {
		
	@Test
	public void testSend() {
		assertTrue(MessageServices.addMessage(Message.MsgType.PVT, "admin", "Karl", "hello Karl"));
	}
	
	@Test
	public void testSendGroup() {
		assertTrue(MessageServices.addMessage(Message.MsgType.GRP, "admin", "grouptest1", "hello Karl"));
	}
	
	@Test
	public void testSendGroupFail() {
		assertTrue(MessageServices.addMessage(Message.MsgType.GRP, "admin", "Karl", "hello Karl"));
	}

}
