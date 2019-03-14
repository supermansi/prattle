package edu.northeastern.ccs.im.services;

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.GroupToUserDAO;
import edu.northeastern.ccs.im.dao.MessageDAO;
import edu.northeastern.ccs.im.dao.MessageToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.model.Message;

public class MessageServices {
	
	private static GroupDAO groupDAO;
	private static GroupToUserDAO groupUserDAO;
	private static UserDAO userDAO;
	private static MessageDAO messageDAO;
	private static MessageToUserDAO messageUserDAO;
	
	private MessageServices() {
		// empty private constructor
	}
	
	static {
		groupDAO = GroupDAO.getInstance();
		groupUserDAO = GroupToUserDAO.getInstance();
		userDAO = UserDAO.getInstance();
		messageDAO = MessageDAO.getInstance();
		messageUserDAO = MessageToUserDAO.getInstance();
	}
	
	public boolean addMessage(String msgType, String sender, String receiver, String message) {
		if(userDAO.isUserExists(sender)) {
			if(userDAO.isUserExists(receiver)) {
				int senderID = userDAO.getUserByUsername(sender).getUserID();
				Message sendMessage = new Message(Message.MsgType.valueOf(msgType), senderID, message, Long.toString(System.currentTimeMillis()));
				return true;
			}
		}
		return false;
	}
	
}
