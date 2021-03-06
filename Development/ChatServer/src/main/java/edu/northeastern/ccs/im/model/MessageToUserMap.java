/** Copyright (c) 2019 Rohan Gori, Aditi Kacheria, Mansi Jain, Joshua Dick. All rights reserved.*/
package edu.northeastern.ccs.im.model;

/**
 * Class for mapping messages to users.
 */
public class MessageToUserMap {
	
	private int id;
	private int msgID;
	private int receiverID;
	private String receiverIP;

	/**
	 * Method to get the #ID of this mapping.
	 *
	 * @return int representing the #ID of this mapping
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method to set the #ID of this mapping.
	 *
	 * @param id int representing the #ID of this mapping
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method to get the #ID of the message.
	 *
	 * @return int representing the #ID of this message
	 */
	public int getMsgID() {
		return msgID;
	}

	/**
	 * Method to set the #ID of the message.
	 *
	 * @param msgID int representing the #ID of this message
	 */
	public void setMsgID(int msgID) {
		this.msgID = msgID;
	}

	/**
	 * Method to get the #ID of the receiver.
	 *
	 * @return int representing the #ID of this receiver
	 */
	public int getReceiverID() {
		return receiverID;
	}

	/**
	 * Method to set the #ID of the receiver.
	 *
	 * @param receiverID int representing the #ID of this receiver
	 */
	public void setReceiverID(int receiverID) {
		this.receiverID = receiverID;
	}

	/**
	 * Method to get the receiver ip.
	 *
	 * @return the receiver ip as a string
	 */
	public String getReceiverIP() {
		return receiverIP;
	}

	/**
	 * Method to set the receiver ip.
	 *
	 * @param receiverIP the receiver ip as a string
	 */
	public void setReceiverIP(String receiverIP) {
		this.receiverIP = receiverIP;
	}
}
