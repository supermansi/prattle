package edu.northeastern.ccs.im.server;

import static org.junit.Assert.assertEquals;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;

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
    Mockito.when(clientRunnable.isInitialized()).thenReturn(true);

    Class clazz = Prattle.class;
    Field field = clazz.getDeclaredField("active");

    field.setAccessible(true);
    queue = (ConcurrentLinkedQueue<ClientRunnable>) field.get("Prattle");
    queue.add(clientRunnable);
  }

  @Test
  public void testBroadcastMessageNull() throws IOException {
    Prattle.broadcastMessage(null);
    serverSocketChannel.close();
  }
  @Test
  public void testBroadcastMessage() throws IOException {
    Message message = Message.makeBroadcastMessage("abcd", "hello world");
    Prattle.broadcastMessage(message);
    serverSocketChannel.close();
  }

  @Test
  public void testStopServer() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Class clazz = Prattle.class;
    Method method = clazz.getDeclaredMethod("stopServer");
    method.setAccessible(true);
    method.invoke(new PrattleThread());
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

  private static class PrattleThread extends Thread {
    @Override
    public void run() {
      String[] args = new String[0];
      Prattle.main(args);
    }
  }
}