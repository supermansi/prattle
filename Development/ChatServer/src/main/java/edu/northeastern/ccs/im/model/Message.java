package edu.northeastern.ccs.im.model;

/**
 * This class represents a model object for a message.
 */
public class Message {
	
	private int msgID;
	private MsgType msgType;
	private int senderID;
	private String message;
	private String timestamp;

	public enum MsgType {
		PVT, GRP, BCT;
	}

	/**
	 * Constructor for a message model object.
	 *
	 * @param msgID int representing the message ID
	 * @param msgType MsgType for the message
	 * @param senderID int representing the sender ID
	 * @param message string representing the text of the message
	 * @param timestamp string representing the time the message was sent
	 */
	public Message(int msgID, MsgType msgType, int senderID, String message, String timestamp) {
		super();
		this.msgID = msgID;
		this.msgType = msgType;
		this.senderID = senderID;
		this.message = message;
		this.timestamp = timestamp;
	}

	/**
	 * Constructor for a message model object.
	 *
	 * @param msgType MsgType for the message
	 * @param senderID int representing the sender ID
	 * @param message string representing the text of the message
	 * @param timestamp string representing the time the message was sent
	 */
	public Message(MsgType msgType, int senderID, String message, String timestamp) {
		this.msgType = msgType;
		this.senderID = senderID;
		this.message = message;
		this.timestamp = timestamp;
	}

	/**
	 * Constructor for a message model object.
	 */
	public Message() {
		// Auto-generated constructor stub
	}

	/**
	 * Method to get the ID# of the message.
	 *
	 * @return int representing the ID# of the message
	 */
	public int getMsgID() {
		return msgID;
	}

	/**
	 * Method to set the #ID of the message.
	 *
	 * @param msgID int representing the #ID of the message
	 */
	public void setMsgID(int msgID) {
		this.msgID = msgID;
	}

	/**
	 * Method to get the message type.
	 *
	 * @return MsgType representing the type of message
	 */
	public MsgType getMsgType() {
		return msgType;
	}

	/**
	 * Method to set the message type.
	 *
	 * @param msgType MsgType representing the type of message
	 */
	public void setMsgType(MsgType msgType) {
		this.msgType = msgType;
	}

	/**
	 * Method to get the #ID of the sender.
	 *
	 * @return int representing the #ID of the sender
	 */
	public int getSenderID() {
		return senderID;
	}

	/**
	 * Method to set the #ID of the sender.
	 *
	 * @param senderID int representing the #ID of the sender
	 */
	public void setSenderID(int senderID) {
		this.senderID = senderID;
	}

	/**
	 * Method to get the text of the message.
	 *
	 * @return string representing the text of the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Method to set the text of the message.
	 *
	 * @param message string representing the text of the message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Method to get the timestamp of the message.
	 *
	 * @return string representing the message timestamp.
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Method to set the timestamp of the message.
	 *
	 * @param timestamp string representing the message timestamp.
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
