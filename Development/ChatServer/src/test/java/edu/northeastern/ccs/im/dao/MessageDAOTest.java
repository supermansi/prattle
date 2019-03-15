package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import edu.northeastern.ccs.im.model.Message;

public class MessageDAOTest {
	
	MessageDAO messageDAO = MessageDAO.getInstance();
	
	@Test
	public void testCreateMessage() {
		Message message = messageDAO.createMessage(new Message(Message.MsgType.PVT, 52, "test message", Long.toString(System.currentTimeMillis())));
		assertEquals("test message", message.getMessageText());
	}
	
	@Test
	public void testGetMessageByID() {
		Message message = messageDAO.getMessageByID(2);
		assertEquals(52, message.getSenderID());
	}
	
	@Test
	public void testGetMessageBySender() {
		List<Message> message = messageDAO.getMessagesBySender(52);
		assertNotNull(message.size());
	}
	
	@Test
	public void testGetMessageFail() {
		messageDAO.getMessageByID(1);
	}

}
