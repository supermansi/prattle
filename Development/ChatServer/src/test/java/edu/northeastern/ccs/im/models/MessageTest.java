package edu.northeastern.ccs.im.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.northeastern.ccs.im.model.Message;

public class MessageTest {
	
	Message message;
	
	@Test
	public void testMessageID() {
		message.setMsgID(123);
		assertEquals(123, message.getMsgID());
	}
}
