package edu.northeastern.ccs.im;

/**
 * Each instance of this class represents a single transmission by our IM clients.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0 International
 * License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/. It
 * is based on work originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 *
 * @version 1.3
 */
public class Message {

  /**
   * The string sent when a field is null.
   */
  private static final String NULL_OUTPUT = "--";

  /**
   * The handle of the message.
   */
  private MessageType msgType;

  /**
   * The first argument used in the message. This will be the sender's identifier.
   */
  private String msgSender;

  /**
   * The second argument used in the message.
   */
  private String msgText;

  /**
   * Create a new message that contains actual IM text. The type of distribution is defined by the
   * handle and we must also set the name of the message sender, message recipient, and the text to
   * send.
   *
   * @param handle  Handle for the type of message being created.
   * @param srcName Name of the individual sending this message
   * @param text    Text of the instant message
   */
  private Message(MessageType handle, String srcName, String text) {
    msgType = handle;
    // Save the properly formatted identifier for the user sending the
    // message.
    msgSender = srcName;
    // Save the text of the message.
    msgText = text;
  }

  /**
   * Create a new message to continue the logout process.
   *
   * @param myName The name of the client that sent the quit message.
   * @return Instance of Message that specifies the process is logging out.
   */
  public static Message makeQuitMessage(String myName) {
    return new Message(MessageType.QUIT, myName, null);
  }


  /**
   * Create a new message broadcasting an announcement to the world.
   *
   * @param myName Name of the sender of this very important missive.
   * @param text   Text of the message that will be sent to all users
   * @return Instance of Message that transmits text to all logged in users.
   */
  public static Message makeBroadcastMessage(String myName, String text) {
    return new Message(MessageType.BROADCAST, myName, text);
  }

  /**
   * Create a new message stating the name with which the user would like to login.
   *
   * @param text Name the user wishes to use as their screen name.
   * @return Instance of Message that can be sent to the server to try and login.
   */
  protected static Message makeHelloMessage(String text) {
    return new Message(MessageType.HELLO, null, text);
  }

  /**
   * Given a handle, name and text, return the appropriate message instance or an instance from a
   * subclass of message.
   *
   * @param handle  Handle of the message to be generated.
   * @param srcName Name of the originator of the message (may be null)
   * @param text    Text sent in this message (may be null)
   * @return Instance of Message (or its subclasses) representing the handle, name, & text.
   */
  protected static Message makeMessage(String handle, String srcName, String text) {
    Message result = null;
    if (handle.compareTo(MessageType.QUIT.toString()) == 0) {
      result = makeQuitMessage(srcName);
    } else if (handle.compareTo(MessageType.HELLO.toString()) == 0) {
      result = makeSimpleLoginMessage(srcName, text);
    } else if (handle.compareTo(MessageType.BROADCAST.toString()) == 0) {
      result = makeBroadcastMessage(srcName, text);
    } else if (handle.compareTo(MessageType.ACKNOWLEDGEMENT.toString()) == 0) {
      result = makeAckMessage(srcName, text);
    } else if (handle.compareTo(MessageType.NO_ACKNOWLEDGEMENT.toString()) == 0) {
      result = makeNackMessage(srcName, text);
    } else if (handle.compareTo(MessageType.PRIVATE.toString()) == 0) {
      result = makePrivateMessage(srcName, text);
    } else if (handle.compareTo(MessageType.GROUP.toString()) == 0) {
      result = makeGroupMessage(srcName, text);
    } else if (handle.compareTo(MessageType.REGISTRATION.toString()) == 0) {
      result = makeRegisterationMessage(srcName, text);
    } else if (handle.compareTo(MessageType.UPDATE_FN.toString()) == 0) {
      result = makeUpdateFirstNameMessage(srcName, text);
    } else if (handle.compareTo(MessageType.UPDATE_LN.toString()) == 0) {
      result = makeUpdateLastNameMessage(srcName, text);
    } else if (handle.compareTo(MessageType.UPDATE_PW.toString()) == 0) {
      result = makeUpdatePasswordMessage(srcName, text);
    } else if (handle.compareTo(MessageType.UPDATE_EM.toString()) == 0) {
      result = makeUpdateEmailMessage(srcName, text);
    } else if (handle.compareTo(MessageType.CREATE_GROUP.toString()) == 0) {
      result = createGroupMessage(srcName, text);
    } else if (handle.compareTo(MessageType.DELETE_GROUP.toString()) == 0) {
      result = deleteGroupMessage(srcName, text);
    } else if (handle.compareTo(MessageType.REMOVE_USER.toString()) == 0) {
      result = makeRemoveUserMessage(srcName, text);
    } else if (handle.compareTo(MessageType.RETRIEVE_USER.toString()) == 0) {
      result = makeRetrieveUserMessage(srcName, text);
    } else if (handle.compareTo(MessageType.RETRIEVE_GROUP.toString()) == 0) {
      result = makeRetrieveGroupMessage(srcName, text);
    } else if (handle.compareTo(MessageType.ADD_USER_TO_GRP.toString()) == 0) {
      result = makeAddUserToGroupMessage(srcName, text);
    } else if (handle.compareTo(MessageType.DEACTIVATE_USER.toString()) == 0) {
      result = makeDeactivateUserMessage(srcName, text);
    } else if (handle.compareTo(MessageType.USER_EXISTS.toString()) == 0) {
      result = makeUserExistsMessage(srcName, text);
    } else if (handle.compareTo(MessageType.ATTACHMENT.toString()) == 0) {
      result = makeAttachmentMessage(srcName, text);
    } else if (handle.compareTo(MessageType.LAST_SEEN.toString()) == 0) {
      result = makeLastSeenMessage(srcName, text);
    } else if (handle.compareTo(MessageType.LEAVE_GROUP.toString()) == 0) {
      result = makeLeaveGroupMessage(srcName, text);
    } else if (handle.compareTo(MessageType.SET_GROUP_RESTRICTION.toString()) == 0) {
      result = makeSetGroupRestrictionMessage(srcName, text);
    } else if (handle.compareTo(MessageType.MAKE_ADMIN.toString()) == 0) {
      result = makeMakeAdminMessage(srcName, text);
    } else if (handle.compareTo(MessageType.RECALL.toString()) == 0) {
      result = makeRecallMessage(srcName, text);
    } else if (handle.compareTo(MessageType.READ_ATTACHMENT_MESSAGE.toString()) == 0) {
      result = makeReadAttachmentMessage(srcName, text);
    } else if (handle.compareTo(MessageType.GET_GROUP_USERS.toString()) == 0) {
      result = makeGetUsersInGroupMessage(srcName, text);
    }
    return result;
  }

