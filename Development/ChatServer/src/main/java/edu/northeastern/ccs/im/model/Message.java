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
	public Message(MsgType msgType, int senderID, String message, String timestamp) {
		this.msgType = msgType;
		this.senderID = senderID;
		this.message = message;
		this.timestamp = timestamp;
	}
	public Message() {
		// Auto-generated constructor stub
	}
	public int getMsgID() {
		return msgID;
	}
	public void setMsgID(int msgID) {
		this.msgID = msgID;
	}
	
	public MsgType getMsgType() {
		return msgType;
	}
	public void setMsgType(MsgType msgType) {
		this.msgType = msgType;
	}
	
	public int getSenderID() {
		return senderID;
	}
	public void setSenderID(int senderID) {
		this.senderID = senderID;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
