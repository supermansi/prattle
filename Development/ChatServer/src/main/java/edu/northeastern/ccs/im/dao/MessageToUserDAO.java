package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ccs.im.model.Message;

/**
 * Class for the message to user DAO.
 */
public class MessageToUserDAO {

  protected static IConnectionManager connectionManager;
  private static UserDAO userDAO;
  private static MessageToUserDAO messageToUserDAO;
  private static GroupDAO groupDAO;

  /**
   * Private constructor for the message to user DAO.
   */
  private MessageToUserDAO() {
    //empty private constructor for singleton
  }

  /**
   * Method to get the singleton instance of the message to user DAO.
   *
   * @return the instance of the message to user DAO
   */
  public static MessageToUserDAO getInstance() {
    if (messageToUserDAO == null) {
      connectionManager = new ConnectionManager();
      messageToUserDAO = new MessageToUserDAO();
      userDAO = UserDAO.getInstance();
      groupDAO = GroupDAO.getInstance();
    }
    return messageToUserDAO;
  }

  /**
   * Method to map a message to the receiverID.
   *
   * @param message    message to map
   * @param receiverId receiverID to map
   */
  public void mapMsgIdToReceiverId(Message message, int receiverId) throws SQLException {
    String insertMSgToUserMap = "INSERT INTO MESSAGETOUSERMAP(MSGID, RECEIVERID) VALUES(?,?);";
    // Check if group exists and user exists
    Connection connection = connectionManager.getConnection();
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(insertMSgToUserMap);
      statement.setInt(1, message.getMsgID());
      statement.setInt(2, receiverId);
      statement.executeUpdate();
    } finally {
      if (statement != null) {
        statement.close();
      }
      connection.close();
    }
  }

  /**
   * Method to get messages sent to a group.
   *
   * @param groupName group name to retrieve messages from
   * @return a list of strings that contain the messages sent to a group
   */
  public List<String> getMessagesFromGroup(String groupName) throws SQLException {
    List<String> messages = new ArrayList<>();
    String retrieveQuery = "SELECT message, senderID FROM message WHERE msgID in (SELECT msgID FROM messageToUserMap WHERE receiverID=?);";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(retrieveQuery, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, groupDAO.getGroupByGroupName(groupName).getGrpID());
      ResultSet resultSet = null;
      try {
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
          String username = userDAO.getUserByUserID(resultSet.getInt("senderID")).getUsername();
          String message = resultSet.getString("message");
          messages.add(username + " " + message);
        }
      } finally {
        if(resultSet!=null){
          resultSet.close();
        }
      }
      return messages;
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  /**
   * Method to retrieve messages between users.
   *
   * @param sender   sender of the message
   * @param receiver receiver of the message
   * @return list of strings representing the messages
   */
  public List<String> retrieveUserMsg(String sender, String receiver) throws SQLException {
    String selectQuery = "SELECT message.senderID, message.message, message.timestamp FROM message JOIN messageToUserMap ON message.msgID = messageToUserMap.msgID WHERE message.senderID = ? AND messageToUserMap.receiverID = ? AND message.msgType = 'PVT' union SELECT message.senderID, message.message, message.timestamp FROM message JOIN messageToUserMap ON message.msgID = messageToUserMap.msgID WHERE message.senderID = ? AND messageToUserMap.receiverID = ? AND message.msgType = 'PVT' order by timestamp;";
    List<String> chat = new ArrayList<>();
    Connection connection = connectionManager.getConnection();
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(selectQuery);
      statement.setInt(1, userDAO.getUserByUsername(sender).getUserID());
      statement.setInt(2, userDAO.getUserByUsername(receiver).getUserID());
      statement.setInt(3, userDAO.getUserByUsername(receiver).getUserID());
      statement.setInt(4, userDAO.getUserByUsername(sender).getUserID());
      ResultSet resultSet = null;
      try {
        resultSet = statement.executeQuery();
        while (resultSet.next()) {
          int senderId = resultSet.getInt("senderID");
          String msg = resultSet.getString("message");
          chat.add(userDAO.getUserByUserID(senderId).getUsername() + " " + msg);
        }
      } finally {
        if(resultSet!=null){
          resultSet.close();
        }
      }
      return chat;
    } finally {
      if (statement != null) {
        statement.close();
      }
      connection.close();
    }
  }
}
