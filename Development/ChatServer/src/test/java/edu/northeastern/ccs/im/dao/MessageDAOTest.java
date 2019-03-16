package edu.northeastern.ccs.im.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message;

public class MessageDAOTest {
	
	MessageDAO messageDAO;
	boolean isException;
	@Before
	public void setUp() throws NoSuchFieldException, IllegalAccessException {
		messageDAO = MessageDAO.getInstance();
		Class clazz = MessageDAO.class;
		Field connectionManager = clazz.getDeclaredField("connectionManager");
		connectionManager.setAccessible(true);
		connectionManager.set(messageDAO, new ConnectionManager());
		isException = false;

	}

	@After
	public void cleanUp() {
		isException = false;
	}

	@Test
	public void testCreateMessage() {
		Message message = messageDAO.createMessage(new Message(Message.MsgType.PVT, 52, "test message", Long.toString(System.currentTimeMillis())));
		assertEquals("test message", message.getMessage());
	}

	@Test(expected = DatabaseConnectionException.class)
	public void testCreateMessageException() throws NoSuchFieldException, IllegalAccessException {
		Class clazz = MessageDAO.class;
		Field connectionManager = clazz.getDeclaredField("connectionManager");
		connectionManager.setAccessible(true);
		connectionManager.set(messageDAO, new ConnectionTest());
		isException = true;
		Message message = messageDAO.createMessage(new Message(Message.MsgType.PVT, 52, "test message", Long.toString(System.currentTimeMillis())));
	}
	
	@Test
	public void testGetMessageByID() {
		Message message = messageDAO.getMessageByID(2);
		assertEquals(52, message.getSenderID());
	}

	@Test(expected = DatabaseConnectionException.class)
	public void testGetMessageByIDException() throws NoSuchFieldException, IllegalAccessException {
		Class clazz = MessageDAO.class;
		Field connectionManager = clazz.getDeclaredField("connectionManager");
		connectionManager.setAccessible(true);
		connectionManager.set(messageDAO, new ConnectionTest());
		isException = true;
		Message message = messageDAO.getMessageByID(2);
	}
	
	@Test
	public void testGetMessageBySender() {
		List<Message> message = messageDAO.getMessagesBySender(52);
		assertNotNull(message.size());
	}

	@Test(expected = DatabaseConnectionException.class)
	public void testGetMessageBySenderException() throws NoSuchFieldException, IllegalAccessException {
		Class clazz = MessageDAO.class;
		Field connectionManager = clazz.getDeclaredField("connectionManager");
		connectionManager.setAccessible(true);
		connectionManager.set(messageDAO, new ConnectionTest());
		isException = true;
		List<Message> message = messageDAO.getMessagesBySender(52);
	}

	@Test(expected = DatabaseConnectionException.class)
	public void testGetMessageFail() {
		messageDAO.getMessageByID(1);
	}

	@Test(expected = DatabaseConnectionException.class)
	public void testGetMessageFailException() throws NoSuchFieldException, IllegalAccessException {
		Class clazz = MessageDAO.class;
		Field connectionManager = clazz.getDeclaredField("connectionManager");
		connectionManager.setAccessible(true);
		connectionManager.set(messageDAO, new ConnectionTest());
		isException = true;
		Message message = messageDAO.getMessageByID(1);
	}
}
