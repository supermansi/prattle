package edu.northeastern.ccs.im.models;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.model.Message;

public class MessageTest {
	
	Message message;
	
	@Before
	public void setUp() {
		message = new Message();
	}
	
	@Test
	public void testMessageID() {
		message.setMsgID(123);
		assertEquals(123, message.getMsgID());
	}
	
	@Test
	public void testMessageType() {
		String type = "PVT";
		message.setMsgType(Message.MsgType.valueOf(type));
		assertEquals(type, message.getMsgType().name());
	}
	
	@Test
	public void testMessageSender() {
		message.setSenderID(12);
		assertEquals(12, message.getSenderID());
	}
	
	@Test
	public void testMessage() {
		message.setMessageText("hello");
		assertEquals("hello", message.getMessageText());
	}
	
	@Test
	public void testTimestamp() {
		String time = new Timestamp(System.currentTimeMillis()).toString();
		message.setTimestamp(time);
		assertEquals(time, message.getTimestamp());
	}
}