  public static Message makeReadAttachmentMessage(String srcName, String text) {
    return new Message(MessageType.READ_ATTACHMENT_MESSAGE, srcName, text);
  }

  public static Message makeRecallMessage(String srcName, String text) {
    return new Message(MessageType.RECALL, srcName, text);
  }

  public static Message makeAttachmentMessage(String srcName, String text) {
    return new Message((MessageType.ATTACHMENT), srcName, text);
  }

  /**
   * Method to create a registration message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return a registration message
   */
  public static Message makeRegisterationMessage(String srcName, String text) {
    return new Message((MessageType.REGISTRATION), srcName, text);
  }

  /**
   * Method to create a group message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return a group message
   */
  public static Message makeGroupMessage(String srcName, String text) {
    return new Message((MessageType.GROUP), srcName, text);
  }

  /**
   * Method to create a private message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return a private message
   */
  public static Message makePrivateMessage(String srcName, String text) {
    return new Message((MessageType.PRIVATE), srcName, text);
  }

  /**
   * Method to create a no acknowledge message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return a no acknowledge message
   */
  public static Message makeNackMessage(String srcName, String text) {
    return new Message(MessageType.NO_ACKNOWLEDGEMENT, srcName, text);
  }

  /**
   * Method to create an acknowledge message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return an acknowledge message
   */
  public static Message makeAckMessage(String srcName, String text) {
    return new Message(MessageType.ACKNOWLEDGEMENT, srcName, text);
  }

  /**
   * Method to create an update first name message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return an update first name message
   */
  public static Message makeUpdateFirstNameMessage(String srcName, String text) {
    return new Message(MessageType.UPDATE_FN, srcName, text);
  }

  /**
   * Method to create an update last name message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return an update last name message
   */
  public static Message makeUpdateLastNameMessage(String srcName, String text) {
    return new Message(MessageType.UPDATE_LN, srcName, text);
  }

  /**
   * Method to create an update password message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return an update password message
   */
  public static Message makeUpdatePasswordMessage(String srcName, String text) {
    return new Message(MessageType.UPDATE_PW, srcName, text);
  }

  /**
   * Method to create an update email message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return an update email message
   */
  public static Message makeUpdateEmailMessage(String srcName, String text) {
    return new Message(MessageType.UPDATE_EM, srcName, text);
  }

