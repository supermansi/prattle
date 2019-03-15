package edu.northeastern.ccs.im.model;

/**
 * This class represents a model object for a group.
 */
public class Groups {
	
	private int grpID;
	private String grpName;
	private int adminID;

	/**
	 * Constructor for group object.
	 *
	 * @param grpName name of the group
	 * @param adminID id for the admin of the group
	 */
	public Groups(String grpName, int adminID) {
		this.grpName = grpName;
		this.adminID = adminID;
	}

	/**
	 * Constructor for group object.
	 *
	 * @param grpID id# for the group
	 * @param grpName name of the group
	 * @param adminID id# of the group admin
	 */
	public Groups(int grpID, String grpName, int adminID) {
		this.grpID = grpID;
		this.grpName = grpName;
		this.adminID = adminID;
	}

	/**
	 * Method to get the group ID.
	 *
	 * @return int representing the group id
	 */
	public int getGrpID() {
		return grpID;
	}

	/**
	 * Method to set the group ID.
	 *
	 * @param grpID int representing the group ID
	 */
	public void setGrpID(int grpID) {
		this.grpID = grpID;
	}

	/**
	 * Method to get the group name.
	 *
	 * @return string representing the group name
	 */
	public String getGrpName() {
		return grpName;
	}

	/**
	 * Method to set the group name.
	 *
	 * @param grpName string to set the group name to
	 */
	public void setGrpName(String grpName) {
		this.grpName = grpName;
	}

	/**
	 * Method to get the admin id.
	 *
	 * @return int representing the admin id#
	 */
	public int getAdminID() {
		return adminID;
	}

	/**
	 * Method to set the admin id#.
	 *
	 * @param adminID int representing the new admin id number
	 */
	public void setAdminID(int adminID) {
		this.adminID = adminID;
	}

}
