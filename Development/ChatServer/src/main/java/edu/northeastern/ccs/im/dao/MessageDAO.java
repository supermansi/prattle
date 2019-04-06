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
    String insertMessage = "INSERT INTO Message(msgType, senderID, message, timestamp, senderIP) VALUES(?,?,?,?,?);";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(insertMessage, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, message.getMsgType().name());
      preparedStatement.setInt(2, message.getSenderID());
      preparedStatement.setString(3, message.getMessageText());
      preparedStatement.setString(4, message.getTimestamp());
      preparedStatement.setString(5, message.getSenderIP());
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

  /**
   *Method to get the time stamp of the last message sent between two given users.
   *
   * @param senderID int representing the id of the message sender
   * @param receiverID int representing the id of the message receiver
   * @return String representing the timestamp of the last message
   * @throws SQLException if sender or receiver id are not found
   */
  public String getTimeStampOfLastMessage(int senderID, int receiverID) throws SQLException {
    String getTimeStamp = "SELECT timestamp FROM Message JOIN MessageToUserMap on Message.msgID = MessageToUserMap.msgID WHERE message.senderID=? AND messagetousermap.receiverID=? ORDER BY timestamp DESC LIMIT 1;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    String timestamp = null;
    try {
      preparedStatement = connection.prepareStatement(getTimeStamp, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, senderID);
      preparedStatement.setInt(2, receiverID);
      try{
        resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
          timestamp = resultSet.getString(1);
        }
      }
      finally {
        if(resultSet != null) {
          resultSet.close();
        }
      }
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
    return timestamp;
  }

  /**
   *Method to get the #id of the last message sent between two given users.
   *
   * @param senderID int representing the id of the message sender
   * @param receiverID int representing the id of the message receiver
   * @return Int representing the #id of the last message
   * @throws SQLException if sender or receiver id are not found
   */
  public int getIdOfLastMessage(int senderID, int receiverID) throws SQLException {
    String getTimeStamp = "SELECT message.msgID FROM Message JOIN MessageToUserMap on Message.msgID = MessageToUserMap.msgID WHERE message.senderID=? AND messagetousermap.receiverID=? ORDER BY timestamp DESC LIMIT 1;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    int msgID = 0;
    try {
      preparedStatement = connection.prepareStatement(getTimeStamp, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, senderID);
      preparedStatement.setInt(2, receiverID);
      try{
        resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
          msgID = resultSet.getInt(1);
        }
      }
      finally {
        if(resultSet != null) {
          resultSet.close();
        }
      }
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
    return msgID;
  }

  /**
   * Method to delete a message by its #id.
   *
   * @param tableName String representing the name of the database table to search
   * @param msgID int representing the #id for the message to delete
   * @throws SQLException if the table name of message id cannot be found
   */
  public void deleteMessageByID(String tableName, int msgID) throws SQLException {
    String deleteMessage = "DELETE FROM " + tableName +" WHERE msgID = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(deleteMessage);
      preparedStatement.setInt(1, msgID);
      preparedStatement.executeUpdate();
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }
}