  /**
   * Method to create a create group message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return a create group message
   */
  public static Message createGroupMessage(String srcName, String text) {
    return new Message(MessageType.CREATE_GROUP, srcName, text);
  }

  /**
   * Method to create a delete group message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return a delete group message
   */
  public static Message deleteGroupMessage(String srcName, String text) {
    return new Message(MessageType.DELETE_GROUP, srcName, text);
  }

  /**
   * Method to create a remove user message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return a remove user message
   */
  public static Message makeRemoveUserMessage(String srcName, String text) {
    return new Message(MessageType.REMOVE_USER, srcName, text);
  }

  /**
   * Method to create a retrieve user message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return a retrieve user message
   */
  public static Message makeRetrieveUserMessage(String srcName, String text) {
    return new Message(MessageType.RETRIEVE_USER, srcName, text);
  }

  /**
   * Method to create a retrieve group message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return a retrieve group message
   */
  public static Message makeRetrieveGroupMessage(String srcName, String text) {
    return new Message(MessageType.RETRIEVE_GROUP, srcName, text);
  }

  /**
   * Method to create an add user to group message.
   *
   * @param srcName sender's user name
   * @param text    text of the message
   * @return an add user to group message
   */
  public static Message makeAddUserToGroupMessage(String srcName, String text) {
    return new Message(MessageType.ADD_USER_TO_GRP, srcName, text);
  }

  /**
   * Create a new message for the early stages when the user logs in without all the special stuff.
   *
   * @param myName Name of the user who has just logged in.
   * @return Instance of Message specifying a new friend has just logged in.
   */
  public static Message makeSimpleLoginMessage(String myName, String text) {
    return new Message(MessageType.HELLO, myName, text);
  }

  public static Message makeDeactivateUserMessage(String srcName, String text) {
    return new Message(MessageType.DEACTIVATE_USER, srcName, text);
  }

  public static Message makeUserExistsMessage(String srcName, String text) {
    return new Message(MessageType.USER_EXISTS, srcName, text);
  }

  public static Message makeLastSeenMessage(String srcName, String text) {
    return new Message(MessageType.LAST_SEEN, srcName, text);
  }

  public static Message makeLeaveGroupMessage(String srcName, String text) {
    return new Message(MessageType.LEAVE_GROUP, srcName, text);
  }

  public static Message makeSetGroupRestrictionMessage(String srcName, String text) {
    return new Message(MessageType.SET_GROUP_RESTRICTION, srcName, text);
  }

  public static Message makeMakeAdminMessage(String srcName, String text) {
    return new Message(MessageType.MAKE_ADMIN, srcName, text);
  }

  public static Message makeGetUsersInGroupMessage(String srcName, String text) {
    return new Message(MessageType.GET_GROUP_USERS,srcName,text);
  }


  /**
   * Return the name of the sender of this message.
   *
   * @return String specifying the name of the message originator.
   */
  public String getName() {
    return msgSender;
  }

  /**
   * Return the text of this message.
   *
   * @return String equal to the text sent by this message.
   */
  public String getText() {
    return msgText;
  }

  /**
   * Determine if this message is broadcasting text to everyone.
   *
   * @return True if the message is a broadcast message; false otherwise.
   */
  public boolean isBroadcastMessage() {
    return (msgType == MessageType.BROADCAST);
  }

  /**
   * Determine if this message is sent by a new client to log-in to the server.
   *
   * @return True if the message is an initialization message; false otherwise
   */
  public boolean isInitialization() {
    return (msgType == MessageType.HELLO);
  }

  /**
   * Determine if this message is a message signing off from the IM server.
   *
   * @return True if the message is sent when signing off; false otherwise
   */
  public boolean terminate() {
    return (msgType == MessageType.QUIT);
  }

  /**
   * Determine if this message is a private message.
   *
   * @return True if the message is a private message; false otherwise
   */
  public boolean isPrivateMessage() {
    return (msgType == MessageType.PRIVATE);
  }

  /**
   * Determine if this message is an acknowledge message.
   *
   * @return True if the message is an acknowledge message; false otherwise
   */
  public boolean isAcknowledge() {
    return (msgType == MessageType.ACKNOWLEDGEMENT);
  }

  /**
   * Determine if this message is a non acknowledge message.
   *
   * @return True if the message is a non acknowledge message; false otherwise
   */
  public boolean isNonAcknowledge() {
    return (msgType == MessageType.NO_ACKNOWLEDGEMENT);
  }

