package edu.northeastern.ccs.im;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NetworkConnectionTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  SocketChannel sockChan;
  NetworkConnection networkConnection;
  Selector selector = new Selector() {
    @Override
    public boolean isOpen() {
      return false;
    }

    @Override
    public SelectorProvider provider() {
      return null;
    }

    @Override
    public Set<SelectionKey> keys() {
      return null;
    }

    @Override
    public Set<SelectionKey> selectedKeys() {
      return null;
    }

    @Override
    public int selectNow() throws IOException {
      return 0;
    }

    @Override
    public int select(long timeout) throws IOException {
      return 0;
    }

    @Override
    public int select() throws IOException {
      return 0;
    }

    @Override
    public Selector wakeup() {
      return null;
    }

    @Override
    public void close() throws IOException {
      throw new IOException();
    }
  };
  SocketChannel socketChannel1 = new SocketChannel(SelectorProvider.provider()) {
    @Override
    public SocketChannel bind(SocketAddress local) throws IOException {
      return null;
    }

    @Override
    public <T> SocketChannel setOption(SocketOption<T> name, T value) throws IOException {
      return null;
    }

    @Override
    public SocketChannel shutdownInput() throws IOException {
      return null;
    }

    @Override
    public SocketChannel shutdownOutput() throws IOException {
      return null;
    }

    @Override
    public Socket socket() {
      return null;
    }

    @Override
    public boolean isConnected() {
      return false;
    }

    @Override
    public boolean isConnectionPending() {
      return false;
    }

    @Override
    public boolean connect(SocketAddress remote) throws IOException {
      return false;
    }

    @Override
    public boolean finishConnect() throws IOException {
      return false;
    }

    @Override
    public SocketAddress getRemoteAddress() throws IOException {
      return null;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
      return 0;
    }

    @Override
    public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      return 0;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
      throw new IOException();
    }

    @Override
    public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return 0;
    }

    @Override
    public SocketAddress getLocalAddress() throws IOException {
      return null;
    }

    @Override
    public <T> T getOption(SocketOption<T> name) throws IOException {
      return null;
    }

    @Override
    public Set<SocketOption<?>> supportedOptions() {
      return null;
    }

    @Override
    protected void implCloseSelectableChannel() throws IOException {

    }

    @Override
    protected void implConfigureBlocking(boolean block) throws IOException {
      throw new IOException();
    }
  };

  @Before
  public void init() throws IOException {
    sockChan = mock(SocketChannel.class);
    when(sockChan.configureBlocking(false)).thenReturn(null);
  }

  @Test
  public void testSendMessage() {
    Message testMessage1 = Message.makeSimpleLoginMessage("Rohan");
    networkConnection = new NetworkConnection(sockChan);
    networkConnection.sendMessage(testMessage1);
  }

  @Test
  public void testSelectorClose() {
    networkConnection = new NetworkConnection(sockChan);
    networkConnection.close();
  }

  @Test
  public void getIterator() {
    networkConnection = new NetworkConnection(sockChan);
    networkConnection.iterator();
  }

  @Test(expected = AssertionError.class)
  public void testSendMessageException() {
    networkConnection = new NetworkConnection(socketChannel1);
  }

  @Test(expected = AssertionError.class)
  public void testCloseException() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    networkConnection = new NetworkConnection(sockChan);
    Field privateField = Class.forName(NetworkConnection.class.getName()).getDeclaredField("selector");
    privateField.setAccessible(true);
    privateField.set(networkConnection, selector);
    networkConnection.close();
  }

  @Test
  public void testFailedWrite() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    Message testMessage1 = Message.makeSimpleLoginMessage("Rohan");
    networkConnection = new NetworkConnection(sockChan);
    Field privateField = Class.forName(NetworkConnection.class.getName()).getDeclaredField("channel");
    privateField.setAccessible(true);
    privateField.set(networkConnection, socketChannel1);

    assertEquals(false, networkConnection.sendMessage(testMessage1));
  }

  @Test
  public void testReadArgument() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("readArgument")) {
        met = m;
      }
    }
    met.setAccessible(true);
    CharBuffer fb = CharBuffer.allocate(10);
    fb.put('a');
    fb.put(1, '1');
    assertNotEquals("", met.invoke(obj, fb).toString());
  }

  @Test
  public void testReadArgumentWithZeroLength() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("readArgument")) {
        met = m;
      }
    }
    met.setAccessible(true);
    CharBuffer fb = CharBuffer.allocate(10);
    fb.put('a');
    fb.put(1, '0');
    assertNotEquals("", met.invoke(obj, fb));
  }

  @Test(expected = InvocationTargetException.class)
  public void testReadArgumentsFailedAssert() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("readArgument")) {
        met = m;
      }
    }
    met.setAccessible(true);
    CharBuffer fb = CharBuffer.allocate(10);
    fb.put(1, 'a');
    assertNotEquals("", met.invoke(obj, fb));
  }

  @Test
  public void testPerformWriteAndReturn() throws InvocationTargetException, IllegalAccessException {
    //Message testMessage1 = Message.makeSimpleLoginMessage("Rohan");
    networkConnection = new NetworkConnection(sockChan);
    //networkConnection.sendMessage(testMessage1);
    Class<NetworkConnection> clazz = NetworkConnection.class;
    Method method[] = clazz.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("performWriteAndReturn")) {
        met = m;
      }
    }
    met.setAccessible(true);
    ByteBuffer wrapper = ByteBuffer.wrap("".getBytes());
    assertEquals(true,met.invoke(networkConnection,wrapper));

  }

  @Test
  public void testHasNext() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("hasNext")) {
        met = m;
      }
    }
    met.setAccessible(true);
    assertEquals(false, met.invoke(obj));
  }

  @Test
  public void testHasNextWithInjectedMessage() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, NoSuchFieldException {

    Queue<Message> messages = new ConcurrentLinkedQueue<>();
    Message testMsg0 = Message.makeSimpleLoginMessage("Rohan");
    messages.add(testMsg0);
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("hasNext")) {
        met = m;
      }
    }
    met.setAccessible(true);
    Field privateField = Class.forName(NetworkConnection.class.getName()).getDeclaredField("messages");
    privateField.setAccessible(true);
    privateField.set(networkConnection, messages);

    assertEquals(true, met.invoke(obj));
  }

  @Test
  public void testHasNextWithInjectedSelector() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, NoSuchFieldException {

    Queue<Message> messages = new ConcurrentLinkedQueue<>();
    Message testMsg0 = Message.makeSimpleLoginMessage("Rohan");
    messages.add(testMsg0);
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("hasNext")) {
        met = m;
      }
    }
    met.setAccessible(true);

    Field privateField1 = Class.forName(NetworkConnection.class.getName()).getDeclaredField("selector");
    privateField1.setAccessible(true);
    privateField1.set(networkConnection, selector);

    assertEquals(false, met.invoke(obj));
  }

  @Test(expected = InvocationTargetException.class)
  public void testHasNextWithInjectedSelectorThrowIOException() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, NoSuchFieldException, IOException {

    selector = mock(Selector.class);
    when(selector.selectNow()).thenThrow(new IOException());
    when(selector.selectedKeys()).thenReturn(new HashSet<SelectionKey>());

    Queue<Message> messages = new ConcurrentLinkedQueue<>();
    Message testMsg0 = Message.makeSimpleLoginMessage("Rohan");
    messages.add(testMsg0);
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("hasNext")) {
        met = m;
      }
    }
    met.setAccessible(true);

    Field privateField1 = Class.forName(NetworkConnection.class.getName()).getDeclaredField("selector");
    privateField1.setAccessible(true);
    privateField1.set(networkConnection, selector);

    assertEquals(false, met.invoke(obj));
  }


  @Test
  public void testHasNextWithInjectedSelectorMockedReturnNonZero() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, NoSuchFieldException, IOException {

    Queue<Message> messages = new ConcurrentLinkedQueue<>();
    Message testMsg0 = Message.makeSimpleLoginMessage("Rohan");
    messages.add(testMsg0);
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("hasNext")) {
        met = m;
      }
    }
    met.setAccessible(true);
    selector = mock(Selector.class);
    when(selector.selectNow()).thenReturn(1);
    when(selector.selectedKeys()).thenReturn(new HashSet<SelectionKey>());
    SelectionKey key = mock(SelectionKey.class);
    when(key.isReadable()).thenReturn(true);
    Field privateField = Class.forName(NetworkConnection.class.getName()).getDeclaredField("key");
    privateField.setAccessible(true);
    privateField.set(networkConnection, key);
    Field privateField1 = Class.forName(NetworkConnection.class.getName()).getDeclaredField("selector");
    privateField1.setAccessible(true);
    privateField1.set(networkConnection, selector);

    assertEquals(true, met.invoke(obj));
  }

  @Test(expected = Exception.class)
  public void testHasNextWithInjectedSelectorMockedNonRreadableKey() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, NoSuchFieldException, IOException {

    Queue<Message> messages = new ConcurrentLinkedQueue<>();
    Message testMsg0 = Message.makeSimpleLoginMessage("Rohan");
    messages.add(testMsg0);
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("hasNext")) {
        met = m;
      }
    }
    met.setAccessible(true);
    selector = mock(Selector.class);
    when(selector.selectNow()).thenReturn(1);
    when(selector.selectedKeys()).thenReturn(new HashSet<SelectionKey>());
    SelectionKey key = mock(SelectionKey.class);
    when(key.isReadable()).thenReturn(false);
    Field privateField = Class.forName(NetworkConnection.class.getName()).getDeclaredField("key");
    privateField.setAccessible(true);
    privateField.set(networkConnection, key);
    Field privateField1 = Class.forName(NetworkConnection.class.getName()).getDeclaredField("selector");
    privateField1.setAccessible(true);
    privateField1.set(networkConnection, selector);

    assertEquals(true, met.invoke(obj));
  }






  @Test(expected = InvocationTargetException.class)
  public void testNextOnEmptyMEssageList() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, NoSuchFieldException {

    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("next")) {
        met = m;
      }
    }
    met.setAccessible(true);
    assertEquals(null, met.invoke(obj));
  }

  @Test
  public void testNextWithInjectedMessage() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, NoSuchFieldException {

    Queue<Message> messages = new ConcurrentLinkedQueue<>();
    Message testMsg0 = Message.makeSimpleLoginMessage("Rohan");
    messages.add(testMsg0);
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("next")) {
        met = m;
      }
    }
    met.setAccessible(true);
    Field privateField = Class.forName(NetworkConnection.class.getName()).getDeclaredField("messages");
    privateField.setAccessible(true);
    privateField.set(networkConnection, messages);

    assertEquals("HLO 5 Rohan 2 --", met.invoke(obj).toString());
  }

  @Test(expected = InvocationTargetException.class)
  public void testScanAndGetMinMessage() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, NoSuchFieldException {

    Queue<Message> messages = new ConcurrentLinkedQueue<>();
    Message testMsg0 = Message.makeSimpleLoginMessage("Rohan");
    messages.add(testMsg0);
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("scanAndGetMinMessage")) {
        met = m;
      }
    }
    met.setAccessible(true);
    Field privateField = Class.forName(NetworkConnection.class.getName()).getDeclaredField("messages");
    privateField.setAccessible(true);
    privateField.set(networkConnection, messages);
    CharBuffer fb = CharBuffer.allocate(20);
    //fb.put('a');
    fb.put(1, '1');
    fb.put(2, '1');
    fb.put(3, '1');
    fb.put(4, '1');
    fb.put(5, 'a');
    fb.put(6, '1');
    fb.put(7, '1');
    fb.put(8, '1');
    fb.put(9, 'a');
