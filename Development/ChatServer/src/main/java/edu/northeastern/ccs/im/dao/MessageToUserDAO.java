package edu.northeastern.ccs.im.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message;

/**
 * Class for the message to user DAO.
 */
public class MessageToUserDAO {

  protected static IConnectionManager connectionManager;
  private static UserDAO userDAO;
  private static MessageToUserDAO messageToUserDAO;
  private static GroupDAO groupDAO;
  private static final String MSG_FIELD = "message";

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
  public void mapMsgIdToReceiverId(Message message, int receiverId, String receiverIP) throws SQLException {
    String insertMSgToUserMap = "INSERT INTO MESSAGETOUSERMAP(MSGID, RECEIVERID, RECEIVERIP) VALUES(?,?,?);";
    // Check if group exists and user exists
    Connection connection = connectionManager.getConnection();
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(insertMSgToUserMap);
      statement.setInt(1, message.getMsgID());
      statement.setInt(2, receiverId);
      statement.setString(3, receiverIP);
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
    String retrieveQuery = "SELECT message, senderID FROM message WHERE msgID in (SELECT msgID FROM messageToUserMap WHERE receiverID=?);";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(retrieveQuery, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, groupDAO.getGroupByGroupName(groupName).getGrpID());
      return getGroupMessages(preparedStatement);
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
    String selectQuery = "SELECT message.chatSenderID, message.senderID, message.message, message.timestamp FROM message JOIN messageToUserMap ON message.msgID = messageToUserMap.msgID WHERE message.senderID = ? AND messageToUserMap.receiverID = ? AND message.msgType = 'PVT' union SELECT message.chatSenderID, message.senderID, message.message, message.timestamp FROM message JOIN messageToUserMap ON message.msgID = messageToUserMap.msgID WHERE message.senderID = ? AND messageToUserMap.receiverID = ? AND message.msgType = 'PVT' order by timestamp;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(selectQuery);
      statement.setInt(1, userDAO.getUserByUsername(sender).getUserID());
      statement.setInt(2, userDAO.getUserByUsername(receiver).getUserID());
      statement.setInt(3, userDAO.getUserByUsername(receiver).getUserID());
      statement.setInt(4, userDAO.getUserByUsername(sender).getUserID());
      return getMessages(statement);
    } finally {
      if (statement != null) {
        statement.close();
      }
      connection.close();
    }
  }


  /**
   * Method to get a list of strings representing notifications for the user about messages received
   * that have not been viewed by the user.
   *
   * @param userID int representing the user #id
   * @return a list of strings representing notifications for the user
   * @throws SQLException if the user #id is not found in the database
   */
  public List<String> getNotifications(int userID) throws SQLException {
    String getNotifs = "SELECT User.username, A.C FROM User JOIN (SELECT M.senderID, COUNT(M.senderID) AS C FROM (SELECT Message.senderID FROM Message JOIN MessageToUserMap ON Message.msgID = MessageToUserMap.msgID WHERE MessageToUserMap.receiverID = ? AND Message.timestamp > (SELECT lastSeen FROM User WHERE userID=?)) M GROUP BY M.senderID) A ON User.userID = A.senderID;";
    String getGroupNotifs = "SELECT T1.Grpname, T1.C From ((SELECT M.MSGID,G.GRPNAME,Count(*) C FROM MESSAGETOUSERMAP M join Groups G on G.GRPID = M.RECEIVERID WHERE RECEIVERID = (SELECT GROUPID FROM GROUPTOUSERMAP GM WHERE USERID = ?) Group By G.GRPNAME) As T1 Join (SELECT M.msgID FROM Message M WHERE M.timestamp > (SELECT lastSeen FROM User WHERE userID = ?)) AS T2 ON T1.msgID = T2.msgID );";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    PreparedStatement preparedStatement2 = null;
    ResultSet resultSet = null;
    ResultSet resultSet2 = null;
    String senderUserName = null;
    int count = 0;
    List<String> notifs = new ArrayList<>();
    try {
      preparedStatement = connection.prepareStatement(getNotifs, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, userID);
      preparedStatement.setInt(2, userID);
      try {
        preparedStatement2 = connection.prepareStatement(getGroupNotifs, Statement.RETURN_GENERATED_KEYS);
        preparedStatement2.setInt(1, userID);
        preparedStatement2.setInt(2, userID);
        try {
          resultSet = preparedStatement.executeQuery();
          while (resultSet.next()) {
            senderUserName = resultSet.getString("username");
            count = resultSet.getInt(2);
            notifs.add(senderUserName + " " + Integer.toString(count));
          }
        } finally {
          if (resultSet != null) {
            resultSet.close();
          }
        }
        try {
          resultSet2 = preparedStatement2.executeQuery();
          while (resultSet2.next()) {
            senderUserName = resultSet2.getString(1);
            count = resultSet2.getInt(2);
            notifs.add(senderUserName + " " + Integer.toString(count));
          }
        } finally {
          if (resultSet2 != null) {
            resultSet2.close();
          }
        }
      } finally {
        if (preparedStatement2 != null) {
          preparedStatement2.close();
        }
      }
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
    return notifs;
  }

  public List<String> getMessagesBetween(String sender, String receiver, String start, String end) throws SQLException {
    String getMessages = "SELECT message.chatSenderID, message.senderID, message.message, message.timestamp FROM message JOIN messageToUserMap ON message.msgID = messageToUserMap.msgID WHERE message.senderID = ? AND messageToUserMap.receiverID = ? AND message.msgType = 'PVT' AND message.timestamp >= ? AND message.timestamp <= ? union SELECT message.chatSenderID, message.senderID, message.message, message.timestamp FROM message JOIN messageToUserMap ON message.msgID = messageToUserMap.msgID WHERE message.senderID = ? AND messageToUserMap.receiverID = ? AND message.msgType = 'PVT' AND message.timestamp >= ? AND message.timestamp <= ? order by timestamp;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement statement = null;
    try {
      statement = connection.prepareStatement(getMessages);
      statement.setInt(1, userDAO.getUserByUsername(sender).getUserID());
      statement.setInt(2, userDAO.getUserByUsername(receiver).getUserID());
      statement.setString(3, start);
      statement.setString(4, end);
      statement.setInt(5, userDAO.getUserByUsername(receiver).getUserID());
      statement.setInt(6, userDAO.getUserByUsername(sender).getUserID());
      statement.setString(7, start);
      statement.setString(8, end);
      return getMessages(statement);
    } finally {
      if (statement != null) {
        statement.close();
      }
      connection.close();
    }
  }

  private List<String> getMessages(PreparedStatement statement) throws SQLException {
    List<String> chat = new ArrayList<>();
    ResultSet resultSet = null;
    try {
      resultSet = statement.executeQuery();
      while (resultSet.next()) {
        int senderId = resultSet.getInt("senderID");
        String msg = resultSet.getString(MSG_FIELD);
        int chatID = resultSet.getInt("chatSenderID");
        chat.add(chatID + " " + userDAO.getUserByUserID(senderId).getUsername() + " " + msg);
      }
    } finally {
      if (resultSet != null) {
        resultSet.close();
      }
    }
    return chat;
  }

  public List<String> getMessagesFromGroupBetween(String groupName, String start, String end) throws SQLException {
    String retrieveQuery = "SELECT message, senderID FROM message WHERE msgID in (SELECT msgID FROM messageToUserMap WHERE receiverID=?) AND timestamp >= ? AND timestamp <= ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(retrieveQuery, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, groupDAO.getGroupByGroupName(groupName).getGrpID());
      preparedStatement.setString(2, start);
      preparedStatement.setString(3, end);
      return getGroupMessages(preparedStatement);
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  private List<String> getGroupMessages(PreparedStatement preparedStatement) throws SQLException {
    List<String> messages = new ArrayList<>();
    ResultSet resultSet = null;
    try {
      resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        String username = userDAO.getUserByUserID(resultSet.getInt("senderID")).getUsername();
        String message = resultSet.getString(MSG_FIELD);
        messages.add(username + " " + message);
      }
    } finally {
      if (resultSet != null) {
        resultSet.close();
      }
    }
    return messages;
  }

  public void mapMsgIdToReceiverThreadId(Message message, int receiverId) throws SQLException {
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

  public void updateReceiverIP(int receiverID, String receiverIP) throws SQLException {
    String updateIP = "UPDATE MESSAGETOUSERMAP SET RECEIVERIP = ? WHERE RECEIVERIP IS NULL AND RECEIVERID = ? AND msgID IN (SELECT msgID FROM MESSAGE WHERE MSGTYPE = 'PVT');";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    try {
      preparedStatement = connection.prepareStatement(updateIP, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, receiverIP);
      preparedStatement.setInt(2, receiverID);
      preparedStatement.executeUpdate();
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }

  public List<String> getGroupMessagesForTappedUser(String username) throws SQLException {
    String getGroupMsges = "SELECT U.USERNAME SenderName, M.SENDERIP, G.grpName ReceiverName, MAP.RECEIVERIP, M.MESSAGE, M.TIMESTAMP FROM Message M JOIN MessageToUserMap MAP ON M.msgID = MAP.msgID JOIN Groups G ON G.grpID = MAP.receiverID JOIN USER U ON U.userID = M.senderID WHERE M.msgType = 'GRP' AND MAP.receiverID IN (SELECT groupID FROM GroupToUserMAP WHERE userID = (SELECT userID FROM User WHERE username = ? AND isTapped = TRUE)) ORDER BY MAP.RECEIVERID;;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    List<String> tappedMsgs = new ArrayList<>();
    try {
      preparedStatement = connection.prepareStatement(getGroupMsges, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, username);
      try {
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
          String senderName = resultSet.getString(1);
          String senderIP = resultSet.getString(2);
          String receiverName = resultSet.getString(3);
          String receiverIP = resultSet.getString(4);
          String message = resultSet.getString(5);
          String timestamp = resultSet.getString(6);
          tappedMsgs.add(senderName + " " + senderIP + " " + receiverName + " " + receiverIP + " " + message + " " + timestamp);
        }
        return tappedMsgs;
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

  public List<String> getTappedMessagesSender(String username) throws SQLException {
    String getMessages = "SELECT U.USERNAME ReceiverName, M.SENDERIP, MAP.RECEIVERIP, M.MESSAGE, M.TIMESTAMP FROM Message M JOIN MessageToUserMap MAP ON M.msgID = MAP.msgID JOIN USER U ON U.USERID = MAP.receiverID WHERE M.senderID = (SELECT userID FROM User WHERE username = ? AND isTapped = TRUE) ORDER BY MAP.RECEIVERID;";
    List<String> messages = new ArrayList<>();
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(getMessages, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, username);
      try {
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
          messages.add(resultSet.getString("ReceiverName") + " " + resultSet.getString(2) + " " + resultSet.getString(3) + " " + resultSet.getString(4) + " " + resultSet.getString(5));
        }
        return messages;
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

  public List<String> getTappedMessagesReceiver(String username) throws SQLException {
    String getMessages = "SELECT U.USERNAME SenderName, M.SENDERIP, MAP.RECEIVERIP, M.MESSAGE, M.TIMESTAMP FROM Message M JOIN MessageToUserMap MAP ON M.msgID = MAP.msgID JOIN USER U ON U.USERID = M.senderID WHERE MAP.receiverID = (SELECT userID FROM User WHERE username = ? AND isTapped = TRUE) ORDER BY M.SENDERID;";
    List<String> messages = new ArrayList<>();
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(getMessages, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setString(1, username);
      try {
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
          messages.add(resultSet.getString(1) + " " + resultSet.getString(2) + " " + resultSet.getString(3) + " " + resultSet.getString(4) + " " + resultSet.getString(5));
        }
        return messages;
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

  public List<String> getMessageThread(int senderID, int receiverID, int chatMsgID) throws SQLException {
    String getChat = "SELECT User.username, T.* FROM User JOIN (SELECT M.msgID, M.senderID, M.message, M.chatSenderID, M.replyID, MAP.receiverID FROM Message M JOIN MessageToUserMap MAP ON M.msgID = MAP.msgID WHERE (senderID = ? AND receiverID = ?) OR (senderID = ? AND receiverID = ?) ORDER BY chatSenderID DESC) AS T  ON T.senderID = User.userID;";
    List<String> messages = new ArrayList<>();
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(getChat, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, senderID);
      preparedStatement.setInt(2, receiverID);
      preparedStatement.setInt(3, receiverID);
      preparedStatement.setInt(4, senderID);
      try {
        resultSet = preparedStatement.executeQuery();
        int replyID = -1;
        while (resultSet.next()) {
          if (resultSet.getInt("chatSenderID") == chatMsgID || resultSet.getInt("msgID") == replyID) {
            messages.add(resultSet.getString("username") + " " + resultSet.getString(MSG_FIELD));
            replyID = resultSet.getInt("replyID");
          }
        }
        Collections.reverse(messages);
        return messages;
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

  public int getMessageIDFromChatID(int senderID, int receiverID, int chatMsgID) throws SQLException {
    String getMessageID = "SELECT M.msgID FROM Message M JOIN MessageToUserMap MAP ON M.msgID = MAP.msgID WHERE ((senderID = ? AND receiverID = ?) OR (senderID = ? AND receiverID = ?)) AND chatSenderID = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    int messageID = -1;
    try {
      preparedStatement = connection.prepareStatement(getMessageID, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, senderID);
      preparedStatement.setInt(2, receiverID);
      preparedStatement.setInt(3, receiverID);
      preparedStatement.setInt(4, senderID);
      preparedStatement.setInt(5, chatMsgID);
      try {
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          messageID = resultSet.getInt(1);
        }
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
    return messageID;
  }

  public String getMessageByChatID(int senderID, int receiverID, int chatID, Message.MsgType msgType) throws SQLException {
    String getMessage = "SELECT M.MESSAGE FROM MESSAGE M JOIN MESSAGETOUSERMAP MAP ON M.MSGID = MAP.MSGID WHERE ((M.SENDERID = ? AND MAP.RECEIVERID = ?) OR (MAP.RECEIVERID = ? AND M.SENDERID = ?)) AND M.MSGTYPE = ? AND M.CHATSENDERID = ?;";
    Connection connection = connectionManager.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      preparedStatement = connection.prepareStatement(getMessage, Statement.RETURN_GENERATED_KEYS);
      preparedStatement.setInt(1, senderID);
      preparedStatement.setInt(2, receiverID);
      preparedStatement.setInt(3, senderID);
      preparedStatement.setInt(4, receiverID);
      preparedStatement.setString(5, msgType.toString());
      preparedStatement.setInt(6, chatID);
      try {
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          return resultSet.getString(1);
        } else {
          throw new DatabaseConnectionException("No message exists with this ID");
        }
      } finally {
        if (resultSet != null) {
          resultSet.close();
        }
        connection.close();
      }
    } finally {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      connection.close();
    }
  }
}