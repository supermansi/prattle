/** Copyright (c) 2019 Rohan Gori, Aditi Kacheria, Mansi Jain, Joshua Dick. All rights reserved.*/
package edu.northeastern.ccs.im;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

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
  private static boolean isMsgMapInitialised = false;
  private static ConcurrentMap<String, BiFunction<String, String, Message>> messageMap = new ConcurrentHashMap<>();
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
  public static Message makeQuitMessage(String myName, String msgText) {
    return new Message(MessageType.QUIT, myName, msgText);
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

    if (!isMsgMapInitialised) {
      initialiseMessageMap();
    }
    return messageMap.get(handle).apply(srcName, text);

  }

  /**
   * Method to initialize the message map.
   */

  private static void initialiseMessageMap() {
    isMsgMapInitialised = true;
    BiFunction<String, String, Message> helloFunction = ((src, txt) -> (makeSimpleLoginMessage(src, txt)));
    BiFunction<String, String, Message> quitFunction = ((src, txt) -> (makeQuitMessage(src, txt)));
    BiFunction<String, String, Message> broadcastFunction = ((src, txt) -> (makeBroadcastMessage(src, txt)));
    BiFunction<String, String, Message> acknowledgeFunction = ((src, txt) -> (makeAckMessage(src, txt)));
    BiFunction<String, String, Message> negativeAckFunction = ((src, txt) -> (makeNackMessage(src, txt)));
    BiFunction<String, String, Message> privateFunction = ((src, txt) -> (makePrivateMessage(src, txt)));
    BiFunction<String, String, Message> groupFunction = ((src, txt) -> (makeGroupMessage(src, txt)));
    BiFunction<String, String, Message> registrationFunction = ((src, txt) -> (makeRegisterationMessage(src, txt)));
    BiFunction<String, String, Message> updateFNFunction = ((src, txt) -> (makeUpdateFirstNameMessage(src, txt)));
    BiFunction<String, String, Message> updateLNFunction = ((src, txt) -> (makeUpdateLastNameMessage(src, txt)));
    BiFunction<String, String, Message> updatePWFunction = ((src, txt) -> (makeUpdatePasswordMessage(src, txt)));
    BiFunction<String, String, Message> updateEMFunction = ((src, txt) -> (makeUpdateEmailMessage(src, txt)));
    BiFunction<String, String, Message> createGroupFunction = ((src, txt) -> (createGroupMessage(src, txt)));
    BiFunction<String, String, Message> deleteGroupFunction = ((src, txt) -> (deleteGroupMessage(src, txt)));
    BiFunction<String, String, Message> removeUserFunction = ((src, txt) -> (makeRemoveUserMessage(src, txt)));
    BiFunction<String, String, Message> addUserFunction = ((src, txt) -> (makeAddUserToGroupMessage(src, txt)));
    BiFunction<String, String, Message> retrieveUserFunction = ((src, txt) -> (makeRetrieveUserMessage(src, txt)));
    BiFunction<String, String, Message> retrieveGroupFunction = ((src, txt) -> (makeRetrieveGroupMessage(src, txt)));
    BiFunction<String, String, Message> deactivateAccountFunction = ((src, txt) -> (makeDeactivateUserMessage(src, txt)));
    BiFunction<String, String, Message> userExistsFunction = ((src, txt) -> (makeUserExistsMessage(src, txt)));
    BiFunction<String, String, Message> attachmentFunction = ((src, txt) -> (makeAttachmentMessage(src, txt)));
    BiFunction<String, String, Message> lastSeenFunction = ((src, txt) -> (makeLastSeenMessage(src, txt)));
    BiFunction<String, String, Message> leaveGroupFunction = ((src, txt) -> (makeLeaveGroupMessage(src, txt)));
    BiFunction<String, String, Message> setGroupRestrictionsFunction = ((src, txt) -> (makeSetGroupRestrictionMessage(src, txt)));
    BiFunction<String, String, Message> makeAdminFunction = ((src, txt) -> (makeMakeAdminMessage(src, txt)));
    BiFunction<String, String, Message> recallFunction = ((src, txt) -> (makeRecallMessage(src, txt)));
    BiFunction<String, String, Message> readAttachmentFunction = ((src, txt) -> (makeReadAttachmentMessage(src, txt)));
    BiFunction<String, String, Message> getGroupUsersFunctions = ((src, txt) -> (makeGetUsersInGroupMessage(src, txt)));
    BiFunction<String, String, Message> getAllGroupsUserBelongsToFunction = ((src, txt) -> (makeGetAllGroupsUserBelongsMessage(src, txt)));
    BiFunction<String, String, Message> dndFunction = ((src, txt) -> (makeDNDMessage(src, txt)));
    BiFunction<String, String, Message> getMessagesBetweenFunction = ((src, txt) -> (makeGetMessagesBetweenMessage(src, txt)));
    BiFunction<String, String, Message> createThreadMessageFunction = ((src, txt) -> (makeCreateThreadMessage(src, txt)));
    BiFunction<String, String, Message> postOnThreadFunction = ((src, txt) -> (makePostOnThreadMessage(src, txt)));
    BiFunction<String, String, Message> followUserFunction = ((src, txt) -> (makeFollowUserMessage(src, txt)));
    BiFunction<String, String, Message> getAllThreadsFunction = ((src, txt) -> (makeGetAllThreadsMessage(src, txt)));
    BiFunction<String, String, Message> getThreadMessagesFunction = ((src, txt) -> (makeGetThreadMessagesMessage(src, txt)));
    BiFunction<String, String, Message> unFollowUserFunction = ((src, txt) -> (makeUnfollowUserMessage(src, txt)));
    BiFunction<String, String, Message> forwardFunction = ((src, txt) -> (makeForwardMessageMessage(src, txt)));
    BiFunction<String, String, Message> secretFunction = ((src, txt) -> (makeSecretMessageMessage(src, txt)));
    BiFunction<String, String, Message> setWireTapFunction = ((src, txt) -> (makeSetWiretapMessage(src, txt)));
    BiFunction<String, String, Message> getListOfWireTappedUsersFunction = ((src, txt) -> (makeGetListWiretappedUsers(src, txt)));
    BiFunction<String, String, Message> replyFunction = ((src, txt) -> (makeReplyMessage(src, txt)));
    BiFunction<String, String, Message> getDataForWireTappedUserFunction = ((src, txt) -> (makeGetDataOfWiretappedUser(src, txt)));
    BiFunction<String, String, Message> getUserProfileFunction = ((src, txt) -> (makeGetUserProfileMessage(src, txt)));
    BiFunction<String, String, Message> getFollowersFunction = ((src, txt) -> (makeGetFollowersMessage(src, txt)));
    BiFunction<String, String, Message> getFollowingFunction = ((src, txt) -> (makeGetFollowingMessage(src, txt)));
    BiFunction<String, String, Message> subscribeToThreadFunction = ((src, txt) -> (makeSubscribeToThreadMessage(src, txt)));
    BiFunction<String, String, Message> getReplyChainFunction = ((src, txt) -> (makeGetReplyChainMessage(src, txt)));

    messageMap.put("HLO", helloFunction);
    messageMap.put("BYE", quitFunction);
    messageMap.put("BCT", broadcastFunction);
    messageMap.put("ACK", acknowledgeFunction);
    messageMap.put("NAK", negativeAckFunction);
    messageMap.put("REG", registrationFunction);
    messageMap.put("PVT", privateFunction);
    messageMap.put("GRP", groupFunction);
    messageMap.put("UFN", updateFNFunction);
    messageMap.put("ULN", updateLNFunction);
    messageMap.put("UPW", updatePWFunction);
    messageMap.put("UEM", updateEMFunction);
    messageMap.put("CGR", createGroupFunction);
    messageMap.put("DGR", deleteGroupFunction);
    messageMap.put("RMU", removeUserFunction);
    messageMap.put("RTU", retrieveUserFunction);
    messageMap.put("RTG", retrieveGroupFunction);
    messageMap.put("AUG", addUserFunction);
    messageMap.put("DUS", deactivateAccountFunction);
    messageMap.put("UEX", userExistsFunction);
    messageMap.put("ATT", attachmentFunction);
    messageMap.put("LSN", lastSeenFunction);
    messageMap.put("SGR", setGroupRestrictionsFunction);
    messageMap.put("LGR", leaveGroupFunction);
    messageMap.put("MAD", makeAdminFunction);
    messageMap.put("RCL", recallFunction);
    messageMap.put("RAM", readAttachmentFunction);
    messageMap.put("GGU", getGroupUsersFunctions);
    messageMap.put("GUP", getUserProfileFunction);
    messageMap.put("DND", dndFunction);
    messageMap.put("GUG", getAllGroupsUserBelongsToFunction);
    messageMap.put("GMB", getMessagesBetweenFunction);
    messageMap.put("TRD", createThreadMessageFunction);
    messageMap.put("POT", postOnThreadFunction);
    messageMap.put("FUS", followUserFunction);
    messageMap.put("GAT", getAllThreadsFunction);
    messageMap.put("GTM", getThreadMessagesFunction);
    messageMap.put("UUS", unFollowUserFunction);
    messageMap.put("FWD", forwardFunction);
    messageMap.put("SMS", secretFunction);
    messageMap.put("WTU", getListOfWireTappedUsersFunction);
    messageMap.put("REP", replyFunction);
    messageMap.put("GWU", getDataForWireTappedUserFunction);
    messageMap.put("WTM", setWireTapFunction);
    messageMap.put("GFW", getFollowersFunction);
    messageMap.put("GFG", getFollowingFunction);
    messageMap.put("STT", subscribeToThreadFunction);
    messageMap.put("GRC", getReplyChainFunction);

  }

  /**
   * Method to create a get reply chain message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new get reply chain message
   */
  private static Message makeGetReplyChainMessage(String srcName, String text) {
    return new Message(MessageType.GET_REPLY_CHAIN, srcName, text);
  }

  /**
   * Method to create a subscribe to thread message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new subscribe to thread message
   */
  private static Message makeSubscribeToThreadMessage(String srcName, String text) {
    return new Message(MessageType.SUBSCRIBE_TO_THREAD, srcName, text);
  }

  /**
   * Method to create a dnd message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new dnd message
   */
  public static Message makeDNDMessage(String srcName, String text) {
    return new Message(MessageType.DO_NOT_DISTURB, srcName, text);
  }

  /**
   * Method to create a get groups user belongs to message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new get groups user belongs to message
   */
  public static Message makeGetAllGroupsUserBelongsMessage(String srcName, String text) {
    return new Message(MessageType.GET_ALL_GROUP_USER_BELONGS, srcName, text);
  }

  /**
   * Method to create a read attachment message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new read attachment message
   */
  public static Message makeReadAttachmentMessage(String srcName, String text) {
    return new Message(MessageType.READ_ATTACHMENT_MESSAGE, srcName, text);
  }

  /**
   * Method to create a recall message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new recall message
   */
  public static Message makeRecallMessage(String srcName, String text) {
    return new Message(MessageType.RECALL, srcName, text);
  }

  /**
   * Method to create an attachment message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new attachment message
   */
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

  /**
   * Method to create a deactivate user  message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new deactivate user message
   */
  public static Message makeDeactivateUserMessage(String srcName, String text) {
    return new Message(MessageType.DEACTIVATE_USER, srcName, text);
  }

  /**
   * Method to create a user exists message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new user exists message
   */
  public static Message makeUserExistsMessage(String srcName, String text) {
    return new Message(MessageType.USER_EXISTS, srcName, text);
  }

  /**
   * Method to create a last seen message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new last seen message
   */
  public static Message makeLastSeenMessage(String srcName, String text) {
    return new Message(MessageType.LAST_SEEN, srcName, text);
  }

  /**
   * Method to create a leave group message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new leave group message
   */
  public static Message makeLeaveGroupMessage(String srcName, String text) {
    return new Message(MessageType.LEAVE_GROUP, srcName, text);
  }

  /**
   * Method to create a set group restriction message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new set group restriction message
   */
  public static Message makeSetGroupRestrictionMessage(String srcName, String text) {
    return new Message(MessageType.SET_GROUP_RESTRICTION, srcName, text);
  }

  /**
   * Method to create make admin message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new make admin message
   */
  public static Message makeMakeAdminMessage(String srcName, String text) {
    return new Message(MessageType.MAKE_ADMIN, srcName, text);
  }

  /**
   * Method to create a get users in group message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new get users in group message
   */
  public static Message makeGetUsersInGroupMessage(String srcName, String text) {
    return new Message(MessageType.GET_GROUP_USERS, srcName, text);
  }

  /**
   * Method to create a secret message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new secret message
   */
  public static Message makeSecretMessageMessage(String srcName, String text) {
    return new Message(MessageType.SECRET_MESSAGE, srcName, text);
  }

  /**
   * Method to create a forward message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new forward message
   */
  public static Message makeForwardMessageMessage(String srcName, String text) {
    return new Message(MessageType.FORWARD_MESSAGE, srcName, text);
  }

  /**
   * Method to create an unfollow message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new unfollow message
   */
  public static Message makeUnfollowUserMessage(String srcName, String text) {
    return new Message(MessageType.UNFOLLOW_USER, srcName, text);
  }

  /**
   * Method to create a get thread messages message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new get thread messages message
   */
  public static Message makeGetThreadMessagesMessage(String srcName, String text) {
    return new Message(MessageType.GET_THREAD_MESSAGES, srcName, text);
  }

  /**
   * Method to create a get all threads message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new get all threads message
   */
  public static Message makeGetAllThreadsMessage(String srcName, String text) {
    return new Message(MessageType.GET_ALL_THREADS, srcName, text);
  }

  /**
   * Method to create a follow message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new follow message
   */
  public static Message makeFollowUserMessage(String srcName, String text) {
    return new Message(MessageType.FOLLOW_USER, srcName, text);
  }

  /**
   * Method to create a post on thread message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new post on thread message
   */
  public static Message makePostOnThreadMessage(String srcName, String text) {
    return new Message(MessageType.POST_ON_THREAD, srcName, text);
  }

  /**
   * Method to create a create thread message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new create thread message
   */
  public static Message makeCreateThreadMessage(String srcName, String text) {
    return new Message(MessageType.CREATE_THREAD, srcName, text);
  }

  /**
   * Method to create a get messages between message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new get messages between message
   */
  public static Message makeGetMessagesBetweenMessage(String srcName, String text) {
    return new Message(MessageType.GET_MESSAGES_BETWEEN, srcName, text);
  }

  /**
   * Method to create a set wiretap message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new set wiretap message
   */
  public static Message makeSetWiretapMessage(String srcName, String text) {
    return new Message(MessageType.SET_WIRETAP_MESSAGE, srcName, text);
  }

  /**
   * Method to create a get data of wiretap user message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new get data of wiretap user message
   */
  public static Message makeGetDataOfWiretappedUser(String srcName, String text) {
    return new Message(MessageType.GET_DATA_WIRETAPPED_USER, srcName, text);
  }

  /**
   * Method to create a reply message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new reply message
   */
  public static Message makeReplyMessage(String srcName, String text) {
    return new Message(MessageType.REPLY, srcName, text);
  }

  /**
   * Method to create a get list of wiretap user message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new get list of wiretap user message
   */
  public static Message makeGetListWiretappedUsers(String srcName, String text) {
    return new Message(MessageType.GET_LIST_OF_WIRETAPPED_USERS, srcName, text);
  }

  /**
   * Method to create a get user profile message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new get user profile message
   */
  public static Message makeGetUserProfileMessage(String srcName, String text) {
    return new Message(MessageType.GET_USER_PROFILE, srcName, text);
  }

  /**
   * Method to create a get following message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new get following message
   */
  public static Message makeGetFollowingMessage(String srcName, String text) {
    return new Message(MessageType.GET_FOLLOWING, srcName, text);
  }

  /**
   * Method to create a get followers message.
   *
   * @param srcName the sender of the message
   * @param text    the message text
   * @return a new get followers message
   */
  public static Message makeGetFollowersMessage(String srcName, String text) {
    return new Message(MessageType.GET_FOLLOWERS, srcName, text);
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

  /**
   * Determine if this message is a deactivate user message.
   *
   * @return True if the message is a deactivate user message; false otherwise
   */
  public boolean isDeactivateUser() {
    return (msgType == MessageType.DEACTIVATE_USER);
  }

  /**
   * Determine if this message is a user exists message.
   *
   * @return True if the message is a user exists message; false otherwise
   */
  public boolean isUserExists() {
    return (msgType == MessageType.USER_EXISTS);
  }

  /**
   * Determine if this message is an attachment message.
   *
   * @return True if the message is an attachment message; false otherwise
   */
  public boolean isAttachmentMessage() {
    return (msgType == MessageType.ATTACHMENT);
  }

  /**
   * Determine if this message is a last seen message.
   *
   * @return True if the message is a last seen message; false otherwise
   */
  public boolean isLastSeen() {
    return (msgType == MessageType.LAST_SEEN);
  }

  /**
   * Determine if this message is a change group restriction message.
   *
   * @return True if the message is a change group restriction message; false otherwise
   */
  public boolean isChangeGroupRestriction() {
    return (msgType == MessageType.SET_GROUP_RESTRICTION);
  }

  /**
   * Determine if this message is a leave group message.
   *
   * @return True if the message is a leave group message; false otherwise
   */
  public boolean isLeaveGroup() {
    return (msgType == MessageType.LEAVE_GROUP);
  }

  /**
   * Determine if this message is a make admin message.
   *
   * @return True if the message is a make admin message; false otherwise
   */
  public boolean isMakeAdmin() {
    return (msgType == MessageType.MAKE_ADMIN);
  }

  /**
   * Determine if this message is a recall message.
   *
   * @return True if the message is a recall message; false otherwise
   */
  public boolean isRecall() {
    return (msgType == MessageType.RECALL);
  }

  /**
   * Determine if this message is a read attachment message.
   *
   * @return True if the message is a read attachment message; false otherwise
   */
  public boolean isReadAttachmentMessage() {
    return (msgType == MessageType.READ_ATTACHMENT_MESSAGE);
  }

  /**
   * Determine if this message is a get users in group message.
   *
   * @return True if the message is a get users in group message; false otherwise
   */
  public boolean isGetUsersInGroup() {
    return (msgType == MessageType.GET_GROUP_USERS);
  }

  /**
   * Determine if this message is a get all groups user belongs to message.
   *
   * @return True if the message is a get all groups user belongs to message; false otherwise
   */
  public boolean isGetAllGroupsUserBelongsTo() {
    return (msgType == MessageType.GET_ALL_GROUP_USER_BELONGS);
  }

  /**
   * Determine if this message is a dnd message.
   *
   * @return True if the message is a dnd message; false otherwise
   */
  public boolean isDND() {
    return (msgType == MessageType.DO_NOT_DISTURB);
  }

  /**
   * Determine if this message is a secret message.
   *
   * @return True if the message is a secret message; false otherwise
   */
  public boolean isSecretMessage() {
    return (msgType == MessageType.SECRET_MESSAGE);
  }

  /**
   * Determine if this message is a forward message.
   *
   * @return True if the message is a forward message; false otherwise
   */
  public boolean isForwardMessage() {
    return (msgType == MessageType.FORWARD_MESSAGE);
  }

  /**
   * Determine if this message is a unfollow message.
   *
   * @return True if the message is a unfollow message; false otherwise
   */
  public boolean isUnfollowUser() {
    return (msgType == MessageType.UNFOLLOW_USER);
  }

  /**
   * Determine if this message is a follow message.
   *
   * @return True if the message is a follow message; false otherwise
   */
  public boolean isFollowUser() {
    return (msgType == MessageType.FOLLOW_USER);
  }

  /**
   * Determine if this message is a get thread messages message.
   *
   * @return True if the message is a get thread messages message; false otherwise
   */
  public boolean isGetThreadMessages() {
    return (msgType == MessageType.GET_THREAD_MESSAGES);
  }

  /**
   * Determine if this message is a get all threads message.
   *
   * @return True if the message is a get all threads message; false otherwise
   */
  public boolean isGetAllThreads() {
    return (msgType == MessageType.GET_ALL_THREADS);
  }

  /**
   * Determine if this message is a post on thread message.
   *
   * @return True if the message is a post on thread message; false otherwise
   */
  public boolean isPostOnThread() {
    return (msgType == MessageType.POST_ON_THREAD);
  }

  /**
   * Determine if this message is a create thread message.
   *
   * @return True if the message is a create thread message; false otherwise
   */
  public boolean isCreateThread() {
    return (msgType == MessageType.CREATE_THREAD);
  }

  /**
   * Determine if this message is a get messages between message.
   *
   * @return True if the message is a get messages between message; false otherwise
   */
  public boolean isGetMessagesBetween() {
    return (msgType == MessageType.GET_MESSAGES_BETWEEN);
  }

  /**
   * Determine if this message is a get list of wiretap user message.
   *
   * @return True if the message is a get list of wiretap user message; false otherwise
   */
  public boolean isGetListOfWiretapUsers() {
    return (msgType == MessageType.GET_LIST_OF_WIRETAPPED_USERS);
  }

  /**
   * Determine if this message is a reply message.
   *
   * @return True if the message is a reply message; false otherwise
   */
  public boolean isReply() {
    return (msgType == MessageType.REPLY);
  }

  /**
   * Determine if this message is a get data of wiretap user message.
   *
   * @return True if the message is a get data of wiretap user message; false otherwise
   */
  public boolean isGetDataWiretappedUser() {
    return (msgType == MessageType.GET_DATA_WIRETAPPED_USER);
  }

  /**
   * Determine if this message is a set wiretap user message.
   *
   * @return True if the message is a set wiretap user message; false otherwise
   */
  public boolean isSetWiretapMessage() {
    return (msgType == MessageType.SET_WIRETAP_MESSAGE);
  }

  /**
   * Determine if this message is a wiretap status message.
   *
   * @return True if the message is a wiretap status message; false otherwise
   */
  public boolean isWireTapStatusMessage() {
    return (msgType == MessageType.SET_WIRETAP_MESSAGE);
  }

  /**
   * Determine if this message is a get message type message.
   *
   * @return True if the message is a get message type message; false otherwise
   */
  public MessageType getMessageType() {
    return this.msgType;
  }

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
