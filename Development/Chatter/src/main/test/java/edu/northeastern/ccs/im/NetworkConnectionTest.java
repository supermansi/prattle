package edu.northeastern.ccs.im;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Set;

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
  @Before
  public void init() throws IOException {
    sockChan = mock(SocketChannel.class);
    when(sockChan.configureBlocking(false)).thenReturn(null);
  }

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

  SocketChannel socketChannel1 = new SocketChannel(SelectorProvider.provider() ) {
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
  @Test
  public void testSendMessage(){
    Message testMessage1 = Message.makeSimpleLoginMessage("Rohan");
    networkConnection = new NetworkConnection(sockChan);
    networkConnection.sendMessage(testMessage1);
  }

  @Test
  public void testSelectorClose(){
    networkConnection = new NetworkConnection(sockChan);
    networkConnection.close();
  }

  @Test
  public void getIterator(){
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

  @Test(expected = AssertionError.class)
  public void testFailedWrite() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    Message testMessage1 = Message.makeSimpleLoginMessage("Rohan");
    networkConnection = new NetworkConnection(socketChannel1);
    networkConnection.sendMessage(testMessage1);
  }

}