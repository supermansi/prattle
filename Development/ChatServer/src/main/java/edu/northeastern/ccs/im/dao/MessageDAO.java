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

/**
 * Class for the message DAO.
 */
public class MessageDAO {

  protected static IConnectionManager connectionManager;
  private static MessageDAO messageDAO;

  /**
   * Private constructor for message DAO.
   */
  private MessageDAO() {
    //empty private constructor for singleton
  }

  /**
   * Method to return the singleton instance of the message DAO.
   *
   * @return the instance of message DAO
   */
  public static MessageDAO getInstance() {
    if (messageDAO == null) {
      connectionManager = new ConnectionManager();
      messageDAO = new MessageDAO();
    }
    return messageDAO;
  }

  /**
   * Method to store a message in the database.
   *
   * @param message message to be stored in the databse
   * @return a message model object
   */
  public Message createMessage(Message message) throws SQLException {
    String insertMessage = "INSERT INTO Message(msgType, senderID, message, timestamp) VALUES(?,?,?,?);";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(insertMessage, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, message.getMsgType().name());
      preparedStatement.setInt(2, message.getSenderID());
      preparedStatement.setString(3, message.getMessageText());
      preparedStatement.setString(4, message.getTimestamp());
      preparedStatement.executeUpdate();
      try {
        resultSet = preparedStatement.getGeneratedKeys();
        if (resultSet.next()) {
          int msgID = resultSet.getInt(1);
          message.setMsgID(msgID);
        }
        return message;

      } finally {
        if (resultSet != null) {
          resultSet.close();
        }
      }
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }

  }

  /**
   * Method to retrieve a message from the database by the #ID.
   *
   * @param msgID int representing the message #ID
   * @return message model object
   */
  public Message getMessageByID(int msgID) throws SQLException {
    String getMessage = "SELECT * FROM Message WHERE msgID = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(getMessage, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, msgID);
      Message message;
      ResultSet resultSet = null;
      try {
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          Message.MsgType msgType = Message.MsgType.valueOf(resultSet.getString("msgType"));
          int senderID = resultSet.getInt("senderID");
          String context = resultSet.getString("message");
          String timestamp = resultSet.getString("timestamp");
          message = new Message(msgID, msgType, senderID, context, timestamp);
        } else {
          throw new DatabaseConnectionException("Message not found.");
        }
      } finally {
        if (resultSet != null) {
          resultSet.close();
        }
      }
      return message;
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }

      connection.close();
    }
  }

  /**
   * Method to return a list of messages that have the same sender #ID.
   *
   * @param senderID int representing the sender #ID.
   * @return a list of messages matching the sender #ID
   */
  public List<Message> getMessagesBySender(int senderID) throws SQLException {
    List<Message> messages = new ArrayList<>();
    String listMessages = "SELECT * FROM Message WHERE senderID=?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(listMessages, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, senderID);
      try {
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
          int msgID = resultSet.getInt("msgID");
          messages.add(getMessageByID(msgID));
        }
      } finally {
        if (resultSet != null) {
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
}
