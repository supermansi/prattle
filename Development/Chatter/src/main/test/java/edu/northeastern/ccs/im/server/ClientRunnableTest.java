package edu.northeastern.ccs.im.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientRunnableTest {

  private static NetworkConnection clientSocket;
  public ClientRunnable clientRunnable;
  NetworkConnection connection;

  @Before
  public void init() {
    connection = mock(NetworkConnection.class);
    clientRunnable = new ClientRunnable(connection);
  }


  @Test
  public void testGetUserId() {
    List<Message> nameList = new ArrayList();
    Message testMsg0 = Message.makeSimpleLoginMessage("Rohan");
    Message testMsg1 = Message.makeBroadcastMessage("Rohan", "Test");
    nameList.add(testMsg0);
    nameList.add(testMsg1);
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
    assertNotSame(clientRunnable.getUserId(),-1);
  }

  @Test
  public void testRunForNullMethodName() {
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

  @Test
  public void testHandleIncomingMessage() {
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
    assertEquals(true, clientRunnable.isInitialized());
  }

  @Test
  public void testTerminateMessage() {
    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    List<Message> nameList = new ArrayList();
    Message testMessage1 = Message.makeSimpleLoginMessage("Rohan");
    Message testMessage2 = Message.makeQuitMessage("Rohan");
    nameList.add(testMessage1);
    nameList.add(testMessage2);
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

    Message tm1 = Message.makeSimpleLoginMessage("Rohan");
    clientRunnable.enqueueMessage(tm1);
    when(connection.sendMessage(any())).thenReturn(true);
    when(connection.iterator()).thenReturn(itr);
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    clientRunnable.run();
    clientRunnable.run();
    Mockito.verify(connection,times(2)).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals(2, capturedMsgs.size());
    assertEquals(true, capturedMsgs.get(1).terminate());
    Mockito.verify(connection).close();
  }

  @Test
  public void testDualHelloMessage() {
    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    List<Message> nameList = new ArrayList();
    Message testMessage1 = Message.makeSimpleLoginMessage("Rohan");
    Message testMessage2 = Message.makeSimpleLoginMessage("Rohan");
    nameList.add(testMessage1);
    nameList.add(testMessage2);
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

    Message tm1 = Message.makeSimpleLoginMessage("Rohan");
    clientRunnable.enqueueMessage(tm1);
    when(connection.sendMessage(any())).thenReturn(true);
    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
    clientRunnable.run();
    Mockito.verify(connection).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals(1, capturedMsgs.size());
    assertEquals(true, capturedMsgs.get(0).isInitialization());

  }

  @Test
  public void testRun() {
    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    Message testMessage1 = Message.makeBroadcastMessage("Rohan", "Random1");
    Message testMessage2 = Message.makeBroadcastMessage("Rohan", "Random2");
    List<Message> nameList = new ArrayList();
    Message testMsg0 = Message.makeSimpleLoginMessage("Rohan");
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
    Mockito.verify(connection,times(2)).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals(2, capturedMsgs.size());
    assertEquals("Rohan", capturedMsgs.get(0).getName());
    assertEquals("Random1", capturedMsgs.get(0).getText());
    assertEquals("Rohan", capturedMsgs.get(1).getName());
    assertEquals("Random2", capturedMsgs.get(1).getText());

  }

  @Test
  public void testIncorrectInitialization() {
    List<Message> nameList = new ArrayList();
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
    assertEquals(clientRunnable.isInitialized(),false);
  }

  @Test
  public void testRunForDifferentNames() {
    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    //when(connection.sendMessage(testMsg)).thenReturn(true);
    Message testMessage1 = Message.makeSimpleLoginMessage("Rohan");
    Message testMessage2 = Message.makeBroadcastMessage("Rohan", "Random2");
    Message testMessage3 = Message.makeBroadcastMessage("Josh", "Random2");
    List<Message> nameList = new ArrayList();
    nameList.add(testMessage1);
    nameList.add(testMessage2);
    nameList.add(testMessage3);
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
    clientRunnable.run();
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    clientRunnable.run();
    Mockito.verify(connection).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals(1, capturedMsgs.size());
    assertEquals("Bouncer", capturedMsgs.get(0).getName());
    Mockito.verify(connection).close();
  }

  @Test
  public void testRunForNullMessageName() {
    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    List<Message> nameList = new ArrayList();
    Message testMessage1 = Message.makeSimpleLoginMessage("Rohan");
    Message testMessage2 = Message.makeBroadcastMessage(null, "Random");
    nameList.add(testMessage1);
    nameList.add(testMessage2);
    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);
    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    clientRunnable.run();
    Mockito.verify(connection).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals(1, capturedMsgs.size());
    assertEquals("Bouncer", capturedMsgs.get(0).getName());
    Mockito.verify(connection).close();
  }

  @Test
  public void testBasicRunMethod() {
    when(connection.sendMessage(any())).thenReturn(true);

    ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
    Message testMessage1 = Message.makeSimpleLoginMessage("Rohan");
    Message testMessage2 = Message.makeBroadcastMessage("Rohan", "Random2");
    List<Message> nameList = new ArrayList();
    nameList.add(testMessage1);
    nameList.add(testMessage2);

    clientRunnable.enqueueMessage(testMessage2);

    GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);
    when(connection.iterator()).thenReturn(itr);
    clientRunnable.run();
    clientRunnable.run();
    Mockito.verify(connection).sendMessage(messageCaptor.capture());
    List<Message> capturedMsgs = messageCaptor.getAllValues();
    assertEquals(1, capturedMsgs.size());
    assertEquals("Rohan", capturedMsgs.get(0).getName());
  }

  @Test
  public void testTerminate() {
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
    clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
    clientRunnable.run();
    clientRunnable.terminateClient();
    Mockito.verify(connection).close();
  }

  @Test
  public void testTimerIsBehind() throws InvocationTargetException, IllegalAccessException {
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
    assertEquals(clientRunnable.isInitialized(),false);

  }

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
