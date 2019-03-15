package edu.northeastern.ccs.im.services;

import java.util.List;

import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.MessageDAO;
import edu.northeastern.ccs.im.dao.MessageToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message;

public class MessageServices {
	
	private static GroupDAO groupDAO;
	private static UserDAO userDAO;
	private static MessageDAO messageDAO;
	private static MessageToUserDAO messageUserDAO;
	
	private MessageServices() {
		// empty private constructor
	}
	
	static {
		groupDAO = GroupDAO.getInstance();
		userDAO = UserDAO.getInstance();
		messageDAO = MessageDAO.getInstance();
		messageUserDAO = MessageToUserDAO.getInstance();
	}
	
	public static boolean addMessage(Message.MsgType msgType, String sender, String receiver, String message) {
		if(msgType == Message.MsgType.PVT) {
		     if(userDAO.isUserExists(receiver)) {
		    	int senderID = userDAO.getUserByUsername(sender).getUserID();
				Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
				messageDAO.createMessage(sendMessage);
				messageUserDAO.mapMsgIdToReceiverId(sendMessage, userDAO.getUserByUsername(receiver).getUserID());
				return true;
		     }
		   }
		   else if(msgType == Message.MsgType.GRP) {
		     if(groupDAO.checkGroupExists(receiver)) {
		    	int senderID = userDAO.getUserByUsername(sender).getUserID();
				Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
				messageDAO.createMessage(sendMessage);
				messageUserDAO.mapMsgIdToReceiverId(sendMessage, groupDAO.getGroupByGroupName(receiver).getGrpID());
				return true;
		     }
		   }
		   else {
		     throw new DatabaseConnectionException("This is not a valid handle.");
		   }
		return false;
	}

	public static List<String> retrieveUserMessages(String sender, String receiver) {
		return messageUserDAO.retrieveUserMsg(sender,receiver);
	}
	
	public static List<String> retrieveGroupMessages(String groupName) {
		return messageUserDAO.getMessagesFromGroup(groupName);
	}
}
