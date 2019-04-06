package edu.northeastern.ccs.im.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.services.GroupServices;
import edu.northeastern.ccs.im.services.MessageServices;
import edu.northeastern.ccs.im.services.UserServices;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * This is a test class for all methods of the ClientRunnable class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({UserServices.class, MessageServices.class, GroupServices.class,Prattle.class})
@PowerMockIgnore("javax.net.ssl.*")
public class ClientRunnableTest {

  private static NetworkConnection clientSocket;
  public ClientRunnable clientRunnable;
  NetworkConnection connection;

  /**
   * Set up for testing.
   */
  @Before
  public void init() {
    connection = mock(NetworkConnection.class);
    clientRunnable = new ClientRunnable(connection);

  }

  public void delete() throws SQLException {
    UserDAO.getInstance().deleteUser("t");
  }


  /**
   * Test for the getUserId method.
   */
  @Test
  public void testGetUserId() throws SQLException {
    List<Message> nameList = new ArrayList();
    mockStatic(UserServices.class);
    List<String> pushMsgs = new ArrayList<>();
    //pushMsgs.add("ABC 1");
    mockStatic(MessageServices.class);
    when(MessageServices.getPushNotifications(any())).thenReturn(pushMsgs);

    when(UserServices.login("r", "a")).thenReturn(true);
    Message testMsg0 = Message.makeSimpleLoginMessage("r", "a");


    Message testMsg1 = Message.makeBroadcastMessage("Rohan", "Test");
    nameList.add(testMsg0);
    nameList.add(testMsg1);
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
    assertNotSame(clientRunnable.getUserId(), -1);
  }

  /**
   * Test for when the sender's username is null.
   */
  @Test
  public void testRunForNullMethodName() {
    mockStatic(UserServices.class);
    when(connection.iterator()).thenReturn(new Iterator<Message>() {
      @Override
      public boolean hasNext() {
        return true;
      }

      @Override
      public Message next() {
        return Message.makeBroadcastMessage(null, "Random Text");
      }
    });
    clientRunnable.run();
    assertEquals(false, clientRunnable.isInitialized());
  }

  /**
   * Test for the handleIncomingMessage method.
   */
  @Test
  public void testHandleIncomingMessage() {
    mockStatic(UserServices.class);
    when(connection.iterator()).thenReturn(new Iterator<Message>() {
      @Override
      public boolean hasNext() {
        return true;
      }

      @Override
      public Message next() {
        return Message.makeBroadcastMessage("Rohan", "Random Text");
      }
    });
    clientRunnable.run();
    clientRunnable.run();
    assertEquals(false, clientRunnable.isInitialized());
  }

  /**
   * Test for making a quit message to terminate connection.
   */
  @Test
  public void testTerminateMessage() throws SQLException {
    List<String> pushMsgs = new ArrayList<>();
    pushMsgs.add("ABC 1");
    mockStatic(MessageServices.class);
    when(MessageServices.getPushNotifications(any())).thenReturn(pushMsgs);

    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    List<Message> nameList = new ArrayList();
    mockStatic(UserServices.class);
    when(UserServices.login("r", "a")).thenReturn(true);

    Message testMessage1 = Message.makeSimpleLoginMessage("r", "a");
    ;
    Message testMessage2 = Message.makeQuitMessage("Rohan");
    nameList.add(testMessage1);
    nameList.add(testMessage2);
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

    Message tm1 = Message.makeSimpleLoginMessage("r", "a");
    ;
    clientRunnable.enqueueMessage(tm1);
    when(connection.sendMessage(any())).thenReturn(true);
    when(connection.iterator()).thenReturn(itr);
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    clientRunnable.run();
    clientRunnable.run();
    verify(connection, times(4)).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals(4, capturedMsgs.size());
    assertEquals(false, capturedMsgs.get(1).terminate());
    verify(connection).close();
  }

