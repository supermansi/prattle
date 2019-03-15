package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

  public List<String> retrieveUserMsg(String sender, String receiver) {
    String selectQuery = "SELECT message.senderID, message.message, message.timestamp FROM message JOIN messageToUserMap ON message.msgID = messageToUserMap.msgID WHERE message.senderID = ? AND messageToUserMap.receiverID = ? AND message.msgType = 'PVT' union SELECT message.senderID, message.message, message.timestamp FROM message JOIN messageToUserMap ON message.msgID = messageToUserMap.msgID WHERE message.senderID = ? AND messageToUserMap.receiverID = ? AND message.msgType = 'PVT' order by timestamp;";
    List<String> chat = new ArrayList<>();
    try(Connection connection = connectionManager.getConnection();
        PreparedStatement statement = connection.prepareStatement(selectQuery);) {
        statement.setInt(1,userDAO.getUserByUsername(sender).getUserID());
        statement.setInt(2,userDAO.getUserByUsername(receiver).getUserID());
        statement.setInt(3,userDAO.getUserByUsername(receiver).getUserID());
        statement.setInt(4,userDAO.getUserByUsername(sender).getUserID());
      try (ResultSet resultSet = statement.executeQuery();) {
        while (resultSet.next()) {
          int senderId = resultSet.getInt("senderID");
          String msg = resultSet.getString("message");
//          String ts = resultSet.getString("timestamp");
          chat.add(userDAO.getUserByUserID(senderId).getUsername() + " " + msg);
        }
      }
      return chat;
    } catch (SQLException e) {
      throw new DatabaseConnectionException(e.getMessage() + "\n" + e.getStackTrace());
    }
  }
}
