package edu.northeastern.ccs.im.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.northeastern.ccs.im.dao.*;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Groups;
import edu.northeastern.ccs.im.model.Message;
import edu.northeastern.ccs.im.model.User;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageServiceTest {

  private MessageToUserDAO mockMessageToUserDAO;
  private UserDAO mockUserDAO;
  private MessageDAO mockMessageDAO;
  private GroupDAO mockGroupDAO;
  private GroupToUserDAO mockGroupUserDAO;
  private MessageServices messageServices;
  private Message msg;
  private Message createdMsg;
  private String time;
  private User user;
  private User createdUser;
  private User receiver;
  private User createdReceiver;
  private String updatedTime;
  private Groups group;
  private Groups createdGroup;
  private List<String> pvtChat;
  private List<String> grpChat;

  @Before
  public void setUp() throws NoSuchFieldException, IllegalAccessException, SQLException {
    mockMessageToUserDAO = mock(MessageToUserDAO.class);
    mockUserDAO = mock(UserDAO.class);
    mockGroupDAO = mock(GroupDAO.class);
    mockMessageDAO = mock(MessageDAO.class);
    mockGroupUserDAO = mock(GroupToUserDAO.class);
    time = Long.toString(System.currentTimeMillis());

    user = new User("Daba", "Daba", "Daba", "daba@gmail.com", "daba");
    createdUser = new User(52, "Daba", "Daba", "Daba", "daba@gmail.com", "daba");
    receiver = new User("Daba11", "Daba11", "Daba11", "daba11@gmail.com", "daba11");
    createdReceiver = new User(12, "Daba11", "Daba11", "Daba11", "daba11@gmail.com", "daba11");
    when(mockUserDAO.getUserByUsername(user.getUsername())).thenReturn(createdUser);
    when(mockUserDAO.getUserByUserID(user.getUserID())).thenReturn(createdUser);
    when(mockUserDAO.isUserExists(user.getUsername())).thenReturn(true);
    when(mockUserDAO.isUserExists(user.getUserID())).thenReturn(true);

    when(mockUserDAO.getUserByUsername(receiver.getUsername())).thenReturn(createdReceiver);
    when(mockUserDAO.getUserByUsername(createdReceiver.getUsername())).thenReturn(createdReceiver);
    when(mockUserDAO.getUserByUserID(receiver.getUserID())).thenReturn(createdReceiver);
    when(mockUserDAO.isUserExists(receiver.getUsername())).thenReturn(true);
    //when(mockUserDAO.isUserExists(receiver.getUserID())).thenReturn(true);
    when(mockUserDAO.isUserExists(any(Integer.class))).thenReturn(true);

    updatedTime = Long.toString(System.currentTimeMillis());

    group = new Groups("MSD", createdUser.getUsername());
    createdGroup = new Groups(2, "MSD", createdUser.getUsername());
    when(mockGroupDAO.checkGroupExists("MSD")).thenReturn(true);
    when(mockGroupDAO.getGroupByGroupName("MSD")).thenReturn(createdGroup);
    when(mockGroupDAO.getGroupByGroupID(createdGroup.getGrpID())).thenReturn(createdGroup);

    msg = new Message(Message.MsgType.PVT, createdUser.getUserID(), "Yo", time);
    createdMsg = new Message(22, Message.MsgType.PVT, createdUser.getUserID(), "Yo", time);

    pvtChat = new ArrayList<>();
    pvtChat.add(createdUser.getUsername() + " /pvt " + createdMsg.getMessageText());

    grpChat = new ArrayList<>();
    grpChat.add(createdUser.getUsername() + " /grp " + createdGroup.getGrpName() + " " + createdMsg.getMessageText());
    when(mockMessageDAO.createMessage(msg)).thenReturn(createdMsg);
    doNothing().when(mockMessageToUserDAO).mapMsgIdToReceiverId(createdMsg, createdReceiver.getUserID(), "00000000");
    when(mockMessageToUserDAO.retrieveUserMsg(createdUser.getUsername(), createdReceiver.getUsername())).thenReturn(pvtChat);
    when(mockMessageToUserDAO.getMessagesFromGroup("MSD")).thenReturn(grpChat);

    Class clazz = MessageServices.class;
    Field messageDAOField = clazz.getDeclaredField("messageDAO");
    messageDAOField.setAccessible(true);
    messageDAOField.set(messageServices, mockMessageDAO);

    Field messageUserDAOField = clazz.getDeclaredField("messageUserDAO");
    messageUserDAOField.setAccessible(true);
    messageUserDAOField.set(messageServices, mockMessageToUserDAO);

    Field userDAOField = clazz.getDeclaredField("userDAO");
    userDAOField.setAccessible(true);
    userDAOField.set(messageServices, mockUserDAO);

    Field groupDAOField = clazz.getDeclaredField("groupDAO");
    groupDAOField.setAccessible(true);
    groupDAOField.set(messageServices, mockGroupDAO);

    Field groupUserDAOField = clazz.getDeclaredField("groupUserDAO");
    groupUserDAOField.setAccessible(true);
    groupUserDAOField.set(messageServices, mockGroupUserDAO);
  }

  @Test
  public void testSendPVT() throws SQLException {
    Map<Message.IPType, String> map = new HashMap<>();
    map.put(Message.IPType.RECEIVERIP, "00000000");
    map.put(Message.IPType.SENDERIP, "00000000");
    assertEquals(true, MessageServices.addMessage(msg.getMsgType(), createdUser.getUsername(), createdReceiver.getUsername(), msg.getMessageText(), 0, map, false));
  }


  @Test
  public void testSendGRP() throws SQLException {
    Map<Message.IPType, String> map = new HashMap<>();
    map.put(Message.IPType.RECEIVERIP, "00000000");
    map.put(Message.IPType.SENDERIP, "00000000");
    assertEquals(true, MessageServices.addMessage(Message.MsgType.GRP, createdUser.getUsername(), createdGroup.getGrpName(), msg.getMessageText(), 0, map, false));
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testSendFalse() throws SQLException {
    Map<Message.IPType, String> map = new HashMap<>();
    map.put(Message.IPType.RECEIVERIP, "00000000");
    map.put(Message.IPType.SENDERIP, "00000000");
    assertEquals(false, MessageServices.addMessage(Message.MsgType.BCT, createdUser.getUsername(), createdGroup.getGrpName(), msg.getMessageText(), 0, map, false));
  }

  @Test
  public void testSendPVTReceiverNotExist() throws SQLException {
    Map<Message.IPType, String> map = new HashMap<>();
    map.put(Message.IPType.RECEIVERIP, "00000000");
    map.put(Message.IPType.SENDERIP, "00000000");
    assertEquals(false, MessageServices.addMessage(Message.MsgType.PVT, createdUser.getUsername(), "ABCD", msg.getMessageText(), 0, map, false));
  }

  @Test
  public void testSendGRPReceiverNotExist() throws SQLException {
    Map<Message.IPType, String> map = new HashMap<>();
    map.put(Message.IPType.RECEIVERIP, "00000000");
    map.put(Message.IPType.SENDERIP, "00000000");
    assertEquals(false, MessageServices.addMessage(Message.MsgType.GRP, createdUser.getUsername(), "ABCD", msg.getMessageText(), 0, map, false));
  }

  @Test
  public void testRetrieveUserMessages() throws SQLException {
    assertEquals(pvtChat, MessageServices.retrieveUserMessages(createdUser.getUsername(), createdReceiver.getUsername()));
  }

  @Test
  public void testRetrieveGroupMessages() throws SQLException {
    assertEquals(grpChat, MessageServices.retrieveGroupMessages(createdGroup.getGrpName()));
  }

  @Test
  public void testRecall() throws SQLException {
    when(mockMessageDAO.getTimeStampOfLastMessage(anyInt(), anyInt())).thenReturn("00000001");
    when(mockUserDAO.getLastSeen(anyString())).thenReturn("00000000");
    when(mockMessageDAO.getIdOfLastMessage(anyInt(), anyInt())).thenReturn(1);
    assertTrue(MessageServices.recallMessage("Daba", "Daba11"));
  }

  @Test
  public void testRecallFalse() throws SQLException {
    when(mockMessageDAO.getTimeStampOfLastMessage(anyInt(), anyInt())).thenReturn("00000000");
    when(mockUserDAO.getLastSeen(anyString())).thenReturn("00000000");
    when(mockMessageDAO.getIdOfLastMessage(anyInt(), anyInt())).thenReturn(1);
    assertFalse(MessageServices.recallMessage("Daba", "Daba11"));
  }

  @Test
  public void testPushNotifications() throws SQLException {
    List<String> notifications = new ArrayList<>();
    notifications.add("user1 5");
    when(mockMessageToUserDAO.getNotifications(52)).thenReturn(notifications);
    when(mockGroupUserDAO.getFollowThreadNotification("Daba")).thenReturn(notifications);
    assertEquals(2, MessageServices.getPushNotifications("Daba").size());
  }

  @Test
  public void testRetrieveGroupMessagesTime() throws SQLException {
    MessageServices.getGroupMessagesBetween(createdGroup.getGrpName(), "0000", "1111");
  }

  @Test
  public void testGetChatID() throws SQLException {
    MultiKeyMap<String, Integer> map = new MultiKeyMap<>();
    when(mockMessageDAO.getChatIDForUsers()).thenReturn(map);
    assertEquals(map, MessageServices.getChatIDForUsers());
  }

  @Test
  public void testPostMessageToThread() throws SQLException {
    when(mockGroupDAO.checkGroupExists("ThreadTest")).thenReturn(true);
    Groups testThread = new Groups(2, "ThreadTest", "abc");
    testThread.setThread(true);
    when(mockGroupDAO.getGroupByGroupName("ThreadTest")).thenReturn(testThread);
    User testUser = new User(22, "aditi", "aditi", "kacheria", "ak@hotmail.com", "kakakak");
    when(mockUserDAO.getUserByUsername("aditi")).thenReturn(testUser);
    Message message = new Message(Message.MsgType.TRD, 22, "hello", Long.toString(System.currentTimeMillis()));
    Message createdMessage = new Message(12, Message.MsgType.TRD, 22, "hello", Long.toString(System.currentTimeMillis()));
    when(mockMessageDAO.addMessageToThread(message)).thenReturn(createdMessage);
    doNothing().when(mockMessageToUserDAO).mapMsgIdToReceiverThreadId(createdMsg, 2);
    MessageServices.postMessageToThread(Message.MsgType.TRD,testUser.getUsername(),testThread.getGrpName(),message.getMessageText());
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testPostMessageToThreadNotExist() throws SQLException {
    when(mockGroupDAO.checkGroupExists("ThreadTest")).thenReturn(false);
    MessageServices.postMessageToThread(Message.MsgType.TRD,"aditi","ThreadTest","hello");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testPostMessageToThreadNotThread() throws SQLException {
    when(mockGroupDAO.checkGroupExists("ThreadTest")).thenReturn(true);
    Groups testThread = new Groups(2, "ThreadTest", "abc");
    testThread.setThread(false);
    when(mockGroupDAO.getGroupByGroupName("ThreadTest")).thenReturn(testThread);
    MessageServices.postMessageToThread(Message.MsgType.TRD,"aditi","ThreadTest","hello");
  }

  @Test(expected = DatabaseConnectionException.class)
  public void testPostMessageToThreadMsgTypeNotTRD() throws SQLException {
    when(mockGroupDAO.checkGroupExists("ThreadTest")).thenReturn(true);
    Groups testThread = new Groups(2,"ThreadTest","abc");
    testThread.setThread(true);
    when(mockGroupDAO.getGroupByGroupName("ThreadTest")).thenReturn(testThread);
    MessageServices.postMessageToThread(Message.MsgType.GRP,"aditi",testThread.getGrpName(),"hello");
  }

  @Test
  public void testSecret() throws SQLException {
    when(mockMessageDAO.isSecret(any(Integer.class), any(Integer.class), any(Integer.class))).thenReturn(true);
    assertTrue(MessageServices.isSecret("Daba","Daba",1));
  }

  @Test
  public void testSecretFalse() throws SQLException {
    when(mockMessageDAO.isSecret(any(Integer.class), any(Integer.class), any(Integer.class))).thenReturn(false);
    assertFalse(MessageServices.isSecret("Daba","Daba",1));
  }
}
