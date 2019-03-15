package edu.northeastern.ccs.im.services;

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.GroupToUserDAO;
import edu.northeastern.ccs.im.dao.MessageDAO;
import edu.northeastern.ccs.im.dao.MessageToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
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
	
	public static boolean addMessage(Message.MsgType msgType, String sender, String receiver, String message) {
		boolean isReceiverValid = false;
		if(msgType == Message.MsgType.PVT) {
		     if(userDAO.isUserExists(receiver)) {
		       isReceiverValid = true;
		     }
		   }
		   else if(msgType == Message.MsgType.GRP) {
		     if(groupDAO.checkGroupExists(receiver)) {
		       isReceiverValid = true;
		     }
		   }
		   else {
		     throw new DatabaseConnectionException("This is not a valid handle.");
		   }
		if(isReceiverValid) {
			int senderID = userDAO.getUserByUsername(sender).getUserID();
			Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
			messageDAO.createMessage(sendMessage);
			messageUserDAO.mapMsgIdToReceiverId(sendMessage, userDAO.getUserByUsername(receiver).getUserID());
			return true;
		}
		return false;
	}
	
}
