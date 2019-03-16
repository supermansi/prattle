package edu.northeastern.ccs.im.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.services.UserServices;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This is a test class for the ClientRunnable class.
 */
@RunWith(MockitoJUnitRunner.class)
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

    public void delete(){
        UserDAO.getInstance().deleteUser("t");
    }

    private void register() {
        Message testMessage1 = Message.makeRegisterationMessage("t","t t t t t");;
        List<Message> nameList = new ArrayList();
        nameList.add(testMessage1);

        clientRunnable.enqueueMessage(testMessage1);

        GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);
        when(connection.iterator()).thenReturn(itr);
        clientRunnable.run();
    }


    /**
     * Test for the getUserId method.
     */
    @Test
    public void testGetUserId() {
        List<Message> nameList = new ArrayList();
        Message testMsg0 = Message.makeSimpleLoginMessage("r","a");;
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
    public void testTerminateMessage() {
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        List<Message> nameList = new ArrayList();
        Message testMessage1 = Message.makeSimpleLoginMessage("r","a");;
        Message testMessage2 = Message.makeQuitMessage("Rohan");
        nameList.add(testMessage1);
        nameList.add(testMessage2);
        GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

        Message tm1 = Message.makeSimpleLoginMessage("r","a");;
        clientRunnable.enqueueMessage(tm1);
        when(connection.sendMessage(any())).thenReturn(true);
        when(connection.iterator()).thenReturn(itr);
        clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
        clientRunnable.run();
        clientRunnable.run();
        verify(connection, times(3)).sendMessage(messageCaptor.capture());
        List<Message> capturedMsgs = messageCaptor.getAllValues();
        assertEquals(3, capturedMsgs.size());
        assertEquals(false, capturedMsgs.get(1).terminate());
        verify(connection).close();
    }

    /**
     * Testing for more than one login message being enqueued.
     */
    @Test
    public void testDualHelloMessage() {
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        List<Message> nameList = new ArrayList();
        Message testMessage1 = Message.makeSimpleLoginMessage("r","a");;
        Message testMessage2 = Message.makeSimpleLoginMessage("r","a");;
        nameList.add(testMessage1);
        nameList.add(testMessage2);
        GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

        Message tm1 = Message.makeSimpleLoginMessage("r","a");;
        clientRunnable.enqueueMessage(tm1);
        when(connection.sendMessage(any())).thenReturn(true);
        when(connection.iterator()).thenReturn(itr);
        clientRunnable.run();
        clientRunnable.run();
        verify(connection,times(2)).sendMessage(messageCaptor.capture());
        List<Message> capturedMsgs = messageCaptor.getAllValues();
        assertEquals(2, capturedMsgs.size());
        assertEquals(true, capturedMsgs.get(0).isInitialization());

    }

    /**
     * Test for the run method to enqueue and send messages.
     */
    @Test
    public void testRun() {
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        Message testMessage1 = Message.makeBroadcastMessage("Rohan", "Random1");
        Message testMessage2 = Message.makeBroadcastMessage("Rohan", "Random2");
        List<Message> nameList = new ArrayList();
        Message testMsg0 = Message.makeSimpleLoginMessage("r","a");
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
        verify(connection, times(4)).sendMessage(messageCaptor.capture());
        List<Message> capturedMsgs = messageCaptor.getAllValues();
        assertEquals(4, capturedMsgs.size());
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
    public void testRunForDifferentNames() {
        when(connection.sendMessage(any())).thenReturn(true);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        //when(connection.sendMessage(testMsg)).thenReturn(true);
        Message testMessage1 = Message.makeSimpleLoginMessage("r","a");;
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
        verify(connection,times(2)).sendMessage(messageCaptor.capture());
        List<Message> capturedMsgs = messageCaptor.getAllValues();
        assertEquals(2, capturedMsgs.size());
        assertEquals("Prattle", capturedMsgs.get(0).getName());
        clientRunnable.terminateClient();
        verify(connection).close();
    }

    /**
     * Test run method when message sender is null.
     */
    @Test
    public void testRunForNullMessageName() {
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        List<Message> nameList = new ArrayList();
        Message testMessage1 = Message.makeSimpleLoginMessage("r","a");;
        Message testMessage2 = Message.makeBroadcastMessage(null, "Random");
        nameList.add(testMessage1);
        nameList.add(testMessage2);
        GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);
        when(connection.iterator()).thenReturn(itr);
        clientRunnable.run();
        clientRunnable.setFuture(Mockito.mock(ScheduledFuture.class));
        clientRunnable.run();
        verify(connection,times(2)).sendMessage(messageCaptor.capture());
        List<Message> capturedMsgs = messageCaptor.getAllValues();
        assertEquals(2, capturedMsgs.size());
        assertEquals("Prattle", capturedMsgs.get(0).getName());
        //Mockito.verify(connection).close();
    }

    /**
     * Test for the run method.
     */
    @Test
    public void testBasicRunMethod() {
        when(connection.sendMessage(any())).thenReturn(true);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        Message testMessage1 = Message.makeSimpleLoginMessage("r","a");;
        Message testMessage2 = Message.makeBroadcastMessage("Rohan", "Random2");
        List<Message> nameList = new ArrayList();
        nameList.add(testMessage1);
        nameList.add(testMessage2);

        clientRunnable.enqueueMessage(testMessage2);

        GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);
        when(connection.iterator()).thenReturn(itr);
        clientRunnable.run();
        clientRunnable.run();
        verify(connection,times(3)).sendMessage(messageCaptor.capture());
        List<Message> capturedMsgs = messageCaptor.getAllValues();
        assertEquals(3, capturedMsgs.size());
        assertEquals("Rohan", capturedMsgs.get(0).getName());
    }

    /**
     * Test method to terminate a ClientRunnable.
     */
    @Test
    public void testTerminate() {
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
        verify(connection).close();
    }

    @Test
    public void testPrivate() {
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
    public void testRetrieveMessageForUser() throws InvocationTargetException, IllegalAccessException {
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
        Message msg = Message.makeRetrieveUserMessage("r","/retrieveUSR j");
        met.invoke(clientRunnable,msg);
    }

    @Test
    public void testRetrieveMessageForGRP() throws InvocationTargetException, IllegalAccessException {
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
        Message msg = Message.makeRetrieveGroupMessage("r","/retrieveUSR MSD");
        met.invoke(clientRunnable,msg);
    }

    @Test
    public void testProcessMessagePVT() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
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
        try{
            met.invoke(clientRunnable,msg);
        }catch (Exception e){
            assert true;
        }

    }

    @Test
    public void testProcessMessageGRPNotExist() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
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
        Message msg = Message.makeGroupMessage("test", "/grp ZZZ hello world");
        met.invoke(clientRunnable,msg);
    }

    @Test
    public void testProcessMessageGRP() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
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
        Message msg1 = Message.createGroupMessage("test", "/createGrp TEST");
        Message msg2 = Message.makeAddUserToGroupMessage("test", "/addUsrToGrp TEST z");
        Message msg3 = Message.makeGroupMessage("test", "/grp TEST hello world");
        Message msg4 = Message.makeRemoveUserMessage("test", "/grp TEST z");
        Message msg5 = Message.deleteGroupMessage("test", "/grp TEST");
        met.invoke(clientRunnable,msg1);
        met.invoke(clientRunnable,msg2);
        met.invoke(clientRunnable,msg3);
        met.invoke(clientRunnable,msg4);
        met.invoke(clientRunnable,msg5);
    }

    @Test
    public void testUserFunctions() throws InvocationTargetException, IllegalAccessException{
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
        Message msg1 = Message.makeUpdateFirstNameMessage("test", "/createGrp XYZ");
        Message msg2 = Message.makeUpdateLastNameMessage("test", "/addUsrToGrp XYZ z");
        Message msg3 = Message.makeUpdateEmailMessage("test", "/addUsrToGrp XYZ");
        Message msg4 = Message.makeUpdatePasswordMessage("test", "/grp test");
        met.invoke(clientRunnable,msg1);
        met.invoke(clientRunnable,msg2);
        met.invoke(clientRunnable,msg3);
        met.invoke(clientRunnable,msg4);
    }

    @Test
    public void testRegisteration(){
        Message testMessage1 = Message.makeRegisterationMessage("t","t t t t t");;
        List<Message> nameList = new ArrayList();
        nameList.add(testMessage1);

        clientRunnable.enqueueMessage(testMessage1);

        GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);
        when(connection.iterator()).thenReturn(itr);
        clientRunnable.run();
        UserDAO.getInstance().deleteUser("t");
    }

    @Test
    public void processUsrGrpRet() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        clientRunnable.setName("r");
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
        met.invoke(clientRunnable,msg1);
        met.invoke(clientRunnable,msg2);
    }

    @Test
    public void testDuplicateRegisteration(){
        when(connection.sendMessage(any())).thenReturn(true);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        Message testMessage1 = Message.makeRegisterationMessage("a","r r r r r");;
        List<Message> nameList = new ArrayList();
        nameList.add(testMessage1);

        clientRunnable.enqueueMessage(testMessage1);

        GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);
        when(connection.iterator()).thenReturn(itr);
        clientRunnable.run();
        verify(connection).sendMessage(messageCaptor.capture());
        List<Message> capturedMsgs = messageCaptor.getAllValues();
        assertEquals("NAK 7 Prattle 42 Either Illegal name or useralready exists.", capturedMsgs.get(0).toString());
    }

    @Test
    public void testFailedLogin(){
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        Message testMessage1 = Message.makeSimpleLoginMessage("y","z");;
        List<Message> nameList = new ArrayList();
        nameList.add(testMessage1);
        GenericMessageIterator<Message> itr = new GenericMessageIterator(nameList);

        when(connection.iterator()).thenReturn(itr);
        clientRunnable.run();
        verify(connection).sendMessage(messageCaptor.capture());
        List<Message> capturedMsgs = messageCaptor.getAllValues();
        assertEquals("NAK 7 Prattle 28 Invalid username or password", capturedMsgs.get(0).toString());
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
