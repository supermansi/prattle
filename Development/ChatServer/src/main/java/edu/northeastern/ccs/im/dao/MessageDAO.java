package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message;

public class MessageDAO {
	
	  protected static ConnectionManager connectionManager;
	  private static MessageDAO messageDAO;

	  public static MessageDAO getInstance() {
	    if (messageDAO == null) {
	      connectionManager = new ConnectionManager();
	      messageDAO = new MessageDAO();
	    }
	    return messageDAO;
	  }

	  private MessageDAO() {
	    //empty private constructor for singleton
	  }
	  
	  public void createMessage(Message message) {
		    String insertMessage = "INSERT INTO Message(msgType, senderID, message, timestamp) VALUES(?,?,?,?);";
		    try (Connection connection = connectionManager.getConnection();
		         PreparedStatement preparedStatement = connection.prepareStatement(insertMessage, Statement.RETURN_GENERATED_KEYS);) {
		      preparedStatement.setString(1, message.getMsgType().name());
		      preparedStatement.setInt(2, message.getSenderID());
		      preparedStatement.setString(3, message.getMessage());
		      preparedStatement.setTimestamp(4, message.getTimestamp());
		      preparedStatement.executeUpdate();
		      try (ResultSet resultSet = preparedStatement.getGeneratedKeys();) {
		        int msgID;
		        if (resultSet.next()) {
		          msgID = resultSet.getInt(1);
		        } else {
		          throw new DatabaseConnectionException("MessageID could not be generated.");
		        }
		        message.setMsgID(msgID);
		      }
		    } catch (SQLException e) {
		      throw new DatabaseConnectionException(e.getMessage()+"\n"+e.getStackTrace());
		    }
	  }
	  
	  public void deleteMessage(Message message) {}
	  
	  public Message getMessageByID(int msgID) {
		  	String getMessage = "SELECT * FROM Message WHERE msgID = ?;";
		    try (Connection connection = connectionManager.getConnection();
		         PreparedStatement preparedStatement = connection.prepareStatement(getMessage, Statement.RETURN_GENERATED_KEYS);) {
		      preparedStatement.setInt(1, msgID);
		      Message message;
		      try(ResultSet resultSet = preparedStatement.executeQuery();) {
		        if (resultSet.next()) {
		        Message.MsgType msgType = Message.MsgType.valueOf(resultSet.getString("msgType"));
		        int senderID = resultSet.getInt("senderID");
		        String context = resultSet.getString("message");
		        Timestamp timestamp = resultSet.getTimestamp("timestamp");
		        message = new Message(msgID, msgType, senderID, context, timestamp);
		        }
		        else {
		          throw new DatabaseConnectionException("Message not found.");
		        }
		      }
		      return message;
		    } catch (SQLException e) {
		      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
		    }
	  }
	  
	  public List<Message> getMessagesBySender(int senderID) {
		  List<Message> messages = new ArrayList<>();
		  String listMessages = "SELECT * FROM Message WHERE senderID=?;";
		try (Connection connection = connectionManager.getConnection();
		         PreparedStatement preparedStatement = connection.prepareStatement(listMessages, Statement.RETURN_GENERATED_KEYS);) {
		      preparedStatement.setInt(1, senderID);
		      try (ResultSet resultSet = preparedStatement.executeQuery();) {
		        while (resultSet.next()) {
		          int msgID = resultSet.getInt("msgID");
		          messages.add(getMessageByID(msgID));
		        }
		      }
		      return messages;
		    } catch (SQLException e) {
		      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
		    }
	  }
}