  /**
   * Testing for more than one login message being enqueued.
   */
  @Test
  public void testDualHelloMessage() throws SQLException {
    List<String> pushMsgs = new ArrayList<>();
    pushMsgs.add("ABC 1");
    mockStatic(MessageServices.class);
    when(MessageServices.getPushNotifications(any())).thenReturn(pushMsgs);

    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    List<Message> nameList = new ArrayList();
    mockStatic(UserServices.class);
    when(UserServices.login("r", "a")).thenReturn(true);

    Message testMessage1 = Message.makeSimpleLoginMessage("r", "a");
    ;
    Message testMessage2 = Message.makeSimpleLoginMessage("r", "a");
    ;
    nameList.add(testMessage1);
    nameList.add(testMessage2);
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

    Message tm1 = Message.makeSimpleLoginMessage("r", "a");
    ;
    clientRunnable.enqueueMessage(tm1);
    when(connection.sendMessage(any())).thenReturn(true);
    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
    clientRunnable.run();
    verify(connection, times(3)).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals(3, capturedMsgs.size());
    assertEquals(true, capturedMsgs.get(0).isInitialization());

  }

  /**
   * Test for the run method to enqueue and send messages.
   */
  @Test
  public void testRun() throws SQLException {
    List<String> pushMsgs = new ArrayList<>();
    pushMsgs.add("ABC 1");
    mockStatic(MessageServices.class);
    when(MessageServices.getPushNotifications(any())).thenReturn(pushMsgs);

    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    Message testMessage1 = Message.makeBroadcastMessage("Rohan", "Random1");
    Message testMessage2 = Message.makeBroadcastMessage("Rohan", "Random2");
    List<Message> nameList = new ArrayList();
    mockStatic(UserServices.class);
    when(UserServices.login("r", "a")).thenReturn(true);

    Message testMsg0 = Message.makeSimpleLoginMessage("r", "a");
    Message testMsg1 = Message.makeBroadcastMessage("Rohan", "Test");
    nameList.add(testMsg0);
    nameList.add(testMsg1);
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

    when(connection.iterator()).thenReturn(itr);

    clientRunnable.enqueueMessage(testMessage1);
    clientRunnable.enqueueMessage(testMessage2);
    when(connection.sendMessage(any())).thenReturn(true);
    clientRunnable.run();
    clientRunnable.run();
    clientRunnable.run();
    verify(connection, times(5)).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals(5, capturedMsgs.size());
    assertEquals("Rohan", capturedMsgs.get(0).getName());
    assertEquals("Random1", capturedMsgs.get(0).getText());
    assertEquals("Rohan", capturedMsgs.get(1).getName());
    assertEquals("Random2", capturedMsgs.get(1).getText());

  }

  /**
   * Test for a failed ClientRunnable initialization.
   */
  @Test
  public void testIncorrectInitialization() {
    mockStatic(UserServices.class);
    List<Message> nameList = new ArrayList();
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
    assertEquals(clientRunnable.isInitialized(), false);
  }

  /**
   * Test for when messages are sent from different users.
   */
  @Test
  public void testRunForDifferentNames() throws SQLException {
    List<String> pushMsgs = new ArrayList<>();
    pushMsgs.add("ABC 1");
    mockStatic(MessageServices.class);
    when(MessageServices.getPushNotifications(any())).thenReturn(pushMsgs);

    when(connection.sendMessage(any())).thenReturn(true);
    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    //when(connection.sendMessage(testMsg)).thenReturn(true);
    mockStatic(UserServices.class);
    when(UserServices.login("r", "a")).thenReturn(true);

    Message testMessage1 = Message.makeSimpleLoginMessage("r", "a");
    ;
    Message testMessage2 = Message.makeBroadcastMessage("r", "Random2");
    Message testMessage3 = Message.makeBroadcastMessage("Josh", "Random2");
    List<Message> nameList = new ArrayList();
    nameList.add(testMessage1);
    nameList.add(testMessage2);
    nameList.add(testMessage3);
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    clientRunnable.run();
    clientRunnable.run();
    verify(connection, times(3)).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals(3, capturedMsgs.size());
    assertEquals("Prattle", capturedMsgs.get(0).getName());
    clientRunnable.terminateClient();
    verify(connection).close();
  }

