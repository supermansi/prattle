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

  UPDATE_FN("UFN"),

  UPDATE_LN("ULN"),

  UPDATE_PW("UPW"),

  UPDATE_EM("UEM"),

  CREATE_GROUP("CGR"),

  DELETE_GROUP("DGR"),

  REMOVE_USER("RMU"),

  RETRIEVE_USER("RTU"),

  RETRIEVE_GROUP("RTG"),

  ADD_USER_TO_GRP("AUG");

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
