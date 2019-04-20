/** Copyright (c) 2019 Rohan Gori, Aditi Kacheria, Mansi Jain, Joshua Dick. All rights reserved.*/
package edu.northeastern.ccs.im.model;

/**
 * This class represents a model object for a group to user mapping.
 */
public class GroupToUserMap {
	
	private int id;
	private int userID;
	private int groupID;

	/**
	 * Constructor for a group to user map object.
	 *
	 * @param userID int representing the user ID
	 * @param groupID int representing the group ID
	 */
	public GroupToUserMap(int userID, int groupID) {
		this.userID = userID;
		this.groupID = groupID;
	}

	/**
	 * Method to get the id of this object.
	 *
	 * @return int representing the id of this model object
	 */
	public int getId() {
		return id;
	}

	/**
	 * Method to set the id of this object.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Method to get the user ID.
	 *
	 * @return int representing the user ID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * Method to set the user ID.
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * Method to get the group ID.
	 *
	 * @return int representing the group ID
	 */
	public int getGroupID() {
		return groupID;
	}

	/**
	 * Method to set the group ID.
	 *
	 * @param groupID int representing the group ID
	 */
	public void setGroupID(int groupID) {
		this.groupID = groupID;
	}

}