//    fb.put(10, '1');
//    fb.put(11, '1');
//    fb.put(12, '1');
//    fb.put(13, '1');
//    fb.put(14, '1');
//    fb.put(15, '1');
//    fb.put(16, '1');

    assertEquals(1, met.invoke(obj,0,fb).toString());
  }

  @Test(expected = InvocationTargetException.class)
  public void testScanAndGetMinMessageInitialPosNonZero() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, NoSuchFieldException {

    Queue<Message> messages = new ConcurrentLinkedQueue<>();
    Message testMsg0 = Message.makeSimpleLoginMessage("Rohan");
    messages.add(testMsg0);
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("scanAndGetMinMessage")) {
        met = m;
      }
    }
    met.setAccessible(true);
    Field privateField = Class.forName(NetworkConnection.class.getName()).getDeclaredField("messages");
    privateField.setAccessible(true);
    privateField.set(networkConnection, messages);
    CharBuffer fb = CharBuffer.allocate(20);
    fb.put(1, '1');
    fb.put(2, '1');
    fb.put(3, '1');
    fb.put(4, '1');
    fb.put(5, 'a');
    fb.put(6, '1');
    fb.put(7, '1');
    fb.put(8, '1');
    fb.put(9, 'a');


    assertEquals(1, met.invoke(obj,5,fb).toString());
  }
  @Test(expected = InvocationTargetException.class)
  public void testScanAndGetMinMessageFailAssert() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, NoSuchFieldException {

    Queue<Message> messages = new ConcurrentLinkedQueue<>();
    Message testMsg0 = Message.makeSimpleLoginMessage("Gori");
    messages.add(testMsg0);
    Class<?> innerClass = NetworkConnection.class.getDeclaredClasses()[0];

    Constructor<?> constructor = innerClass.getDeclaredConstructors()[0];
    constructor.setAccessible(true);
    NetworkConnection networkConnection = new NetworkConnection(sockChan);
    Object obj = constructor.newInstance(networkConnection);
    Method method[] = innerClass.getDeclaredMethods();
    Method met = null;
    for (Method m : method) {
      if (m.getName().contains("scanAndGetMinMessage")) {
        met = m;
      }
    }
    met.setAccessible(true);
    Field privateField = Class.forName(NetworkConnection.class.getName()).getDeclaredField("messages");
    privateField.setAccessible(true);
    privateField.set(networkConnection, messages);
    CharBuffer fb = CharBuffer.allocate(20);
    fb.put(1, '1');
    assertEquals(1, met.invoke(obj,5,fb).toString());
  }

}