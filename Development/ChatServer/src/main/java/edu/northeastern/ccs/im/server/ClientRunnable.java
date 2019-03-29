package edu.northeastern.ccs.im.server;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;

import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
import edu.northeastern.ccs.im.model.Message.MsgType;

import edu.northeastern.ccs.im.ChatLogger;
import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.services.GroupServices;
import edu.northeastern.ccs.im.services.MessageServices;
import edu.northeastern.ccs.im.services.UserServices;

/**
 * Instances of this class handle all of the incoming communication from a single IM client.
 * Instances are created when the client signs-on with the server. After instantiation, it is
 * executed periodically on one of the threads from the thread pool and will stop being run only
 * when the client signs off.
 * <p>
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0 International
 * License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/. It
 * is based on work originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 *
 * @version 1.3
 */
public class ClientRunnable implements Runnable {
  /**
   * Utility class which we will use to send and receive communication to this client.
   */
  private NetworkConnection connection;

  /**
   * Id for the user for whom we use this ClientRunnable to communicate.
   */
  private int userId;

  /**
   * Name that the client used when connecting to the server.
   */
  private String name;

  /**
   * Whether this client has been initialized, set its user name, and is ready to receive messages.
   */
  private boolean initialized;

  /**
   * Whether this client has been terminated, either because he quit or due to prolonged
   * inactivity.
   */
  private boolean terminate;

  /**
   * The timer that keeps track of the clients activity.
   */
  private ClientTimer timer;

  /**
   * The future that is used to schedule the client for execution in the thread pool.
   */
  private ScheduledFuture<?> runnableMe;

  /**
   * Collection of messages queued up to be sent to this client.
   */
  private Queue<Message> waitingList;

  /**
   * Create a new thread with which we will communicate with this single client.
   *
   * @param network NetworkConnection used by this new client
   */
  public ClientRunnable(NetworkConnection network) {
    // Create the class we will use to send and receive communication
    connection = network;
    // Mark that we are not initialized
    initialized = false;
    // Mark that we are not terminated
    terminate = false;
    // Create the queue of messages to be sent
    waitingList = new ConcurrentLinkedQueue<>();
    // Mark that the client is active now and start the timer until we
    // terminate for inactivity.
    timer = new ClientTimer();
  }

  /**
   * Check to see for an initialization attempt and process the message sent.
   */
  private void checkForInitialization() {
    // Check if there are any input messages to read
    Iterator<Message> messageIter = connection.iterator();
    if (messageIter.hasNext()) {
      // If a message exists, try to use it to initialize the connection
      try {
        Message msg = messageIter.next();
        if (msg.isRegistration()) {
          List<String> regInfo = preProcessRegistrationInformation(msg.getText());
          processRegisteration(regInfo, msg);

        } else if (msg.isInitialization()) {
          if (setUserName(msg.getName()) && UserServices.login(msg.getName(), msg.getText())) {
            // Update the time until we terminate this client due to inactivity.
            timer.updateAfterInitialization();
            // Set that the client is initialized.
            initialized = true;
            enqueueMessage(Message.makeAckMessage(ServerConstants.SERVER_NAME, "Successfully loggedin"));
          } else {
            sendMessage(Message.makeNackMessage(ServerConstants.SERVER_NAME, "Invalid username or password"));
            initialized = false;
            terminate = true;
          }
        } else {
          initialized = false;
        }
      } catch (SQLException e) {
        ChatLogger.error("Error in connecting to database");
      }
    }
  }

  private void processRegisteration(List<String> regInfo, Message msg) throws SQLException {
    if (msg.getName() != null && UserServices.register(regInfo.get(0), regInfo.get(1),
            regInfo.get(2), regInfo.get(3), regInfo.get(4))) {
      setUserName(msg.getName());
      timer.updateAfterInitialization();
      // Set that the client is initialized.
      initialized = true;
      enqueueMessage(Message.makeAckMessage(ServerConstants.SERVER_NAME, "User successfully registered"));
    } else {
      initialized = false;
      sendMessage(Message.makeNackMessage(ServerConstants.SERVER_NAME, "Either Illegal name or user" +
              "already exists."));
      terminate = true;
    }
  }

  private List<String> preProcessRegistrationInformation(String text) {
    return Arrays.asList(text.split(" "));
  }


  /**
   * Check if the message is properly formed. At the moment, this means checking that the identifier
   * is set properly.
   *
   * @param msg Message to be checked
   * @return True if message is correct; false otherwise
   */
  private boolean messageChecks(Message msg) {
    if (msg.getName() == null) {
      return false;
    }
    return msg.getName().compareToIgnoreCase(getName()) == 0;
  }

  /**
   * Immediately send this message to the client. This returns if we were successful or not in our
   * attempt to send the message.
   *
   * @param message Message to be sent immediately.
   * @return True if we sent the message successfully; false otherwise.
   */
  private boolean sendMessage(Message message) {
    ChatLogger.info("\t" + message);
    return connection.sendMessage(message);
  }

