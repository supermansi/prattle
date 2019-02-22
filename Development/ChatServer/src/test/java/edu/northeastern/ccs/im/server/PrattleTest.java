package edu.northeastern.ccs.im.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class PrattleTest {
  @Mock
  private ClientRunnable clientRunnable;

  @Mock
  private ScheduledExecutorService executor;

  @Rule
  public MockitoRule mockitoRule = MockitoJUnit.rule();

  private static final int PORT = 4546;
  private static final int CLIENT_CHECK_DELAY = 200;

  private ServerSocketChannel serverSocketChannel;
  private NetworkConnection networkConnection;
  private ConcurrentLinkedQueue<ClientRunnable> queue;

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
    queue = (ConcurrentLinkedQueue<ClientRunnable>) field.get("Prattle");
    queue.add(clientRunnable);
  }

  @Test
  public void testBroadcastMessageTrue() throws IOException {
    Message message = Message.makeBroadcastMessage("abcd", "hello world");
    Prattle.broadcastMessage(message);
    assertEquals(true,clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  @Test
  public void testBroadcastMessageFalse() throws IOException {
    when(clientRunnable.isInitialized()).thenReturn(false);
    Message message = Message.makeBroadcastMessage("abcd", "hello world");
    Prattle.broadcastMessage(message);
    assertEquals(false,clientRunnable.isInitialized());
    assertTrue(!message.equals(null));
    serverSocketChannel.close();
  }

  @Test
  public void testBroadcastMessageNull() throws IOException {
    Prattle.broadcastMessage(null);
    assertEquals(true,clientRunnable.isInitialized());
    serverSocketChannel.close();
  }

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

  private static class PrattleThread extends Thread {
    @Override
    public void run() {
      String[] args = new String[0];
      Prattle.main(args);
    }
  }

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
    when(executor.scheduleAtFixedRate(clientRunnable,CLIENT_CHECK_DELAY,CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS)).thenReturn(scheduledFuture);
    met.invoke(null,serverSocketChannel,executor);
    serverSocketChannel.close();
  }

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
    when(executor.scheduleAtFixedRate(clientRunnable,CLIENT_CHECK_DELAY,CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS)).thenReturn(scheduledFuture);
    met.invoke(null,serverSocketChannel,executor);
    this.serverSocketChannel.close();
  }

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
    when(executor.scheduleAtFixedRate(clientRunnable,CLIENT_CHECK_DELAY,CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS)).thenReturn(scheduledFuture);
    met.invoke(null,serverSocketChannel,executor);
    assertEquals(getDataFromFile().contains("AssertionError"),true);
    this.serverSocketChannel.close();
  }

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
    when(executor.scheduleAtFixedRate(clientRunnable,CLIENT_CHECK_DELAY,CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS)).thenReturn(scheduledFuture);
    met.invoke(null,serverSocketChannel,executor);
    assertEquals(getDataFromFile().contains("IOException"),true);
    this.serverSocketChannel.close();
  }


  private String getDataFromFile() throws IOException {
    FileReader fr;
    File f;
    BufferedReader br;
    f = new File("edu.northeastern.ccs.im.ChatLogger.log");
    br = new BufferedReader(new FileReader(f));
    StringBuilder result = new StringBuilder();
    String next;

    while((next = br.readLine()) != null){
      result.append(next + "\n");
    }
    return result.toString();
  }
}