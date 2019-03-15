package edu.northeastern.ccs.im.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Test class for the methods in the Prattle class.
 */
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
    field.set(clientRunnable,queue);
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
    assertEquals(true, getDataFromFile().contains("AssertionError"));
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
  public void testGrpMessage() throws IOException {
    Message message = Message.makeGroupMessage("abcd", "/grp MSD Hello");
    when(clientRunnable.getName()).thenReturn("j");
    Prattle.sendGroupMessage(message, "MSD");
    assertEquals(true, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  /**
   * Test for broadcastMessage method.
   */
  @Test
  public void testGrpMessageFalseCondn() throws IOException {
    Message message = Message.makeGroupMessage("j", "/grp MSD Hello");
    when(clientRunnable.getName()).thenReturn("j");
    Prattle.sendGroupMessage(message, "MSD");
    assertEquals(true, clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }
  /**
   * Test for broadcastMessage method failure.
   */
  @Test
  public void testGrpMessageFalse() throws IOException {
    when(clientRunnable.isInitialized()).thenReturn(false);
    when(clientRunnable.getName()).thenReturn("a");
    Message message = Message.makeGroupMessage("abcd", "/grp MSD Hello");
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