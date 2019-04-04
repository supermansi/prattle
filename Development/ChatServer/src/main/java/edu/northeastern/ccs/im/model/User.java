package edu.northeastern.ccs.im.model;

/**
 * This class represents a model object for a user.
 */
public class User {

  private int userID;
  private String username;
  private String userFN;
  private String userLN;
  private String email;
  private String password;
  private String lastSeen;

  public enum UserParams {
    USERNAME, FIRSTNAME, LASTNAME, EMAIL;
  }

  /**
   * Constructor for a user model object.
   *
   * @param username string for username
   * @param userFN   string for first name
   * @param userLN   string for last name
   * @param email    string for email
   * @param password string for password
   */
  public User(String username, String userFN, String userLN, String email, String password) {
    super();
    this.username = username;
    this.userFN = userFN;
    this.userLN = userLN;
    this.email = email;
    this.password = password;
  }

  /**
   * Constructor for a user model object.
   *
   * @param userID   int for user #ID
   * @param username string for username
   * @param userFN   string for first name
   * @param userLN   string for last name
   * @param email    string for email
   * @param password string for password
   */
  public User(int userID, String username, String userFN, String userLN, String email, String password) {
    super();
    this.userID = userID;
    this.username = username;
    this.userFN = userFN;
    this.userLN = userLN;
    this.email = email;
    this.password = password;
  }

  /**
   * Method to get the user #ID.
   *
   * @return int representing the user #ID
   */
  public int getUserID() {
    return userID;
  }

  /**
   * Method to set the user #ID.
   *
   * @param userID int representing the user #ID
   */
  public void setUserID(int userID) {
    this.userID = userID;
  }

  /**
   * Method to get the username.
   *
   * @return string representing the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Method to set the username.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Method to get the user first name.
   *
   * @return string representing the user first name
   */
  public String getUserFN() {
    return userFN;
  }

  /**
   * Method to set the user first name.
   */
  public void setUserFN(String userFN) {
    this.userFN = userFN;
  }

  /**
   * Method to get the user last name.
   *
   * @return string representing the user last name
   */
  public String getUserLN() {
    return userLN;
  }

  /**
   * Method to set the user last name.
   */
  public void setUserLN(String userLN) {
    this.userLN = userLN;
  }

  /**
   * Method to get the user email.
   *
   * @return string representing the user email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Method to set the user email.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Method to get the user password.
   *
   * @return string representing the user password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Method to set the user password.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Method to get the last seen message.
   *
   * @return string representing time last seen
   */
  public String getLastSeen() {
    return lastSeen;
  }

  /**
   * Method to set the last seen message.
   */
  public void setLastSeen(String lastSeen) {
    this.lastSeen = lastSeen;
  }

}
