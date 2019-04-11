package edu.northeastern.ccs.im;

/**
 * Enumeration for the different types of messages.
 *
 * @author Maria Jump
 */
public enum MessageType {
  /**
   * Message sent by the user attempting to login using a specified username.
   */
  HELLO("HLO"),
  /**
   * Message sent by the user to start the logging out process and sent by the server once the
   * logout process completes.
   */
  QUIT("BYE"),
  /**
   * Message whose contents is broadcast to all connected users.
   */
  BROADCAST("BCT"),

  /**
   * Message to acknowledge login from server.
   */
  ACKNOWLEDGEMENT("ACK"),

  /**
   * Message to show failed login from server.
   */
  NO_ACKNOWLEDGEMENT("NAK"),

  /**
   * Message sent for registration
   */
  REGISTRATION("REG"),

  /**
   * Private Messages sent .
   */
  PRIVATE("PVT"),

  /**
   * Messages sent to a group.
   */
  GROUP("GRP"),

  /**
   * Message to update user first name.
   */
  UPDATE_FN("UFN"),

  /**
   * Message to update user last name.
   */
  UPDATE_LN("ULN"),

  /**
   * Message to update user password.
   */
  UPDATE_PW("UPW"),

  /**
   * Message to update user email.
   */
  UPDATE_EM("UEM"),

  /**
   * Message to create a group.
   */
  CREATE_GROUP("CGR"),

  /**
   * Message to delete a group.
   */
  DELETE_GROUP("DGR"),

  /**
   * Message to remove a user from a group.
   */
  REMOVE_USER("RMU"),

  /**
   * Message to retrieve messages from a user.
   */
  RETRIEVE_USER("RTU"),

  /**
   * Message to retrieve messages from a group.
   */
  RETRIEVE_GROUP("RTG"),

  /**
   * Message to a user to a group.
   */
  ADD_USER_TO_GRP("AUG"),

  /**
   * Message to deactivate an account.
   */
  DEACTIVATE_USER("DUS"),

  /**
   * Message to check if a user exists.
   */
  USER_EXISTS("UEX"),

  /**
   * Message to send an attachment to another user.
   */
  ATTACHMENT("ATT"),

  /**
   * Message to check the time a user last saw mesages.
   */
  LAST_SEEN("LSN"),

  /**
   * Message to set a groups restriction to either H(high) or L(low).
   */
  SET_GROUP_RESTRICTION("SGR"),

  /**
   * Message to leave a group.
   */
  LEAVE_GROUP("LGR"),

  /**
   * Message to make a user an admin of a group.
   */
  MAKE_ADMIN("MAD"),

  /**
   * Message to recall the last message sent.
   */
  RECALL("RCL"),

  /**
   * Message to read an incoming attachment.
   */
  READ_ATTACHMENT_MESSAGE("RAM"),

  /**
   * Message to get all the users in a group.
   */
  GET_GROUP_USERS("GGU"),

  GET_USER_PROFILE("GUP"),

  DO_NOT_DISTURB("DND"),

  GET_ALL_GROUP_USER_BELONGS("GUG"),

  GET_MESSAGES_BETWEEN("GMB"),

  CREATE_THREAD("TRD"),

  POST_ON_THREAD("POT"),

  FOLLOW_USER("FUS"),

  GET_ALL_THREADS("GAT"),

  GET_THREAD_MESSAGES("GTM"),

  UNFOLLOW_USER("UUS"),

  FOWARD_MESSAGE("FWD"),

  SECRET_MESSAGE("SMS"),

  GET_LIST_OF_WIRETAPPED_USERS("WTU"),

  REPLY("REP"),

  GET_DATA_WIRETAPPED_USER("GWU"),

  SET_WIRETAP_MESSAGE("WTM"),

  GET_FOLLOWERS("GFW"),

  GET_FOLLOWING("GFG"),

  SUBSCRIBE_TO_THREAD("STT"),

  GET_REPLY_CHAIN("GRC");


  /**
   * Store the short name of this message type.
   */
  private String abbreviation;

  /**
   * Define the message type and specify its short name.
   *
   * @param abbrev Short name of this message type, as a String.
   */
  private MessageType(String abbrev) {
    abbreviation = abbrev;
  }

  /**
   * Return a representation of this Message as a String.
   *
   * @return Three letter abbreviation for this type of message.
   */
  @Override
  public String toString() {
    return abbreviation;
  }
}