  /**
   * Test run method when message sender is null.
   */
  @Test
  public void testRunForNullMessageName() throws SQLException {
    List<String> pushMsgs = new ArrayList<>();
    pushMsgs.add("ABC 1");
    mockStatic(MessageServices.class);
    when(MessageServices.getPushNotifications(any())).thenReturn(pushMsgs);

    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    List<Message> nameList = new ArrayList();
    mockStatic(UserServices.class);
    when(UserServices.login("r", "a")).thenReturn(true);

    Message testMessage1 = Message.makeSimpleLoginMessage("r", "a");
    ;
    Message testMessage2 = Message.makeBroadcastMessage(null, "Random");
    nameList.add(testMessage1);
    nameList.add(testMessage2);
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);
    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    clientRunnable.run();
    verify(connection, times(3)).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals(3, capturedMsgs.size());
    assertEquals("Prattle", capturedMsgs.get(0).getName());
    //Mockito.verify(connection).close();
  }

  /**
   * Test for the run method.
   */
  @Test
  public void testBasicRunMethod() throws SQLException {
    List<String> pushMsgs = new ArrayList<>();
    pushMsgs.add("ABC 1");
    mockStatic(MessageServices.class);
    when(MessageServices.getPushNotifications(any())).thenReturn(pushMsgs);

    when(connection.sendMessage(any())).thenReturn(true);

    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    mockStatic(UserServices.class);
    when(UserServices.login("r", "a")).thenReturn(true);

    Message testMessage1 = Message.makeSimpleLoginMessage("r", "a");
    ;
    Message testMessage2 = Message.makeBroadcastMessage("Rohan", "Random2");
    List<Message> nameList = new ArrayList();
    nameList.add(testMessage1);
    nameList.add(testMessage2);

    clientRunnable.enqueueMessage(testMessage2);

    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);
    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
    clientRunnable.run();
    verify(connection, times(4)).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals(4, capturedMsgs.size());
    assertEquals("Rohan", capturedMsgs.get(0).getName());
  }

  /**
   * Test method to terminate a ClientRunnable.
   */
  @Test
  public void testTerminate() throws SQLException {
    List<String> pushMsgs = new ArrayList<>();
    pushMsgs.add("ABC 1");
    mockStatic(MessageServices.class);
    when(MessageServices.getPushNotifications(any())).thenReturn(pushMsgs);

    mockStatic(UserServices.class);
    when(UserServices.login("r", "a")).thenReturn(true);

    when(connection.iterator()).thenReturn(new Iterator<Message>() {
      @Override
      public boolean hasNext() {
        return true;
      }

      @Override
      public Message next() {
        return Message.makeSimpleLoginMessage(null, "Random Text");
      }
    });
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    clientRunnable.run();
    clientRunnable.terminateClient();
    verify(connection,times(2)).close();
  }

  @Test
  public void testPrivate() {
    mockStatic(UserServices.class);
    when(connection.iterator()).thenReturn(new Iterator<Message>() {
      @Override
      public boolean hasNext() {
        return true;
      }

      @Override
      public Message next() {
        return Message.makePrivateMessage("R", "/pvt z hello");
      }
    });
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    clientRunnable.run();
    clientRunnable.terminateClient();
    verify(connection).close();
  }

  @Test
  public void testTerminateException() throws Exception {
    mockStatic(UserServices.class);
    PowerMockito.doThrow(new SQLException("Custom DB Exception")).when(UserServices.class,"updateLastSeen",any(),any());
    when(connection.iterator()).thenReturn(new Iterator<Message>() {
      @Override
      public boolean hasNext() {
        return true;
      }

      @Override
      public Message next() {
        return Message.makePrivateMessage("R", "/pvt z hello");
      }
    });
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    clientRunnable.run();
    clientRunnable.terminateClient();
    verify(connection).close();
  }

  /**
   * Test for the timerIsBehind method.
   */
  @Test
  public void testTimerIsBehind() throws InvocationTargetException, IllegalAccessException {
    mockStatic(UserServices.class);
    Class<ClientRunnable> clazz = ClientRunnable.class;
    ClientTimer timer = mock(ClientTimer.class);
    when(timer.isBehind()).thenReturn(true);
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("setTerminateIfTimerIsBehind")) {
        met = m;
      }
    }
    ClientRunnable clientRunnable = new ClientRunnable(connection);
    //Method met = clazz.getDeclaredMethod("setTerminateIfTimerIsBehind", ClientRunnable.class);
    met.setAccessible(true);
    met.invoke(clientRunnable, timer);
    assertEquals(clientRunnable.isInitialized(), false);

  }

  @Test
  public void testRetrieveMessageForUser() throws InvocationTargetException, IllegalAccessException, SQLException {
    List<String> msgList = new ArrayList<>();
    msgList.add("r /pvt j Hii");
    msgList.add("j /pvt r hello back");
    mockStatic(MessageServices.class);
    when(MessageServices.retrieveUserMessages("r", "j")).thenReturn(msgList);

    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("retrieveMessagesForUser")) {
        met = m;
      }
    }
    ClientRunnable clientRunnable = new ClientRunnable(connection);
    met.setAccessible(true);
    Message msg = Message.makeRetrieveUserMessage("r", "/retrieveUSR j");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testRetrieveMessageForGRP() throws InvocationTargetException, IllegalAccessException, SQLException {

    List<String> msgList = new ArrayList<>();
    msgList.add("r /grp MSD Hello");
    msgList.add("j /grp MSD hello to the group");
    mockStatic(MessageServices.class);
    when(MessageServices.retrieveGroupMessages("MSD")).thenReturn(msgList);

    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("retrieveGroupMessagesForGroup")) {
        met = m;
      }
    }
    ClientRunnable clientRunnable = new ClientRunnable(connection);
    met.setAccessible(true);
    Message msg = Message.makeRetrieveGroupMessage("r", "/retrieveUSR MSD");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testProcessDeactivateUser() throws InvocationTargetException, IllegalAccessException, SQLException {

    mockStatic(UserServices.class);

    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }
    ClientRunnable clientRunnable = new ClientRunnable(connection);
    met.setAccessible(true);
    clientRunnable.setName("J");
    Message msg = Message.makeDeactivateUserMessage("J", "/deactivateACT");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testProcessUserExistsTrue() throws InvocationTargetException, IllegalAccessException, SQLException {

    mockStatic(UserServices.class);
    when(UserServices.userExists("j")).thenReturn(true);

    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }
    ClientRunnable clientRunnable = new ClientRunnable(connection);
    clientRunnable.setName("r");
    met.setAccessible(true);
    Message msg = Message.makeUserExistsMessage("r", "/search j");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testProcessUserExistsFalse() throws InvocationTargetException, IllegalAccessException, SQLException {

    mockStatic(UserServices.class);
    when(UserServices.userExists("a")).thenReturn(false);

    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }
    ClientRunnable clientRunnable = new ClientRunnable(connection);
    clientRunnable.setName("r");
    met.setAccessible(true);
    Message msg = Message.makeUserExistsMessage("r", "/search a");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testProcessLastSeen() throws InvocationTargetException, IllegalAccessException, SQLException {

    mockStatic(UserServices.class);
    when(UserServices.getLastSeen("a")).thenReturn(new Long(0));

    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }
    ClientRunnable clientRunnable = new ClientRunnable(connection);
    clientRunnable.setName("r");
    met.setAccessible(true);
    Message msg = Message.makeLastSeenMessage("r", "/lastSeen J");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testProcessMakeAdmin() throws InvocationTargetException, IllegalAccessException, SQLException {

    mockStatic(GroupServices.class);

    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }
    ClientRunnable clientRunnable = new ClientRunnable(connection);
    clientRunnable.setName("r");
    met.setAccessible(true);
    Message msg = Message.makeMakeAdminMessage("r", "/makeAdmin MSD J");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testProcessSetGroupRestriction() throws InvocationTargetException, IllegalAccessException, SQLException {

    mockStatic(GroupServices.class);

    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }
    ClientRunnable clientRunnable = new ClientRunnable(connection);
    clientRunnable.setName("r");
    met.setAccessible(true);
    Message msg = Message.makeSetGroupRestrictionMessage("r", "/setGroupRestriction MSD H");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testProcessLeaveGroup() throws InvocationTargetException, IllegalAccessException, SQLException {

    mockStatic(GroupServices.class);
    mockStatic(Prattle.class);
    List<String> list = new ArrayList();
    list.add("r");
    list.add("j");
    ConcurrentMap<String,List<String>> hm = new ConcurrentHashMap<>();
    List<String> l = new ArrayList<>();
    l.add("r");
    hm.put("MSD",list);
    Whitebox.setInternalState(Prattle.class,"groupToUserMapping",hm);
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }
    ClientRunnable clientRunnable = new ClientRunnable(connection);
    clientRunnable.setName("r");
    met.setAccessible(true);
    Message msg = Message.makeLeaveGroupMessage("r", "/leaveGroup MSD");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testProcessMessagePVT() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, SQLException {
    mockStatic(MessageServices.class);
    mockStatic(Prattle.class);
    List<String> wt = new ArrayList<>();
    wt.add("r");
    Whitebox.setInternalState(Prattle.class,"listOfWireTappedUsers",wt);
    when(Prattle.updateAndGetChatIDFromUserMap(any(),any())).thenReturn(1);
    when(MessageServices.addMessage(any(),any(),any(),any(),any(Integer.class), any(), any(Boolean.class) )).thenReturn(true);
    clientRunnable.setName("test");
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }

    met.setAccessible(true);
    Message msg = Message.makePrivateMessage("test", "/pvt r hello world");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testProcessMessagePVTDBException() throws Exception {
    mockStatic(MessageServices.class);
    mockStatic(Prattle.class);
    List<String> wt = new ArrayList<>();
    wt.add("r");
    Whitebox.setInternalState(Prattle.class,"listOfWireTappedUsers",wt);
    when(Prattle.updateAndGetChatIDFromUserMap(any(),any())).thenReturn(1);
    PowerMockito.doThrow(new DatabaseConnectionException("Custom DB Exception")).when(MessageServices.class,"addMessage",any(),any(),any(),any(),any(Integer.class), any() ,any(Boolean.class));
    clientRunnable.setName("test");
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }

    met.setAccessible(true);
    Message msg = Message.makePrivateMessage("test", "/pvt r hello world");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testProcessMessagePVTSQLException() throws Exception {
    mockStatic(MessageServices.class);
    mockStatic(Prattle.class);
    List<String> wt = new ArrayList<>();
    wt.add("r");
    Whitebox.setInternalState(Prattle.class,"listOfWireTappedUsers",wt);
    when(Prattle.updateAndGetChatIDFromUserMap(any(),any())).thenReturn(1);
    PowerMockito.doThrow(new SQLException("Custom SQL Exception")).when(MessageServices.class,"addMessage",any(),any(),any(),any(),any(Integer.class),any() ,any(Boolean.class));
    clientRunnable.setName("test");
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }

    met.setAccessible(true);
    Message msg = Message.makePrivateMessage("test", "/pvt r hello world");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testProcessMessageGrpFalse() throws Exception {

    List<String> wt = new ArrayList<>();
    wt.add("r");
    Whitebox.setInternalState(Prattle.class,"listOfWireTappedUsers",wt);
    List<String> list = new ArrayList();
    list.add("r");
    list.add("j");
    ConcurrentMap<String,List<String>> hm = new ConcurrentHashMap<>();
    hm.put("MSD",list);

    Whitebox.setInternalState(Prattle.class,"groupToUserMapping",hm);
    clientRunnable.setName("test");
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }

    met.setAccessible(true);
    mockStatic(Prattle.class);
    when(Prattle.sendGroupMessage(any(),any())).thenReturn(false);
    Message msg = Message.makeGroupMessage("test", "/grp r hello world");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testProcessMessageGRP() throws Exception {
    clientRunnable.setName("test");
    mockStatic(MessageServices.class);
    mockStatic(GroupServices.class);
    mockStatic(Prattle.class);
    when(Prattle.updateAndGetChatIDFromGroupMap(any())).thenReturn(1);
    List<String> list = new ArrayList();
    list.add("r");
    list.add("j");
    ConcurrentMap<String,List<String>> hm = new ConcurrentHashMap<>();
    hm.put("MSD",list);

    Whitebox.setInternalState(Prattle.class,"groupToUserMapping",hm);
    when(Prattle.sendGroupMessage(any(),any())).thenReturn(true);
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }

    met.setAccessible(true);
    Message msg1 = Message.createGroupMessage("test", "/createGrp TEST");
    Message msg2 = Message.makeAddUserToGroupMessage("test", "/addUsrToGrp TEST z");
    Message msg3 = Message.makeGroupMessage("test", "/grp TEST hello world");
    Message msg4 = Message.makeRemoveUserMessage("test", "/grp TEST z");
    Message msg5 = Message.deleteGroupMessage("test", "/grp TEST");
    met.invoke(clientRunnable, msg1);
    met.invoke(clientRunnable, msg2);
    met.invoke(clientRunnable, msg3);
    met.invoke(clientRunnable, msg4);
    met.invoke(clientRunnable, msg5);
  }

  @Test
  public void testProcessGetUsersInGroupMessage() throws Exception {
    clientRunnable.setName("test");
    mockStatic(MessageServices.class);
    mockStatic(GroupServices.class);
    mockStatic(Prattle.class);
    List<String> list = new ArrayList();
    list.add("r");
    list.add("j");
    ConcurrentMap<String, List<String>> hm = new ConcurrentHashMap<>();
    hm.put("MSD", list);

    Whitebox.setInternalState(Prattle.class, "groupToUserMapping", hm);
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }
    Message msg = Message.makeGetUsersInGroupMessage("test", "/getUsersInGroup MSD");
    met.setAccessible(true);
    met.invoke(clientRunnable, msg);
  }


  @Test
  public void testUserFunctions() throws InvocationTargetException, IllegalAccessException {
    clientRunnable.setName("test");
    mockStatic(UserServices.class);
    mockStatic(Prattle.class);
    when(Prattle.updateAndGetChatIDFromGroupMap(any())).thenReturn(1);
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }

    met.setAccessible(true);
    Message msg1 = Message.makeUpdateFirstNameMessage("test", "/createGrp XYZ");
    Message msg2 = Message.makeUpdateLastNameMessage("test", "/addUsrToGrp XYZ z");
    Message msg3 = Message.makeUpdateEmailMessage("test", "/addUsrToGrp XYZ");
    Message msg4 = Message.makeUpdatePasswordMessage("test", "/grp test");
    met.invoke(clientRunnable, msg1);
    met.invoke(clientRunnable, msg2);
    met.invoke(clientRunnable, msg3);
    met.invoke(clientRunnable, msg4);
  }

  @Test
  public void testRegisteration() throws SQLException {
    Message testMessage1 = Message.makeRegisterationMessage("t", "t t t t t");
    mockStatic(UserServices.class);
    when(UserServices.register("t", "t", "t", "t", "t")).thenReturn(true);

    List<Message> nameList = new ArrayList();
    nameList.add(testMessage1);

    clientRunnable.enqueueMessage(testMessage1);

    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);
    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
  }

  @Test
  public void processUsrGrpRet() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException, SQLException {
    clientRunnable.setName("r");
    mockStatic(MessageServices.class);
    when(MessageServices.retrieveGroupMessages(any())).thenReturn(new ArrayList<>());
    when(MessageServices.retrieveUserMessages(any(),any())).thenReturn(new ArrayList<>());
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }

    met.setAccessible(true);
    Message msg1 = Message.makeRetrieveUserMessage("r", "/retrieveUSR j");
    Message msg2 = Message.makeRetrieveGroupMessage("r", "retrieveGrp MSD");
    met.invoke(clientRunnable, msg1);
    met.invoke(clientRunnable, msg2);
  }

  @Test
  public void testDuplicateRegisteration() throws SQLException {
    Mockito.when(connection.sendMessage(any())).thenReturn(true);
    mockStatic(UserServices.class);
    when(UserServices.register("r", "r", "r", "r", "r")).thenReturn(false);
    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    Message testMessage1 = Message.makeRegisterationMessage("a", "r r r r r");
    ;
    List<Message> nameList = new ArrayList();
    nameList.add(testMessage1);

    clientRunnable.enqueueMessage(testMessage1);

    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);
    when(connection.iterator()).thenReturn(itr);
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    clientRunnable.run();
    verify(connection).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals("NAK 7 Prattle 42 Either Illegal name or useralready exists.", capturedMsgs.get(0).toString());
  }

