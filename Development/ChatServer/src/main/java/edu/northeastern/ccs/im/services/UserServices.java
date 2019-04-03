package edu.northeastern.ccs.im.services;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import edu.northeastern.ccs.im.PasswordHash;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.model.User;

/**
 * Class for the user services.
 */
public class UserServices {

  private static UserDAO userDAO = UserDAO.getInstance();

  /**
   * Private constructor for user services.
   */
  private UserServices() {
    //empty private constructor
  }

  /**
   * Method to validate a user's name and password upon logging in.
   *
   * @param username the user's name
   * @param password the user's password
   * @return true if the fields match, false otherwise
   */
  public static boolean login(String username, String password) throws SQLException {
    return userDAO.validateUser(username, PasswordHash.hashPassword(password));
  }

  /**
   * Method to register and store a new user in the database.
   *
   * @param username user's user name
   * @param password user's password
   * @param userFN user's first name
   * @param userLN user's last name
   * @param email user's email
   * @return true if the user has been registered and stored in teh database, false otherwise
   */
  public static boolean register(String username, String password, String userFN,
                                 String userLN, String email) throws SQLException {

    if (userDAO.isUserExists(username)) {
      return false; // user exists
    } else {
      User registerUser = new User(username, userFN, userLN, email, PasswordHash.hashPassword(password));
      userDAO.createUser(registerUser);
      return true; // user does not exist and is created
    }
  }

  /**
   * Method to update a user's first name.
   *
   * @param username user's username
   * @param updatedFirstName new first name
   */
  public static void updateFN(String username, String updatedFirstName) throws SQLException {
    userDAO.updateFirstName(username, updatedFirstName);
  }

  /**
   * Method to update a user's last name.
   *
   * @param username user's username
   * @param updatedLastName new last name
   */
  public static void updateLN(String username, String updatedLastName) throws SQLException {
    userDAO.updateLastName(username, updatedLastName);
  }

  /**
   * Method to update a user's password.
   *
   * @param username user's username
   * @param updatedPassword new password
   */
  public static void updatePassword(String username, String updatedPassword) throws SQLException {
    userDAO.updatePassword(username, PasswordHash.hashPassword(updatedPassword));
  }

  /**
   * Method to update a user's email.
   *
   * @param username user's username
   * @param updatedEmail new email
   */
  public static void updateEmail(String username, String updatedEmail) throws SQLException {
    userDAO.updateEmail(username, updatedEmail);
  }

  /**
   * Method to delete a user from the database
   *
   * @param username user name of user to be delete
   */
  public static void deleteUser(String username) throws SQLException {
    if (userDAO.isUserExists(username)) {
      userDAO.deleteUser(username);
    } else {
      throw new IllegalArgumentException("User cannot be deleted because user does not exist.");
    }
  }

  /**
   * Method to update the timestamp stored in the database of a user's last seen message.
   *
   * @param username user's username
   * @param time string representing time stamp
   */
  public static void updateLastSeen(String username, Long time) throws SQLException {
    String lastSeen = Long.toString(time);
    userDAO.updateLastSeen(username, lastSeen);
  }

  public static Long getLastSeen(String username) throws SQLException {
    String lastSeen = userDAO.getLastSeen(username);
    return Long.parseLong(lastSeen);
  }

  public static boolean userExists(String username) throws SQLException {
    return userDAO.isUserExists(username);
  }

  public static Map<User.UserParams, String> getUserProfile(String username) throws SQLException {
    if (userDAO.isUserExists(username)) {
      Map<User.UserParams, String> userProfile = new HashMap<>();
      User user = userDAO.getUserProfile(userDAO.getUserByUsername(username).getUserID());
      userProfile.put(User.UserParams.USERNAME, user.getUsername());
      userProfile.put(User.UserParams.FIRSTNAME, user.getUserFN());
      userProfile.put(User.UserParams.LASTNAME, user.getUserLN());
      userProfile.put(User.UserParams.EMAIL, user.getEmail());
      return userProfile;
    } else {
      throw new IllegalArgumentException("User with this username does not exist");
    }
  }
}