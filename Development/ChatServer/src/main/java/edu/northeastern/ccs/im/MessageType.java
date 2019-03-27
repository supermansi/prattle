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

  ACKNOWLEDGEMENT("ACK"),

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

  DEACTIVATE_USER("DUS"),

  USER_EXISTS("UEX");

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
