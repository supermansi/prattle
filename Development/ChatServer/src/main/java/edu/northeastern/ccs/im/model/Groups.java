package edu.northeastern.ccs.im.model;

/**
 * This class represents a model object for a group.
 */
public class Groups {

  private int grpID;
  private String grpName;
  private String admins;
  private Restricted restricted;
  private boolean isThread;

  public enum Restricted {
    H, L;
  }

  /**
   * Constructor for group object.
   *
   * @param grpName name of the group
   * @param admins  id for the admin of the group
   */
  public Groups(String grpName, String admins) {
    this.grpName = grpName;
    this.admins = admins;
  }

  /**
   * Constructor for group object.
   *
   * @param grpID   id# for the group
   * @param grpName name of the group
   * @param admins  id# of the group admin
   */
  public Groups(int grpID, String grpName, String admins) {
    this.grpID = grpID;
    this.grpName = grpName;
    this.admins = admins;
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
  public String getAdmins() {
    return admins;
  }

  /**
   * Method to set the admin id#.
   *
   * @param admins int representing the new admin id number
   */
  public void setAdmins(String admins) {
    this.admins = admins;
  }

  public Restricted getRestricted() {
    return restricted;
  }

  public void setRestricted(Restricted restricted) {
    this.restricted = restricted;
  }

  public boolean isThread() {
    return isThread;
  }

  public void setThread(boolean thread) {
    isThread = thread;
  }
}
