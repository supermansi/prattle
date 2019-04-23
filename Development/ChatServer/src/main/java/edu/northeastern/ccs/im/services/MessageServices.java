/** Copyright (c) 2019 Rohan Gori, Aditi Kacheria, Mansi Jain, Joshua Dick. All rights reserved.*/
package edu.northeastern.ccs.im.services;

import edu.northeastern.ccs.im.dao.*;

import org.apache.commons.collections4.map.MultiKeyMap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

  static {
    groupDAO = GroupDAO.getInstance();
    groupUserDAO = GroupToUserDAO.getInstance();
    userDAO = UserDAO.getInstance();
    messageDAO = MessageDAO.getInstance();
    messageUserDAO = MessageToUserDAO.getInstance();
  }

  /**
   * Private constructor for the message service instance.
   */
  private MessageServices() {
    // empty private constructor
  }

  private static boolean addMessage(Message sendMessage, String receiver, String receiverIP) throws SQLException {
    if (sendMessage.getMsgType() == Message.MsgType.PVT) {
      if (userDAO.isUserExists(receiver)) {
        sendMessage = messageDAO.createMessage(sendMessage);
        messageUserDAO.mapMsgIdToReceiverId(sendMessage, userDAO.getUserByUsername(receiver).getUserID(), receiverIP);
        return true;
      }
    } else if (sendMessage.getMsgType() == Message.MsgType.GRP) {
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
   * @return true if message is added to database, false otherwise
   */
  public static boolean addMessage(Message.MsgType msgType, String sender, String receiver, String message, int chatID, Map<Message.IPType, String> senderReceiverIPMap) throws SQLException {
    int senderID = userDAO.getUserByUsername(sender).getUserID();
    Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
    sendMessage.setSenderIP(senderReceiverIPMap.get(Message.IPType.SENDERIP));
    sendMessage.setChatSenderID(chatID);
    sendMessage.setSecret(false);
    sendMessage.setReplyID(-1);
    return addMessage(sendMessage, receiver, senderReceiverIPMap.get(Message.IPType.RECEIVERIP));
  }

  /**
   * Method to add a message to the database.
   *
   * @param msgType the type of message
   * @param sender the sender of the message
   * @param receiver the receiver of the message
   * @param message the message
   * @param chatID the chat #id
   * @param senderReceiverIPMap the sender to receiver ip mapping
   * @param isSecret bool if message is secret
   * @return true if added, false otherwise
   * @throws SQLException if the database cannot establish a connection
   */
  public static boolean addMessage(Message.MsgType msgType, String sender, String receiver, String message, int chatID, Map<Message.IPType, String> senderReceiverIPMap, boolean isSecret) throws SQLException {
    int senderID = userDAO.getUserByUsername(sender).getUserID();
    Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
    sendMessage.setSenderIP(senderReceiverIPMap.get(Message.IPType.SENDERIP));
    sendMessage.setChatSenderID(chatID);
    sendMessage.setSecret(isSecret);
    sendMessage.setReplyID(-1);
    return addMessage(sendMessage, receiver, senderReceiverIPMap.get(Message.IPType.RECEIVERIP));
  }

  /**
   * Method to add a message to the database.
   *
   * @param msgType the type of message
   * @param sender the sender of the message
   * @param receiver the receiver of the message
   * @param message the message
   * @param chatID the chat #id
   * @param senderReceiverIPMap the sender to receiver ip mapping
   * @param replyID reply #id of the message
   * @return true if added, false otherwise
   * @throws SQLException if the database cannot establish a connection
   */
  public static boolean addMessage(Message.MsgType msgType, String sender, String receiver, String message, int chatID, Map<Message.IPType, String> senderReceiverIPMap, int replyID) throws SQLException {
    int senderID = userDAO.getUserByUsername(sender).getUserID();
    int receiverID = userDAO.getUserByUsername(receiver).getUserID();
    Message sendMessage = new Message(msgType, senderID, message, Long.toString(System.currentTimeMillis()));
    sendMessage.setSenderIP(senderReceiverIPMap.get(Message.IPType.SENDERIP));
    sendMessage.setChatSenderID(chatID);
    sendMessage.setSecret(false);
    int replyMessageID = messageUserDAO.getMessageIDFromChatID(senderID, receiverID, replyID);
    sendMessage.setReplyID(replyMessageID);
    return addMessage(sendMessage, receiver, senderReceiverIPMap.get(Message.IPType.RECEIVERIP));
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

  /**
   * Method to recall the last message sent if the recipient has not yet viewed it.
   *
   * @param sender the sender of the message
   * @param receiver the receiver of the message
   * @return true if message is recalled, false otherwise
   * @throws SQLException if the database cannot establish a connection
   */
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

  /**
   * Method to get a list of push notifications for a user.
   *
   * @param username the user to search for
   * @return  a list of push notifications as strings
   * @throws SQLException if the database cannot establish a connection
   */
  public static List<String> getPushNotifications(String username) throws SQLException {
    List<String> notifications = new ArrayList<>();
    notifications.addAll(messageUserDAO.getNotifications(userDAO.getUserByUsername(username).getUserID()));
    notifications.addAll(groupUserDAO.getFollowThreadNotification(username));
    return notifications;
  }

  /**
   * Method to get messages between two give dates.
   *
   * @param sender the sender of the message
   * @param receiver the receiver of the message
   * @param startDate the start date
   * @param endDate the end date
   * @return a list of message between the given dates
   * @throws SQLException if the database cannot establish a connection
   */
  public static List<String> getMessagesBetween(String sender, String receiver, String startDate, String endDate) throws SQLException {
    return messageUserDAO.getMessagesBetween(sender, receiver, startDate, endDate);
  }

  /**
   * Method to get messages between two give dates.
   *
   * @param groupName the group to search for
   * @param start the start date
   * @param end the end date
   * @return a list of message between the given dates
   * @throws SQLException if the database cannot establish a connection
   */
  public static List<String> getGroupMessagesBetween(String groupName, String start, String end) throws SQLException {
    return messageUserDAO.getMessagesFromGroupBetween(groupName, start, end);
  }

  /**
   * Method to post a message to a thread.
   *
   * @param msgType the type of message
   * @param sender the sender of the message
   * @param receiverThread the receiver of the message
   * @param message the message
   * @throws SQLException if the database cannot establish a connection
   */
  public static void postMessageToThread(Message.MsgType msgType, String sender, String receiverThread, String message) throws SQLException {
    if (groupDAO.checkGroupExists(receiverThread) && groupDAO.getGroupByGroupName(receiverThread).isThread()) {
      if (msgType == Message.MsgType.TRD) {
        Message sendMessage = new Message(msgType, userDAO.getUserByUsername(sender).getUserID(), message, Long.toString(System.currentTimeMillis()));
        messageDAO.addMessageToThread(sendMessage);
        messageUserDAO.mapMsgIdToReceiverThreadId(sendMessage, groupDAO.getGroupByGroupName(receiverThread).getGrpID());
      } else {
        throw new DatabaseConnectionException("Message is not of type Thread");
      }
    } else {
      throw new DatabaseConnectionException("No such thread exists");
    }
  }

  /**
   * Method to get a map of chat ids to users.
   *
   * @return a map of chat ids to users
   * @throws SQLException if the database cannot establish a connection
   */
  public static MultiKeyMap getChatIDForUsers() throws SQLException {
    return messageDAO.getChatIDForUsers();
  }

  /**
   * Method to update the receiver ip.
   *
   * @param receiverName the receiver name
   * @param receiverIP the receiver ip
   * @throws SQLException if the database cannot establish a connection
   */
  public static void updateReceiverIP(String receiverName, String receiverIP) throws SQLException {
    messageUserDAO.updateReceiverIP(userDAO.getUserByUsername(receiverName).getUserID(), receiverIP);
  }

  /**
   * Method to get all wiretap data for the CIA user.
   *
   * @param username the user to search for
   * @return a list of wiretap data on the given user
   * @throws SQLException if the database cannot establish a connection
   */
  public static List<String> getAllDataForCIA(String username) throws SQLException {
    List<String> ciaData = new ArrayList<>();
    ciaData.addAll(messageUserDAO.getGroupMessagesForTappedUser(username));
    ciaData.addAll(messageUserDAO.getTappedMessagesReceiver(username));
    ciaData.addAll(messageUserDAO.getTappedMessagesSender(username));
    return ciaData;
  }

  /**
   * Method to determine if a message is a secret message,
   *
   * @param sender teh sender
   * @param receiver the receiver
   * @param chatID the chat #id
   * @return true if the message is secret, false otherwise
   * @throws SQLException if the database cannot establish a connection
   */
  public static boolean isSecret(String sender, String receiver, int chatID) throws SQLException {
    int senderID = userDAO.getUserByUsername(sender).getUserID();
    int receiverID = userDAO.getUserByUsername(receiver).getUserID();
    return messageDAO.isSecret(senderID, receiverID, chatID);
  }

  /**
   * Method to get a reply chain of messages between two users.
   *
   * @param user1 the first user
   * @param user2 the second user
   * @param chatID the chat #id
   * @return a list of reply chain messages
   * @throws SQLException if the database cannot establish a connection
   */
  public static List<String> getReplyThread(String user1, String user2, int chatID) throws SQLException {
    int user1ID = userDAO.getUserByUsername(user1).getUserID();
    int user2ID = userDAO.getUserByUsername(user2).getUserID();
    return messageUserDAO.getMessageThread(user1ID, user2ID, chatID);
  }

  /**
   * Method to get a message to be forwarded to another user.
   *
   * @param senderName the sender name
   * @param receiverName the receiver name
   * @param chatID the chat #id
   * @param messageType the type of message
   * @return true if successful, false otherwise
   * @throws SQLException if the database cannot establish a connection
   */
  public static String getMessageForForwarding(String senderName, String receiverName, int chatID, Message.MsgType messageType) throws SQLException {
    int receiverID;
    if (messageType == Message.MsgType.GRP) {
      receiverID = groupDAO.getGroupByGroupName(receiverName).getGrpID();
    } else if (messageType == Message.MsgType.PVT) {
      receiverID = userDAO.getUserByUsername(receiverName).getUserID();
    } else {
      throw new DatabaseConnectionException("Such type of Message cannot be forwarded");
    }
    return messageUserDAO.getMessageByChatID(userDAO.getUserByUsername(senderName).getUserID(), receiverID, chatID, messageType);
  }
}