package edu.northeastern.ccs.im.services;

import edu.northeastern.ccs.im.dao.*;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message;

/**
 * Class for the Message services.
 */
public class MessageServices {

  private static GroupDAO groupDAO;
  private static GroupToUserDAO groupUserDAO;
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
    groupUserDAO = GroupToUserDAO.getInstance();
    userDAO = UserDAO.getInstance();
    messageDAO = MessageDAO.getInstance();
    messageUserDAO = MessageToUserDAO.getInstance();
  }

  private static boolean addMessage(Message sendMessage, String receiver, String receiverIP) throws SQLException {
    if(sendMessage.getMsgType() == Message.MsgType.PVT) {
      if(userDAO.isUserExists(receiver)){
        sendMessage = messageDAO.createMessage(sendMessage);
        messageUserDAO.mapMsgIdToReceiverId(sendMessage, userDAO.getUserByUsername(receiver).getUserID(), receiverIP);
        return true;
      }
    } else if(sendMessage.getMsgType() == Message.MsgType.GRP) {
      if (groupDAO.checkGroupExists(receiver)) {
        sendMessage = messageDAO.createMessage(sendMessage);
        messageUserDAO.mapMsgIdToReceiverId(sendMessage, groupDAO.getGroupByGroupName(receiver).getGrpID(), receiverIP);
        return true;
      }
    } else {
      throw new DatabaseConnectionException("This is not a valid handle.");
    }
    return false;
  }

  /**
   * Method to add a message to the database.
   *
   * @param msgType  msgType describing type of message
   * @param sender   sender name
   * @param receiver receiver name
   * @param message  message text
   * @param chatID
   * @param SenderReceiverIPMap
   * @return true if message is added to database, false otherwise
   */
  public static boolean addMessage(Message.MsgType msgType, String sender, String receiver, String message, int chatID, Map<Message.IPType, String> SenderReceiverIPMap) throws SQLException {
    int senderID = userDAO.getUserByUsername(sender).getUserID();
    Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
    sendMessage.setSenderIP(SenderReceiverIPMap.get(Message.IPType.SENDERIP));
    sendMessage.setChatSenderID(chatID);
    sendMessage.setSecret(false);
    sendMessage.setReplyID(-1);
    return addMessage(sendMessage, receiver, SenderReceiverIPMap.get(Message.IPType.RECEIVERIP));
  }

  public static boolean addMessage(Message.MsgType msgType, String sender, String receiver, String message, int chatID, Map<Message.IPType, String> SenderReceiverIPMap, boolean isSecret) throws SQLException {
    int senderID = userDAO.getUserByUsername(sender).getUserID();
    Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
    sendMessage.setSenderIP(SenderReceiverIPMap.get(Message.IPType.SENDERIP));
    sendMessage.setChatSenderID(chatID);
    sendMessage.setSecret(isSecret);
    sendMessage.setReplyID(-1);
    return addMessage(sendMessage, receiver, SenderReceiverIPMap.get(Message.IPType.RECEIVERIP));
  }

  public static boolean addMessage(Message.MsgType msgType, String sender, String receiver, String message, int chatID, Map<Message.IPType, String> SenderReceiverIPMap, int replyID) throws SQLException {
    int senderID = userDAO.getUserByUsername(sender).getUserID();
    int receiverID = userDAO.getUserByUsername(receiver).getUserID();
    Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
    sendMessage.setSenderIP(SenderReceiverIPMap.get(Message.IPType.SENDERIP));
    sendMessage.setChatSenderID(chatID);
    sendMessage.setSecret(false);
    // To Do: find the msdID and populate that instead of the replyID
    int replyMessageID = messageUserDAO.getMessageFromChatID(senderID, receiverID, chatID);
    sendMessage.setReplyID(replyMessageID);
    return addMessage(sendMessage, receiver, SenderReceiverIPMap.get(Message.IPType.RECEIVERIP));
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
    List<String> notifications = new ArrayList<>();
    notifications.addAll(messageUserDAO.getNotifications(userDAO.getUserByUsername(username).getUserID()));
    notifications.addAll(groupUserDAO.getFollowThreadNotification(username));
    return notifications;
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

  public static MultiKeyMap getChatIDForUsers() throws SQLException {
    return messageDAO.getChatIDForUsers();
  }

  public static void updateReceiverIP(String receiverName, String receiverIP) throws SQLException {
    messageUserDAO.updateReceiverIP(userDAO.getUserByUsername(receiverName).getUserID(),receiverIP);
  }

  public static List<String> getAllDataForCIA(String username) throws SQLException {
    List<String> ciaData = new ArrayList<>();
    ciaData.addAll(messageUserDAO.getGroupMessagesForTappedUser(username));
    ciaData.addAll(messageUserDAO.getTappedMessagesReceiver(username));
    ciaData.addAll(messageUserDAO.getTappedMessagesSender(username));
    return ciaData;
  }

  public static boolean isSecret(String sender, String receiver, int chatID) throws SQLException {
    int senderID = userDAO.getUserByUsername(sender).getUserID();
    int receiverID = userDAO.getUserByUsername(receiver).getUserID();
    return messageDAO.isSecret(senderID, receiverID, chatID);
  }

  public static List<String> getReplyThread(String user1, String user2, int chatID) throws SQLException {
    int user1ID = userDAO.getUserByUsername(user1).getUserID();
    int user2ID = userDAO.getUserByUsername(user2).getUserID();
    return messageUserDAO.getMessageThread(user1ID, user2ID, chatID);
  }

  public static String getMessageForForwarding(String senderName, String receiverName, int chatID, Message.MsgType messageType) throws SQLException {
    int receiverID;
    if (messageType == Message.MsgType.GRP || messageType == Message.MsgType.TRD) {
      receiverID = groupDAO.getGroupByGroupName(receiverName).getGrpID();
    } else {
      receiverID = userDAO.getUserByUsername(receiverName).getUserID();
    }
    return messageUserDAO.getMessageByChatID(userDAO.getUserByUsername(senderName).getUserID(), receiverID, chatID, messageType);
  }
}