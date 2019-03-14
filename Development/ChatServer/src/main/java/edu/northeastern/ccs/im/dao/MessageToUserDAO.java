package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
