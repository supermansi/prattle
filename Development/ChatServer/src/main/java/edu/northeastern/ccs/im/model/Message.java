package edu.northeastern.ccs.im.model;

import java.sql.Timestamp;

public class Message {
	
	private int msgID;
	private MsgType msgType;
	private int senderID;
	private String message;
	private Timestamp timestamp;

	public enum MsgType {
		PVT, GRP, BCT;
	}
		
	public Message(int msgID, MsgType msgType, int senderID, String message, Timestamp timestamp) {
		super();
		this.msgID = msgID;
		this.msgType = msgType;
		this.senderID = senderID;
		this.message = message;
		this.timestamp = timestamp;
	}
	public Message(MsgType msgType, int senderID, String message, Timestamp timestamp) {
		this.msgType = msgType;
		this.senderID = senderID;
		this.message = message;
		this.timestamp = timestamp;
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
	
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}
