package edu.northeastern.ccs.im.model;

/**
 * This class represents a model object for a messageText.
 */
public class Message {
	
	private int msgID;
	private MsgType msgType;
	private int senderID;
	private String messageText;
	private String timestamp;
	private Boolean isSecret;
	private String senderIP;
	private String chatSenderID;

	public enum MsgType {
		PVT, GRP, BCT;
	}

	/**
	 * Constructor for a messageText model object.
	 *
	 * @param msgID int representing the messageText ID
	 * @param msgType MsgType for the messageText
	 * @param senderID int representing the sender ID
	 * @param messageText string representing the text of the messageText
	 * @param timestamp string representing the time the messageText was sent
	 */
	public Message(int msgID, MsgType msgType, int senderID, String messageText, String timestamp) {
		super();
		this.msgID = msgID;
		this.msgType = msgType;
		this.senderID = senderID;
		this.messageText = messageText;
		this.timestamp = timestamp;
	}

	/**
	 * Constructor for a messageText model object.
	 *
	 * @param msgType MsgType for the messageText
	 * @param senderID int representing the sender ID
	 * @param messageText string representing the text of the messageText
	 * @param timestamp string representing the time the messageText was sent
	 */
	public Message(MsgType msgType, int senderID, String messageText, String timestamp) {
		this.msgType = msgType;
		this.senderID = senderID;
		this.messageText = messageText;
		this.timestamp = timestamp;
	}

	/**
	 * Constructor for a messageText model object.
	 */
	public Message() {
		// Auto-generated constructor stub
	}

	/**
	 * Method to get the ID# of the messageText.
	 *
	 * @return int representing the ID# of the messageText
	 */
	public int getMsgID() {
		return msgID;
	}

	/**
	 * Method to set the #ID of the messageText.
	 *
	 * @param msgID int representing the #ID of the messageText
	 */
	public void setMsgID(int msgID) {
		this.msgID = msgID;
	}

	/**
	 * Method to get the messageText type.
	 *
	 * @return MsgType representing the type of messageText
	 */
	public MsgType getMsgType() {
		return msgType;
	}

	/**
	 * Method to set the messageText type.
	 *
	 * @param msgType MsgType representing the type of messageText
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
	 * Method to get the text of the messageText.
	 *
	 * @return string representing the text of the messageText
	 */
	public String getMessageText() {
		return messageText;
	}

	/**
	 * Method to set the text of the messageText.
	 *
	 * @param messageText string representing the text of the messageText
	 */
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	/**
	 * Method to get the timestamp of the messageText.
	 *
	 * @return string representing the messageText timestamp.
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Method to set the timestamp of the messageText.
	 *
	 * @param timestamp string representing the messageText timestamp.
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public Boolean getSecret() {
		return isSecret;
	}

	public void setSecret(Boolean secret) {
		isSecret = secret;
	}

	public String getSenderIP() {
		return senderIP;
	}

	public void setSenderIP(String senderIP) {
		this.senderIP = senderIP;
	}

	public String getChatSenderID() {
		return chatSenderID;
	}

	public void setChatSenderID(String chatSenderID) {
		this.chatSenderID = chatSenderID;
	}


}