  /**
   * Determine if this message is a group message.
   *
   * @return True if the message is a group; false otherwise
   */
  public boolean isGroupMessage() {
    return (msgType == MessageType.GROUP);
  }

  /**
   * Determine if this message is a registration message.
   *
   * @return True if the message is a registration message; false otherwise
   */
  public boolean isRegistration() {
    return (msgType == MessageType.REGISTRATION);
  }

  /**
   * Determine if this message is an update first name message.
   *
   * @return True if the message is an update first name message; false otherwise
   */
  public boolean isUpdateFirstName() {
    return (msgType == MessageType.UPDATE_FN);
  }

  /**
   * Determine if this message is an update last name message.
   *
   * @return True if the message is an update last name message; false otherwise
   */
  public boolean isUpdateLastName() {
    return (msgType == MessageType.UPDATE_LN);
  }

  /**
   * Determine if this message is an update password message.
   *
   * @return True if the message is an update password message; false otherwise
   */
  public boolean isUpdatePassword() {
    return (msgType == MessageType.UPDATE_PW);
  }

  /**
   * Determine if this message is an update email message.
   *
   * @return True if the message is an update email message; false otherwise
   */
  public boolean isUpdateEmail() {
    return (msgType == MessageType.UPDATE_EM);
  }

  /**
   * Determine if this message is a create group message.
   *
   * @return True if the message is a create group message; false otherwise
   */
  public boolean isCreateGroup() {
    return (msgType == MessageType.CREATE_GROUP);
  }

  /**
   * Determine if this message is a delete group message.
   *
   * @return True if the message is a delete group message; false otherwise
   */
  public boolean isDeleteGroup() {
    return (msgType == MessageType.DELETE_GROUP);
  }

  /**
   * Determine if this message is a remove user message.
   *
   * @return True if the message is a remove user message; false otherwise
   */
  public boolean isRemoveUser() {
    return (msgType == MessageType.REMOVE_USER);
  }

  /**
   * Determine if this message is a retrieve user message.
   *
   * @return True if the message is a retrieve user message; false otherwise
   */
  public boolean isRetrieveUser() {
    return (msgType == MessageType.RETRIEVE_USER);
  }

  /**
   * Determine if this message is a retrieve group message.
   *
   * @return True if the message is a retrieve group message; false otherwise
   */
  public boolean isRetrieveGroup() {
    return (msgType == MessageType.RETRIEVE_GROUP);
  }

  /**
   * Determine if this message is an add user to group message.
   *
   * @return True if the message is an add user to group message; false otherwise
   */
  public boolean isAddUserToGroup() {
    return (msgType == MessageType.ADD_USER_TO_GRP);
  }

  public boolean isDeactivateUser() {
    return (msgType == MessageType.DEACTIVATE_USER);
  }

  public boolean isUserExists() {
    return (msgType == MessageType.USER_EXISTS);
  }

  public boolean isAttachmentMessage() {
    return (msgType == MessageType.ATTACHMENT);
  }

  public boolean isLastSeen() {
    return (msgType == MessageType.LAST_SEEN);
  }

  public boolean isChangeGroupRestriction() {
    return (msgType == MessageType.SET_GROUP_RESTRICTION);
  }

  public boolean isLeaveGroup() {
    return (msgType == MessageType.LEAVE_GROUP);
  }

  public boolean isMakeAdmin() {
    return (msgType == MessageType.MAKE_ADMIN);
  }

  public boolean isRecall() {
    return (msgType == MessageType.RECALL);
  }

  public boolean isReadAttachmentMessage() {
    return (msgType == MessageType.READ_ATTACHMENT_MESSAGE);
  }

  public boolean isGetUsersInGroup() { return (msgType == MessageType.GET_GROUP_USERS); }

  /**
   * Representation of this message as a String. This begins with the message handle and then
   * contains the length (as an integer) and the value of the next two arguments.
   *
   * @return Representation of this message as a String.
   */
  @Override
  public String toString() {
    String result = msgType.toString();
    if (msgSender != null) {
      result += " " + msgSender.length() + " " + msgSender;
    } else {
      result += " " + NULL_OUTPUT.length() + " " + NULL_OUTPUT;
    }
    if (msgText != null) {
      result += " " + msgText.length() + " " + msgText;
    } else {
      result += " " + NULL_OUTPUT.length() + " " + NULL_OUTPUT;
    }
    return result;
  }

}
