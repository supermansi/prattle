package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;

import org.junit.Test;

import edu.northeastern.ccs.im.model.Message;

public class MessageDAOTest {
	
	MessageDAO messageDAO = MessageDAO.getInstance();
	
	@Test
	public void testCreateMessage() {
		messageDAO.createMessage(new Message(Message.MsgType.PVT, 52, "test message", new Timestamp(System.currentTimeMillis())));
	}
	
	@Test
	public void testGetMessageByID() {
		Message message = messageDAO.getMessageByID(2);
		assertEquals(52, message.getSenderID());
	}

}
