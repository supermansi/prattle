package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message;

public class MessageToUserDAO {

  private static UserDAO userDAO;
  private static MessageToUserDAO messageToUserDAO;
  private static GroupDAO groupDAO;
  protected static ConnectionManager connectionManager;

  private MessageToUserDAO() {
    //empty private constructor for singleton
  }

  public static MessageToUserDAO getInstance() {
    if (messageToUserDAO == null) {
      connectionManager = new ConnectionManager();
      messageToUserDAO = new MessageToUserDAO();
      userDAO = UserDAO.getInstance();
      groupDAO = GroupDAO.getInstance();
    }
    return messageToUserDAO;
  }

  public void mapMsgIdToReceiverId(Message message, int receiverId) {
    String insertMSgToUserMap = "INSERT INTO MESSAGETOUSERMAP(MSGID, RECEIVERID) VALUES(?,?);";
    // Check if group exists and user exists
    try (Connection connection = connectionManager.getConnection();
         PreparedStatement statement = connection.prepareStatement(insertMSgToUserMap);) {
      statement.setInt(1, message.getMsgID());
      statement.setInt(2, receiverId);
      statement.executeUpdate();
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }
  
  public List<String> getMessagesFromGroup(String groupName) {
	  List<String> messages = new ArrayList<>();
	  String retrieveQuery = "SELECT message, senderID FROM message WHERE msgID in (SELECT msgID FROM messageToUserMap WHERE receiverID=?);";
	  try (Connection connection = connectionManager.getConnection();
		         PreparedStatement preparedStatement = connection.prepareStatement(retrieveQuery, Statement.RETURN_GENERATED_KEYS);) {
		      preparedStatement.setInt(1, groupDAO.getGroupByGroupName(groupName).getGrpID());
		      try (ResultSet resultSet = preparedStatement.executeQuery();) {
		        while (resultSet.next()) {
		          String username = userDAO.getUserByUserID(resultSet.getInt("senderID")).getUsername();
		          String message = resultSet.getString("message");
		          messages.add(username + " " + message);
		        }
		      }
		      return messages;
		    } catch (SQLException e) {
		      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
		    }
  	}
}
