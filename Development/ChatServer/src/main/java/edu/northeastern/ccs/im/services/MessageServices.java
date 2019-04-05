package edu.northeastern.ccs.im.services;

import org.apache.commons.collections4.map.MultiKeyMap;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import edu.northeastern.ccs.im.dao.GroupDAO;
import edu.northeastern.ccs.im.dao.GroupToUserDAO;
import edu.northeastern.ccs.im.dao.MessageDAO;
import edu.northeastern.ccs.im.dao.MessageToUserDAO;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message;

/**
 * Class for the Message services.
 */
public class MessageServices {

  private static GroupDAO groupDAO;
  private static UserDAO userDAO;
  private static MessageDAO messageDAO;
  private static MessageToUserDAO messageUserDAO;

  /**
   * Private constructor for the message service instance.
   */
  private MessageServices() {
    // empty private constructor
  }

  static {
    groupDAO = GroupDAO.getInstance();
    userDAO = UserDAO.getInstance();
    messageDAO = MessageDAO.getInstance();
    messageUserDAO = MessageToUserDAO.getInstance();
  }

  /**
   * Method to add a message to the database.
   *
   * @param msgType  msgType describing type of message
   * @param sender   sender name
   * @param receiver receiver name
<<<<<<< HEAD
   * @param message  message text
=======
   * @param chatID
>>>>>>> 2fd6bee133990c596ef9a43aa22ec2bc32884081
   * @param senderIP
   * @param receiverIP
   * @param isSecret
   * @return true if message is added to database, false otherwise
   */
  public static boolean addMessage(Message.MsgType msgType, String sender, String receiver, String message, int chatID, String senderIP, String receiverIP, boolean isSecret) throws SQLException {
    if (msgType == Message.MsgType.PVT) {
      if (userDAO.isUserExists(receiver)) {
        int senderID = userDAO.getUserByUsername(sender).getUserID();
        Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
        messageDAO.createMessage(sendMessage);
        messageUserDAO.mapMsgIdToReceiverId(sendMessage, userDAO.getUserByUsername(receiver).getUserID());
        return true;
      }
    } else if (msgType == Message.MsgType.GRP) {
      if (groupDAO.checkGroupExists(receiver)) {
        int senderID = userDAO.getUserByUsername(sender).getUserID();
        Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
        messageDAO.createMessage(sendMessage);
        messageUserDAO.mapMsgIdToReceiverId(sendMessage, groupDAO.getGroupByGroupName(receiver).getGrpID());
        return true;
      }
    } else {
      throw new DatabaseConnectionException("This is not a valid handle.");
    }
    return false;
  }

  /**
   * Method to return a list of messages between users.
   *
   * @param sender   sender's user name
   * @param receiver receiver's user name
   * @return a list of strings with the message text sent between users
   */
  public static List<String> retrieveUserMessages(String sender, String receiver) throws SQLException {
    return messageUserDAO.retrieveUserMsg(sender, receiver);
  }

  /**
   * Method to return a list of messages sent to a group.
   *
   * @param groupName group name
   * @return a list of strings with the message text to the group
   */
  public static List<String> retrieveGroupMessages(String groupName) throws SQLException {
    return messageUserDAO.getMessagesFromGroup(groupName);
  }

  public static boolean recallMessage(String sender, String receiver) throws SQLException {
    String userLastSeen = userDAO.getLastSeen(receiver);
    int senderID = userDAO.getUserByUsername(sender).getUserID();
    int receiverID = userDAO.getUserByUsername(receiver).getUserID();
    String messageLastSeen = messageDAO.getTimeStampOfLastMessage(senderID, receiverID);
    boolean flag = false;
    if (Long.parseLong(userLastSeen) < Long.parseLong(messageLastSeen)) {
      int msgID = messageDAO.getIdOfLastMessage(senderID, receiverID);
      messageDAO.deleteMessageByID("Message", msgID);
      messageDAO.deleteMessageByID("MessageToUserMap", msgID);
      flag = true;
    }
    return flag;
  }

  public static List<String> getPushNotifications(String username) throws SQLException {
    return messageUserDAO.getNotifications(userDAO.getUserByUsername(username).getUserID());
  }

  public static List<String> getMessagesBetween(String sender, String receiver, String startDate, String endDate) throws SQLException {
    return messageUserDAO.getMessagesBetween(sender, receiver, startDate, endDate);
  }

  public static List<String> getGroupMessagesBetween(String groupName, String start, String end) throws SQLException {
    return messageUserDAO.getMessagesFromGroupBetween(groupName, start, end);
  }

  public static void postMessageToThread(Message.MsgType msgType, String sender, String receiverThread, String message) throws SQLException {
    if (groupDAO.checkGroupExists(receiverThread) && groupDAO.getGroupByGroupName(receiverThread).isThread()) {
      if (msgType == Message.MsgType.TRD) {
        Message sendMessage = new Message(msgType, userDAO.getUserByUsername(sender).getUserID(), message, Long.toString(System.currentTimeMillis()));
        messageDAO.addMessageToThread(sendMessage);
        messageUserDAO.mapMsgIdToReceiverThreadId(sendMessage, groupDAO.getGroupByGroupName(receiverThread).getGrpID());
      }
    } else {
      throw new DatabaseConnectionException("No such thread exists");
    }
  }

  public static ConcurrentMap<String,Integer> getChatIDForGroups(){
    return new ConcurrentHashMap<>();
  }

  public static MultiKeyMap getChatIDForUsers(){
    return new MultiKeyMap();
  }

}
