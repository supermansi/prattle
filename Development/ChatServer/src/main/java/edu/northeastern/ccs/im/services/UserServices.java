/** Copyright (c) 2019 Rohan Gori, Aditi Kacheria, Mansi Jain, Joshua Dick. All rights reserved.*/
package edu.northeastern.ccs.im.services;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import edu.northeastern.ccs.im.PasswordHash;
import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.exceptions.DatabaseConnectionException;
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
   * @param userFN   user's first name
   * @param userLN   user's last name
   * @param email    user's email
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
   * @param username         user's username
   * @param updatedFirstName new first name
   */
  public static void updateFN(String username, String updatedFirstName) throws SQLException {
    userDAO.updateFirstName(username, updatedFirstName);
  }

  /**
   * Method to update a user's last name.
   *
   * @param username        user's username
   * @param updatedLastName new last name
   */
  public static void updateLN(String username, String updatedLastName) throws SQLException {
    userDAO.updateLastName(username, updatedLastName);
  }

  /**
   * Method to update a user's password.
   *
   * @param username        user's username
   * @param updatedPassword new password
   */
  public static void updatePassword(String username, String updatedPassword) throws SQLException {
    userDAO.updatePassword(username, PasswordHash.hashPassword(updatedPassword));
  }

  /**
   * Method to update a user's email.
   *
   * @param username     user's username
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
   * @param time     string representing time stamp
   */
  public static void updateLastSeen(String username, Long time) throws SQLException {
    String lastSeen = Long.toString(time);
    userDAO.updateLastSeen(username, lastSeen);
  }

  /**
   * Method to get the timestamp of the last message read by a user.
   *
   * @param username the user to search for
   * @return a time of the last message seen by the given user
   * @throws SQLException if the database cannot establish a connection
   */
  public static Long getLastSeen(String username) throws SQLException {
    String lastSeen = userDAO.getLastSeen(username);
    return Long.parseLong(lastSeen);
  }

  /**
   * Method to check if a given user exists.
   *
   * @param username the username to search for
   * @return true if the user exists, false otherwise
   * @throws SQLException if the database cannot establish a connection
   */
  public static boolean userExists(String username) throws SQLException {
    return userDAO.isUserExists(username);
  }


  /**
   * Method to get a map of the user's profile attributes.
   *
   * @param username the username to search for
   * @return a map of the user's profile attributes
   * @throws SQLException if the database cannot establish a connection
   */
  public static ConcurrentMap<User.UserParams, String> getUserProfile(String username) throws SQLException {
    ConcurrentMap<User.UserParams, String> userProfile = new ConcurrentHashMap<>();
    User user = userDAO.getUserProfile(userDAO.getUserByUsername(username).getUserID());
    userProfile.put(User.UserParams.USERNAME, user.getUsername());
    userProfile.put(User.UserParams.FIRSTNAME, user.getUserFN());
    userProfile.put(User.UserParams.LASTNAME, user.getUserLN());
    userProfile.put(User.UserParams.EMAIL, user.getEmail());
    return userProfile;
  }

  /**
   * Method to add a user to another user's list of followers.
   *
   * @param follower the person who wants to follow another user
   * @param following the user to be followed
   * @throws SQLException if the database cannot establish a connection
   */
  public static void followUser(String follower, String following) throws SQLException {
    try {
      userDAO.followUser(follower, following);
    } catch (SQLException e) {
      throw new DatabaseConnectionException("Unable to follow user");
    }
  }

  /**
   * Method to remove a user from another user's follower list.
   *
   * @param follower the person who wants to unfollow another user
   * @param following the user to be unfollowed
   * @throws SQLException if the database cannot establish a connection
   */
  public static void unFollowUser(String follower, String following) throws SQLException {
    try {
      userDAO.unfollow(follower, following);
    } catch (SQLException e) {
      throw new DatabaseConnectionException("Unable to un-follow user");
    }
  }

  /**
   * Method to get a list of a user's followers.
   *
   * @param username the user to search for
   * @return a list of a user's followers
   * @throws SQLException if the database cannot establish a connection
   */
  public static List<String> getFollowers(String username) throws SQLException {
    return userDAO.getFollowers(username);
  }

  /**
   * Method to get a list of who a user is following.
   *
   * @param username the user to search for
   * @return a list of who that user is following
   * @throws SQLException if the database cannot establish a connection
   */
  public static List<String> getFollowing(String username) throws SQLException {
    return userDAO.getFollowing(username);
  }

  /**
   * Method to get a list of all the wiretapped users.
   *
   * @return a list of all the wiretapped users
   * @throws SQLException if the database cannot establish a connection
   */
  public static List<String> getListOfTappedUsers() throws SQLException {
    return userDAO.getListOfTappedUsers();
  }

  /**
   * Method to turn a wiretap on a user on or off.
   *
   * @param username the user to be tapped/ untapped
   * @param isTapped true to tap the user, false otherwise
   * @throws SQLException if the database cannot establish a connection
   */
  public static void setWireTapStatus(String username, boolean isTapped) throws SQLException {
    if (userDAO.isUserExists(username)) {
      if (isTapped == userDAO.getUserByUsername(username).isTapped()) {
        throw new IllegalStateException("The current wire tapped status of the user is the same as that trying to be set.");
      } else {
        userDAO.setWireTappedStatus(username, isTapped);
      }
    } else {
      throw new DatabaseConnectionException("User not found");
    }
  }
}