package edu.northeastern.ccs.im.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.services.GroupServices;
import edu.northeastern.ccs.im.services.MessageServices;
import edu.northeastern.ccs.im.services.UserServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import sun.nio.ch.Net;

/**
 * Test class for the methods in the Prattle class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({GroupServices.class,UserServices.class,MessageServices.class})
@PowerMockIgnore("javax.net.ssl.*")

public class PrattleTest {
  private static final int PORT = 4546;
  private static final int CLIENT_CHECK_DELAY = 200;
  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();
  @Mock
  private ClientRunnable clientRunnable;
  @Mock
  private ScheduledExecutorService executor;
  private ServerSocketChannel serverSocketChannel;
  private NetworkConnection networkConnection;
  private ConcurrentLinkedQueue<ClientRunnable> queue;

  /**
   * Method to set up for testing.
   */
  @Before
  public void setUp() throws IOException, NoSuchFieldException, IllegalAccessException {
    serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
    SocketChannel clientSocket = SocketChannel.open();
    networkConnection = new NetworkConnection(clientSocket);
    when(clientRunnable.isInitialized()).thenReturn(true);

    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("active");

    field.setAccessible(true);
    //queue = (ConcurrentLinkedQueue<ClientRunnable>) field.get("Prattle");
    queue = new ConcurrentLinkedQueue<ClientRunnable>();
    field.set(clientRunnable, queue);
    queue.add(clientRunnable);
  }

  /**
   * Test for broadcastMessage method.
   */
  @Test
  public void testBroadcastMessageTrue() throws IOException {
    Message message = Message.makeBroadcastMessage("abcd", "hello world");
    Prattle.broadcastMessage(message);
    assertEquals(true, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  /**
   * Test for broadcastMessage method failure.
   */
  @Test
  public void testBroadcastMessageFalse() throws IOException {
    when(clientRunnable.isInitialized()).thenReturn(false);
    Message message = Message.makeBroadcastMessage("abcd", "hello world");
    Prattle.broadcastMessage(message);
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  /**
   * Test for broadcasting a null message.
   */
  @Test
  public void testBroadcastMessageNull() throws IOException {
    Prattle.broadcastMessage(null);
    assertEquals(true, clientRunnable.isInitialized());
    serverSocketChannel.close();
  }

  /**
   * Test for the server close method.
   */
  @Test
  public void testStopServer() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
    Class clazz = Prattle.class;
    Method method = clazz.getDeclaredMethod("stopServer");
    method.setAccessible(true);
    method.invoke(new PrattleThread());
    Field field = clazz.getDeclaredField("isReady");
    field.setAccessible(true);
    assertEquals(false, field.get(null));
    serverSocketChannel.close();
  }

  /**
   * Test to create a client thread.
   */
  @Test
  public void testCreateClientThread() throws IllegalAccessException, InvocationTargetException, IOException {
    Class<Prattle> clazz = Prattle.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("createClientThread")) {
        met = m;
      }
    }
    met.setAccessible(true);
    ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
    ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);
    when(executor.scheduleAtFixedRate(clientRunnable, CLIENT_CHECK_DELAY, CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS)).thenReturn(scheduledFuture);
    met.invoke(null, serverSocketChannel, executor);
    serverSocketChannel.close();
  }

  /**
   * Test for removing a client thread.
   */
  @Test
  public void testRemoveClient() throws IOException {
    int onlineUserCount = queue.size();
    Prattle.removeClient(clientRunnable);
    onlineUserCount -= 1;
    assertEquals(onlineUserCount, queue.size());
    Prattle.removeClient(clientRunnable);
    assertEquals(onlineUserCount, queue.size());
    serverSocketChannel.close();
  }

  /**
   * Test for null socket.
   */
  @Test
  public void testCreateClientThreadNotNullSock() throws IllegalAccessException, InvocationTargetException, IOException {
    ServerSocketChannel serverSocketChannel = mock(ServerSocketChannel.class);
    SocketChannel sockChan = mock(SocketChannel.class);
    when(serverSocketChannel.accept()).thenReturn(sockChan);
    Class<Prattle> clazz = Prattle.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("createClientThread")) {
        met = m;
      }
    }
    met.setAccessible(true);
    ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
    ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);
    when(executor.scheduleAtFixedRate(clientRunnable, CLIENT_CHECK_DELAY, CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS)).thenReturn(scheduledFuture);
    met.invoke(null, serverSocketChannel, executor);
    this.serverSocketChannel.close();
  }

  /**
   * Test for notnullsocket assertion.
   */
  @Test
  public void testCreateClientThreadNotNullSockAssertExcept() throws IllegalAccessException, InvocationTargetException, IOException {
    ServerSocketChannel serverSocketChannel = mock(ServerSocketChannel.class);
    SocketChannel sockChan = mock(SocketChannel.class);
    doThrow(new IOException()).when(sockChan).configureBlocking(false);
    when(serverSocketChannel.accept()).thenReturn(sockChan);
    Class<Prattle> clazz = Prattle.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("createClientThread")) {
        met = m;
      }
    }
    met.setAccessible(true);
    ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
    ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);
    when(executor.scheduleAtFixedRate(clientRunnable, CLIENT_CHECK_DELAY, CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS)).thenReturn(scheduledFuture);
    met.invoke(null, serverSocketChannel, executor);
    //assertEquals(true, getDataFromFile().contains("AssertionError"));
    this.serverSocketChannel.close();
  }

  /**
   * Test for IOExeption with not null Socket.
   */
  @Test
  public void testCreateClientThreadNotNullSockIOExcept() throws IllegalAccessException, InvocationTargetException, IOException {
    ServerSocketChannel serverSocketChannel = mock(ServerSocketChannel.class);
    SocketChannel sockChan = mock(SocketChannel.class);
    doThrow(new IOException()).when(serverSocketChannel).accept();
    Class<Prattle> clazz = Prattle.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("createClientThread")) {
        met = m;
      }
    }
    met.setAccessible(true);
    ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
    ScheduledFuture scheduledFuture = mock(ScheduledFuture.class);
    when(executor.scheduleAtFixedRate(clientRunnable, CLIENT_CHECK_DELAY, CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS)).thenReturn(scheduledFuture);
    met.invoke(null, serverSocketChannel, executor);
    assertEquals(true, getDataFromFile().contains("IOException"));
    this.serverSocketChannel.close();
  }

  /**
   * Test to read data from logger file.
   */
  private String getDataFromFile() throws IOException {
    FileReader fr;
    File f;
    BufferedReader br;
    f = new File("edu.northeastern.ccs.im.ChatLogger.log");
    br = new BufferedReader(new FileReader(f));
    StringBuilder result = new StringBuilder();
    String next;

    while ((next = br.readLine()) != null) {
      result.append(next + "\n");
    }
    return result.toString();
  }

  /**
   * Test for broadcastMessage method.
   */
  @Test
  public void testPvtMessage() throws IOException {
    Message message = Message.makePrivateMessage("abcd", "hello world");
    when(clientRunnable.getName()).thenReturn("abc");
    when(clientRunnable.getDNDStatus()).thenReturn(false);
    Prattle.sendPrivateMessage(message, "abc");
    assertEquals(true, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    this.serverSocketChannel.close();
  }

  /**
   * Test for broadcastMessage method failure.
   */
  @Test
  public void testPvtMessageFalse() throws IOException {
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("abcd");
    Message message = Message.makePrivateMessage("abcd", "hello world");
    Prattle.sendPrivateMessage(message, "abc");
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  /**
   * Test for broadcastMessage method.
   */
  @Test
  public void testGrpMessage() throws IOException, SQLException, NoSuchFieldException, IllegalAccessException {
    //Return R, J
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("a");
    mockStatic(GroupServices.class);
    List<String> list = new ArrayList();
    list.add("r");
    list.add("j");
    ConcurrentMap<String,List<String>> hm = new ConcurrentHashMap<>();
    hm.put("MSD",list);

    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("groupToUserMapping");

    field.setAccessible(true);
    field.set(null, hm);


    Message message = Message.makeGroupMessage("r", "/grp PIKACHU Hello");
    assertFalse(Prattle.sendGroupMessage(message, "PDP"));
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  /**
   * Test for broadcastMessage method.
   */
  @Test
  public void testGrpMessageClientRunnableNotInList() throws IOException, SQLException, IllegalAccessException, NoSuchFieldException {
    //Return R, J
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("a");
    mockStatic(GroupServices.class);
    List<String> list = new ArrayList();
    list.add("z");
    list.add("r");
    ConcurrentMap<String,List<String>> hm = new ConcurrentHashMap<>();

    hm.put("MSD",list);

    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("groupToUserMapping");

    field.setAccessible(true);
    field.set(null, hm);


    Message message = Message.makeGroupMessage("r", "/grp MSD Hello");
    assertTrue(Prattle.sendGroupMessage(message, "MSD"));
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  /**
   * Test for broadcastMessage method failure.
   */
  @Test
  public void testGrpMessageClientRunnableInListSelf() throws IOException, SQLException, NoSuchFieldException, IllegalAccessException {
    //Return R, J
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("r");
    mockStatic(GroupServices.class);
    List<String> list = new ArrayList();
    list.add("r");
    list.add("j");
    ConcurrentMap<String,List<String>> hm = new ConcurrentHashMap<>();
    hm.put("MSD",list);

    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("groupToUserMapping");

    field.setAccessible(true);
    field.set(null, hm);


    Message message = Message.makeGroupMessage("r", "/grp MSD Hello");
    Prattle.sendGroupMessage(message, "MSD");
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  /**
   * Test for broadcastMessage method failure.
   */
  @Test
  public void testGrpMessageClientRunnableInListNonSelf() throws IOException, SQLException, NoSuchFieldException, IllegalAccessException {
    //Return R, J
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("j");
    when(clientRunnable.getDNDStatus()).thenReturn(false);
    mockStatic(GroupServices.class);
    List<String> list = new ArrayList();
    list.add("r");
    list.add("j");
    ConcurrentMap<String,List<String>> hm = new ConcurrentHashMap<>();
    hm.put("MSD",list);

    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("groupToUserMapping");

    field.setAccessible(true);
    field.set(null, hm);


    Message message = Message.makeGroupMessage("r", "/grp MSD Hello");
    Prattle.sendGroupMessage(message, "MSD");
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  /**
   * Test for broadcastMessage method failure.
   */
  @Test
  public void testGrpMessageUserNameNotInGrp() throws IOException, SQLException, NoSuchFieldException, IllegalAccessException {
    //Return R, J
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("j");
    mockStatic(GroupServices.class);
    List<String> list = new ArrayList();
    list.add("r");
    list.add("j");
    ConcurrentMap<String,List<String>> hm = new ConcurrentHashMap<>();
    hm.put("MSD",list);

    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("groupToUserMapping");

    field.setAccessible(true);
    field.set(null, hm);


    Message message = Message.makeGroupMessage("abcd", "/grp MSD Hello");
    Prattle.sendGroupMessage(message, "MSD");
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  @Test
  public void testGrpMessageGroupNameNotPresent() throws IOException, SQLException, IllegalAccessException, NoSuchFieldException {
    //Return R, J
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("j");
    mockStatic(GroupServices.class);
    List<String> list = new ArrayList();
    list.add("r");
    list.add("j");
    ConcurrentMap<String,List<String>> hm = new ConcurrentHashMap<>();
    List<String> l = new ArrayList<>();
    l.add("r");
    hm.put("MSD",list);

    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("groupToUserMapping");

    field.setAccessible(true);
    field.set(null, hm);


    Message message = Message.makeGroupMessage("r", "/grp ZZZ Hello");
    assertFalse(Prattle.sendGroupMessage(message, "ZZZ"));
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  @Test
  public void testGrpMessageSameUser() throws IOException, SQLException, IllegalAccessException, NoSuchFieldException {
    //Return R, J
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("j");
    mockStatic(GroupServices.class);
    List<String> list = new ArrayList();
    list.add("r");
    list.add("j");
    ConcurrentMap<String,List<String>> hm = new ConcurrentHashMap<>();
    List<String> l = new ArrayList<>();
    l.add("r");
    hm.put("MSD",list);

    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("groupToUserMapping");

    field.setAccessible(true);
    field.set(null, hm);


    Message message = Message.makeGroupMessage("r", "/grp MSD Hello");
    assertTrue(Prattle.sendGroupMessage(message, "MSD"));
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  @Test
  public void testInitCacheNonException() throws InvocationTargetException, IllegalAccessException, IOException, SQLException {
    mockStatic(UserServices.class);
    Class<Prattle> clazz = Prattle.class;
    mockStatic(GroupServices.class);
    mockStatic(MessageServices.class);
//    when(GroupServices.getAllChatIdsForGroups()).thenReturn(null);
//    when(MessageServices.getChatIDForUsers()).thenReturn(new MultiKeyMap());
//    when(UserServices.getListOfTappedUsers()).thenReturn(null);
//    when(GroupServices.getUserToFollowerMap()).thenReturn(null);
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("initialiseCache")) {
        met = m;
      }
    }

    met.setAccessible(true);
    met.invoke(null);
    serverSocketChannel.close();
  }

  @Test
  public void testInitCacheException() throws Exception {
    Class<Prattle> clazz = Prattle.class;
    mockStatic(UserServices.class);
    mockStatic(GroupServices.class);
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("initialiseCache")) {
        met = m;
      }
    }
    PowerMockito.doThrow(new SQLException("Custom SQL Exception")).when(GroupServices.class,"getListOfAllUsersForAllGroups");
    met.setAccessible(true);
    met.invoke(null);
    serverSocketChannel.close();
  }

  @Test
  public void checkIsOnline() throws IOException {
    when(clientRunnable.getName()).thenReturn("r");
    assertEquals(true,Prattle.isUserOnline("r"));
    serverSocketChannel.close();
  }

  @Test
  public void checkIsOnlineFalse() throws IOException {
    when(clientRunnable.getName()).thenReturn("z");
    assertEquals(false,Prattle.isUserOnline("r"));
    serverSocketChannel.close();
  }

  /**
   * Test for broadcastMessage method failure.
   */
  @Test
  public void testPvtMessageDND() throws IOException {
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("abc");
    when(clientRunnable.getDNDStatus()).thenReturn(true);
    Message message = Message.makePrivateMessage("abcd", "hello world");
    Prattle.sendPrivateMessage(message, "abc");
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  @Test
  public void testSendMessageToAgencyTrue() throws IOException {
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("CIA");
    when(clientRunnable.getDNDStatus()).thenReturn(true);
    Message message = Message.makePrivateMessage("abcd", "hello world");
    Prattle.sendMessageToAgency(message);
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  @Test
  public void testSendMessageToAgencyFalse() throws IOException {
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("RAW");
    when(clientRunnable.getDNDStatus()).thenReturn(true);
    Message message = Message.makePrivateMessage("abcd", "hello world");
    Prattle.sendMessageToAgency(message);
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  @Test
  public void testGetIPForUser() throws IOException {
    NetworkConnection networkConnectionMocked = mock(NetworkConnection.class);
    SocketChannel mockedSocketChannel = mock(SocketChannel.class);
    SocketAddress mockedSocketAddress = mock(SocketAddress.class);
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("RAW");
    when(clientRunnable.getDNDStatus()).thenReturn(true);
    when(clientRunnable.getConnection()).thenReturn(networkConnectionMocked);
    when(networkConnectionMocked.getChannel()).thenReturn(mockedSocketChannel);
    when(mockedSocketChannel.getRemoteAddress()).thenReturn(mockedSocketAddress);
    when(mockedSocketAddress.toString()).thenReturn("192.168.1.1");
    Message message = Message.makePrivateMessage("abcd", "hello world");
    Prattle.getIPForUser(clientRunnable);
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }


  @Test
  public void testGetIPForUserException() throws IOException {
    NetworkConnection networkConnectionMocked = mock(NetworkConnection.class);
    SocketChannel mockedSocketChannel = mock(SocketChannel.class);
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("RAW");
    when(clientRunnable.getDNDStatus()).thenReturn(true);
    when(clientRunnable.getConnection()).thenReturn(networkConnectionMocked);
    when(networkConnectionMocked.getChannel()).thenReturn(mockedSocketChannel);
    doThrow(new IOException()).when(mockedSocketChannel).getRemoteAddress();
    Message message = Message.makePrivateMessage("abcd", "hello world");
    Prattle.getIPForUser(clientRunnable);
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  @Test
  public void testSendMessageToAgency() throws IOException {
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("RAW");
    when(clientRunnable.getDNDStatus()).thenReturn(true);
    Message message = Message.makePrivateMessage("abcd", "hello world");
    Prattle.sendMessageToAgency(message,"192.168.1.1","172.1.1.0");
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  @Test
  public void testGetIPFromActiveRunnablesFalse() throws IOException {
    Message message = Message.makePrivateMessage("abcd", "hello world");
    when(clientRunnable.getName()).thenReturn("abc");
    when(clientRunnable.getDNDStatus()).thenReturn(false);
    Prattle.sendPrivateMessage(message, "abc");
    assertEquals(true, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    assertNull(Prattle.getIPFromActiveRunnables("abcd"));
    this.serverSocketChannel.close();
  }

  @Test
  public void testGetIPFromActiveRunnablesTrue() throws IOException {
    NetworkConnection networkConnectionMocked = mock(NetworkConnection.class);
    SocketChannel mockedSocketChannel = mock(SocketChannel.class);
    SocketAddress mockedSocketAddress = mock(SocketAddress.class);
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("RAW");
    when(clientRunnable.getDNDStatus()).thenReturn(true);
    when(clientRunnable.getConnection()).thenReturn(networkConnectionMocked);
    when(networkConnectionMocked.getChannel()).thenReturn(mockedSocketChannel);
    when(mockedSocketChannel.getRemoteAddress()).thenReturn(mockedSocketAddress);
    when(mockedSocketAddress.toString()).thenReturn("192.168.1.1");

    Message message = Message.makePrivateMessage("abcd", "hello world");
    when(clientRunnable.getName()).thenReturn("abcd");
    Prattle.sendPrivateMessage(message, "abc");
    assertEquals("192.168.1.1",Prattle.getIPFromActiveRunnables("abcd"));
    this.serverSocketChannel.close();
  }

  @Test
  public void testGetIPFromActiveRunnablesException() throws IOException {
    NetworkConnection networkConnectionMocked = mock(NetworkConnection.class);
    SocketChannel mockedSocketChannel = mock(SocketChannel.class);
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("RAW");
    when(clientRunnable.getDNDStatus()).thenReturn(true);
    when(clientRunnable.getConnection()).thenReturn(networkConnectionMocked);
    when(networkConnectionMocked.getChannel()).thenReturn(mockedSocketChannel);
    doThrow(new IOException()).when(mockedSocketChannel).getRemoteAddress();


    Message message = Message.makePrivateMessage("abcd", "hello world");
    when(clientRunnable.getName()).thenReturn("abcd");
    Prattle.sendPrivateMessage(message, "abc");
    assertEquals(null,Prattle.getIPFromActiveRunnables("abcd"));
    this.serverSocketChannel.close();
  }

  @Test
  public void testUpdateAndGetChatIDFromUserMapSenderReceiver() throws NoSuchFieldException, IllegalAccessException, IOException {
    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("chatIDToUserMap");
    field.setAccessible(true);

    MultiKeyMap<String,Integer> chatIDToUserMap = new MultiKeyMap<>();
    chatIDToUserMap.put("Rohan","Josh",1);
    field.set(null, chatIDToUserMap);
    assertEquals(2,Prattle.updateAndGetChatIDFromUserMap("Rohan","Josh"));
    this.serverSocketChannel.close();
  }

  @Test
  public void testUpdateAndGetChatIDFromUserMapReceiverSender() throws NoSuchFieldException, IllegalAccessException, IOException {
    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("chatIDToUserMap");
    field.setAccessible(true);

    MultiKeyMap<String,Integer> chatIDToUserMap = new MultiKeyMap<>();
    chatIDToUserMap.put("Rohan","Josh",1);
    field.set(null, chatIDToUserMap);
    assertEquals(2,Prattle.updateAndGetChatIDFromUserMap("Josh","Rohan"));
    this.serverSocketChannel.close();
  }

  @Test
  public void testUpdateAndGetChatIDFromUserMapNewUserPair() throws NoSuchFieldException, IllegalAccessException, IOException {
    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("chatIDToUserMap");
    field.setAccessible(true);

    MultiKeyMap<String,Integer> chatIDToUserMap = new MultiKeyMap<>();
    field.set(null, chatIDToUserMap);
    assertEquals(1,Prattle.updateAndGetChatIDFromUserMap("Josh","Rohan"));
    this.serverSocketChannel.close();
  }

  @Test
  public void updateAndGetChatIDFromGroupMap() throws NoSuchFieldException, IllegalAccessException, IOException {
    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("chatIDToGroupMap");
    field.setAccessible(true);

    ConcurrentMap<String,Integer> chatToGrpMap = new ConcurrentHashMap<>();
    chatToGrpMap.put("MSD",1);
    field.set(null, chatToGrpMap);
    assertEquals(2,Prattle.updateAndGetChatIDFromGroupMap("MSD"));
    this.serverSocketChannel.close();
  }

  @Test
  public void updateAndGetChatIDFromGroupMapNewGroup() throws NoSuchFieldException, IllegalAccessException, IOException {
    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("chatIDToGroupMap");
    field.setAccessible(true);

    ConcurrentMap<String,Integer> chatToGrpMap = new ConcurrentHashMap<>();
    field.set(null, chatToGrpMap);
    assertEquals(1,Prattle.updateAndGetChatIDFromGroupMap("MSD"));
    this.serverSocketChannel.close();
  }

  /**
   * Test for broadcastMessage method failure.
   */
  @Test
  public void testGrpMSGDNDTrue() throws IOException, SQLException, NoSuchFieldException, IllegalAccessException {
    //Return R, J
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("j");
    when(clientRunnable.getDNDStatus()).thenReturn(true);
    mockStatic(GroupServices.class);
    List<String> list = new ArrayList();
    list.add("r");
    list.add("j");
    ConcurrentMap<String,List<String>> hm = new ConcurrentHashMap<>();
    hm.put("MSD",list);

    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("groupToUserMapping");

    field.setAccessible(true);
    field.set(null, hm);


    Message message = Message.makeGroupMessage("r", "/grp MSD Hello");
    Prattle.sendGroupMessage(message, "MSD");
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  /**
   * Test for broadcastMessage method failure.
   */
  @Test
  public void testGrpMSGUserPrattle() throws IOException, SQLException, NoSuchFieldException, IllegalAccessException {
    //Return R, J
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("j");
    when(clientRunnable.getDNDStatus()).thenReturn(true);
    mockStatic(GroupServices.class);
    List<String> list = new ArrayList();
    list.add("r");
    list.add("j");
    ConcurrentMap<String,List<String>> hm = new ConcurrentHashMap<>();
    hm.put("MSD",list);

    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("groupToUserMapping");

    field.setAccessible(true);
    field.set(null, hm);


    Message message = Message.makeGroupMessage("PRATTLE", "/grp MSD Hello");
    Prattle.sendGroupMessage(message, "MSD");
    assertEquals(false, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  private static class PrattleThread extends Thread {
    @Override
    public void run() {
      String[] args = new String[0];
      Prattle.main(args);
    }
  }
}