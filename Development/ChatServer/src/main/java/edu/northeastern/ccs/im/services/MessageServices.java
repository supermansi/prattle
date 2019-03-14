package edu.northeastern.ccs.im.services;

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.GroupToUserDAO;
import edu.northeastern.ccs.im.dao.MessageDAO;
import edu.northeastern.ccs.im.dao.MessageToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;

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
	
	
}