  /**
   * Try allowing this user to set his/her user name to the given username.
   *
   * @param userName The new value to which we will try to set userName.
   * @return True if the username is deemed acceptable; false otherwise
   */
  private boolean setUserName(String userName) {
    boolean result = false;
    // Now make sure this name is legal.
    if (userName != null) {
      // Optimistically set this users ID number.
      setName(userName);
      userId = hashCode();
      result = true;
    } else {
      // Clear this name; we cannot use it. *sigh*
      userId = -1;
    }
    return result;
  }

  /**
   * Add the given message to this client to the queue of message to be sent to the client.
   *
   * @param message Complete message to be sent.
   */
  public void enqueueMessage(Message message) {
    waitingList.add(message);
  }

  /**
   * Get the name of the user for which this ClientRunnable was created.
   *
   * @return Returns the name of this client.
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name of the user for which this ClientRunnable was created.
   *
   * @param name The name for which this ClientRunnable.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the name of the user for which this ClientRunnable was created.
   *
   * @return Returns the current value of userName.
   */
  public int getUserId() {
    return userId;
  }

  /**
   * Return if this thread has completed the initialization process with its client and is read to
   * receive messages.
   *
   * @return True if this thread's client should be considered; false otherwise.
   */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Perform the periodic actions needed to work with this client.
   *
   * @see Thread#run()
   */
  public void run() {
    if (!initialized) {
      checkForInitialization();
    } else {
      handleIncomingMessages();
      handleOutgoingMessages();
    }
    setTerminateIfTimerIsBehind(timer);
    if (terminate) {
      terminateClient();
    }
  }

  /**
   * Method to terminate an unresponsive client.
   *
   * @param timer timer from the client
   */
  private void setTerminateIfTimerIsBehind(ClientTimer timer) {
    if (timer.isBehind()) {
      ChatLogger.error("Timing out or forcing off a user " + name);
      terminate = true;
    }
  }

  /**
   * Checks incoming messages and performs appropriate actions based on the type of message.
   */
  protected void handleIncomingMessages() {
    // Client has already been initialized, so we should first check
    // if there are any input
    // messages.
    Iterator<Message> messageIter = connection.iterator();
    if (messageIter.hasNext()) {
      // Get the next message
      Message msg = messageIter.next();
      // If the message is a broadcast message, send it out
      if (msg.terminate()) {
        // Stop sending the poor client message.
        terminate = true;
        // Reply with a quit message.
        enqueueMessage(Message.makeQuitMessage(name));
      } else {
        // Check if the message is legal formatted
        processMessage(msg);
      }
    }
  }

  /**
   * Method to take a message and send out based on type.
   *
   * @param msg message to be sent out
   */
  private void processMessage(Message msg) {
    if (messageChecks(msg)) {
      // Check for our "special messages"
      try {
        if (msg.isBroadcastMessage()) {
          // Check for our "special messages"
          Prattle.broadcastMessage(msg);
        } else if (msg.isPrivateMessage()) {
          String receiverId = getReceiverName(msg.getText());
          Prattle.sendPrivateMessage(msg, receiverId);
          MessageServices.addMessage(MsgType.PVT, msg.getName(), receiverId, msg.getText());
        } else if (msg.isGroupMessage()) {
          String receiverId = getReceiverName(msg.getText());
          Prattle.sendGroupMessage(msg, receiverId);
          MessageServices.addMessage(MsgType.GRP, msg.getName(), receiverId, msg.getText());
        } else if (msg.isCreateGroup()) {
          GroupServices.createGroup(getReceiverName(msg.getText()), msg.getName());
          sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully created group");
        } else if (msg.isDeleteGroup()) {
          GroupServices.deleteGroup(getReceiverName(msg.getText()), msg.getName()); //to do
          sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully deleated group");
        } else if (msg.isRetrieveGroup()) {
          retrieveGroupMessagesForGroup(msg);
        } else if (msg.isRetrieveUser()) {
          retrieveMessagesForUser(msg);
        } else if (msg.isUpdateFirstName()) {
          UserServices.updateFN(msg.getName(), msg.getText().split(" ")[1]);
          sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully updated First name");
        } else if (msg.isUpdateLastName()) {
          UserServices.updateLN(msg.getName(), msg.getText().split(" ")[1]);
          sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully updated Last name");
        } else if (msg.isUpdateEmail()) {
          UserServices.updateEmail(msg.getName(), msg.getText().split(" ")[1]);
          sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully updated Email");
        } else if (msg.isUpdatePassword()) {
          UserServices.updatePassword(msg.getName(), msg.getText().split(" ")[1]);
          sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully updated password");
        } else if (msg.isRemoveUser()) {

          GroupServices.removeUserFromGroup(getReceiverName(msg.getText()), msg.getName(), msg.getText().split(" ")[2]);
          sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully removed User From group");

        } else if (msg.isAddUserToGroup()) {
          GroupServices.addUserToGroup(getReceiverName(msg.getText()), msg.getName(), msg.getText().split(" ")[2]);
          sendMessageToClient(ServerConstants.SERVER_NAME, "Successfully Added User to group");
        } else if(msg.isDeactivateUser()) {
          sendMessageToClient(ServerConstants.SERVER_NAME, "Account successfully deleted.");
          UserServices.deleteUser(msg.getName());
          terminate = true;
        } else if (msg.isUserExists()) {
          if(UserServices.userExists(getReceiverName(msg.getText()))) {
            sendMessageToClient(ServerConstants.SERVER_NAME, "This user exists");
          } else {
            sendMessageToClient(ServerConstants.SERVER_NAME, "This user does not exist");
          }
        } else if(msg.isLastSeen()) {
          String receiver = getReceiverName(msg.getText());
          Long lastSeen = UserServices.getLastSeen(receiver);
          Date resultDate = new Date(lastSeen);
          SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
          sendMessageToClient(ServerConstants.SERVER_NAME, receiver + " last viewed messages at "
                  + sdf.format(resultDate));

        } else if(msg.isChangeGroupRestriction()) {
          //todo logic for change group restriction

          String[] split = msg.getText().split(" ");

          GroupServices.changeGroupRestrictions(split[1], msg.getName(), split[2]);
          sendMessageToClient(ServerConstants.SERVER_NAME, "Group restriction set successfully");
        } else if (msg.isLeaveGroup()) {
          //todo message text processing and call for change group restriction service

        } else if (msg.isMakeAdmin()) {
          String[] split = msg.getText().split(" ");
          GroupServices.makeAdmin(split[1],msg.getName(), split[2]);
          sendMessageToClient(ServerConstants.SERVER_NAME, "Admin added successfully");
        }
      } catch (DatabaseConnectionException e) {
        sendMessageToClient(ServerConstants.SERVER_NAME, e.getMessage());
      } catch (SQLException e) {
        ChatLogger.error("Could not connect to database");
      }
    } else {
      sendMessageToClient(ServerConstants.BOUNCER_ID, "Last message was rejected because it specified an incorrect user name.\"");
    }
  }