//  @Test
//  public void testFailedLogin() {
//    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
//    Message testMessage1 = Message.makeSimpleLoginMessage("y", "z");
//    ;
//    List<Message> nameList = new ArrayList();
//    nameList.add(testMessage1);
//    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);
//
//    when(connection.iterator()).thenReturn(itr);
//    clientRunnable.run();
//    verify(connection).sendMessage(messageCaptor.capture());
//    List<Message> capturedMsgs = messageCaptor.getAllValues();
//    assertEquals("NAK 7 Prattle 28 Invalid username or password", capturedMsgs.get(0).toString());
//  }

  @Test
  public void testFailedLoginStaticMock() throws SQLException {
    List<String> pushMsgs = new ArrayList<>();
    pushMsgs.add("ABC 1");
    mockStatic(MessageServices.class);
    when(MessageServices.getPushNotifications(any())).thenReturn(pushMsgs);

    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    Message testMessage1 = Message.makeSimpleLoginMessage("y", "z");

    mockStatic(UserServices.class);
    when(UserServices.login(any(), any())).thenReturn(false);
    List<Message> nameList = new ArrayList();
    nameList.add(testMessage1);
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

    when(connection.iterator()).thenReturn(itr);
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    clientRunnable.run();
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    verify(connection).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals("NAK 7 Prattle 28 Invalid username or password", capturedMsgs.get(0).toString());
  }

  @Test
  public void testAttachment() throws Exception {

    clientRunnable.setName("test");
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }

    met.setAccessible(true);
    mockStatic(Prattle.class);
    Message msg = Message.makeAttachmentMessage("test", "/file r PQSD");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testRecallSuccessful() throws Exception {

    clientRunnable.setName("test");
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }

    met.setAccessible(true);
    mockStatic(Prattle.class);
    mockStatic(MessageServices.class);
    when(Prattle.isUserOnline(any())).thenReturn(true);
    when(MessageServices.recallMessage(any(),any())).thenReturn(true);
    Message msg = Message.makeRecallMessage("test", "/recall r ");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testRecallFail1() throws Exception {

    clientRunnable.setName("test");
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }

    met.setAccessible(true);
    mockStatic(Prattle.class);
    mockStatic(MessageServices.class);
    when(Prattle.isUserOnline(any())).thenReturn(true);
    when(MessageServices.recallMessage(any(),any())).thenReturn(false);
    Message msg = Message.makeRecallMessage("test", "/recall r ");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testRecallFail2() throws Exception {

    clientRunnable.setName("test");
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }

    met.setAccessible(true);
    mockStatic(Prattle.class);
    mockStatic(MessageServices.class);
    when(Prattle.isUserOnline(any())).thenReturn(false);
    when(MessageServices.recallMessage(any(),any())).thenReturn(true);
    Message msg = Message.makeRecallMessage("test", "/recall r ");
    met.invoke(clientRunnable, msg);
  }

  @Test
  public void testRecallFail3() throws Exception {

    clientRunnable.setName("test");
    Class<ClientRunnable> clazz = ClientRunnable.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("processMessage")) {
        met = m;
      }
    }

    met.setAccessible(true);
    mockStatic(Prattle.class);
    mockStatic(MessageServices.class);
    when(Prattle.isUserOnline(any())).thenReturn(false);
    when(MessageServices.recallMessage(any(),any())).thenReturn(false);
    Message msg = Message.makeRecallMessage("test", "/recall r ");
    met.invoke(clientRunnable, msg);
  }
  /**
   * Message Iterator for use in testing the ClientRunnable class.
   */
  private class GenericMessageIterator<Message> implements Iterator<Message> {
    List<Message> nameList;

    /**
     * Default constructor.
     */
    public GenericMessageIterator(List<Message> messageList) {
      this.nameList = messageList;
    }

    @Override
    public boolean hasNext() {
      return !nameList.isEmpty();
    }

    @Override
    public Message next() {
      return nameList.remove(0);
    }
  }
}