  /**
   * Method to retrieve the messages sent to a group.
   *
   * @param msg retrieve message command sent to server
   */
  private void retrieveGroupMessagesForGroup(Message msg) throws SQLException {
    List<String> messages = MessageServices.retrieveGroupMessages(getReceiverName(msg.getText()));
    for (String conv : messages) {
      String[] arr = conv.split(" ");

      Message sendMessage = Message.makeGroupMessage("@" + msg.getText() + " " + arr[0], conv.substring(arr[0].length() + arr[1].length() + arr[2].length() + 3));
      enqueueMessage(sendMessage);
    }
  }

  /**
   * Method to retrieve the messages sent to a user.
   *
   * @param msg retrieve message command sent to server
   */
  private void retrieveMessagesForUser(Message msg) throws SQLException {
    List<String> messages = MessageServices.retrieveUserMessages(msg.getName(), getReceiverName(msg.getText()));
    for (String conv : messages) {
      String[] arr = conv.split(" ");

      Message sendMessage = Message.makePrivateMessage(arr[0], conv.substring(arr[0].length() + arr[1].length() + arr[2].length() + 3));
      enqueueMessage(sendMessage);
    }
  }

  /**
   * Method to send a message to a client based on username.
   *
   * @param sender  string representing the sender name
   * @param message string representing the message text
   */
  private void sendMessageToClient(String sender, String message) {
    Message sendMsg;
    sendMsg = Message.makeAckMessage(sender, message);
    enqueueMessage(sendMsg);
  }

  /**
   * Method to get the receiver name of a message.
   *
   * @param text the message text
   * @return a string representing the reciever name
   */
  private String getReceiverName(String text) {
    return text.split(" ")[1];
  }

  /**
   * Sends the enqueued messages to the printer and makes sure they were sent out.
   */
  protected void handleOutgoingMessages() {
    // Check to make sure we have a client to send to.
    boolean keepAlive = true;
    if (!waitingList.isEmpty()) {
      keepAlive = false;
      // Send out all of the message that have been added to the
      // queue.
      do {
        Message msg = waitingList.remove();
        boolean sentGood = sendMessage(msg);
        keepAlive |= sentGood;
        // Update the time until we terminate the client for inactivity.
        timer.updateAfterActivity();

      } while (!waitingList.isEmpty());
    }
    terminate |= !keepAlive;
  }

  /**
   * Store the object used by this client runnable to control when it is scheduled for execution in
   * the thread pool.
   *
   * @param future Instance controlling when the runnable is executed from within the thread pool.
   */
  public void setFuture(ScheduledFuture<?> future) {
    runnableMe = future;
  }

  /**
   * Terminate a client that we wish to remove. This termination could happen at the client's
   * request or due to system need.
   */
  public void terminateClient() {
    // Once the communication is done, close this connection.
    connection.close();
    // Remove the client from our client listing.
    Prattle.removeClient(this);
    // And remove the client from our client pool.
    runnableMe.cancel(true);
  }
}