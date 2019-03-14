package edu.northeastern.ccs.im.services;

import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.model.User;

public class UserServices {

	private static UserDAO userDAO = UserDAO.getInstance();

	private UserServices() {
		//empty private constructor
	}

	public static boolean login(String username, String password) {
		return userDAO.validateUser(username,password);
	}

	public static boolean register(String username, String password, String userFN,
									 String userLN, String email) {
		if(userDAO.isUserExists(username)) {
			return false; // user exists
		}
		else {
			User registerUser = new User(username, userFN, userLN, email, password);
			userDAO.createUser(registerUser);
			return true; // user does not exist and is created
		}
	}

	public static void deleteUser(String username) {
		if(userDAO.isUserExists(username)) {
			userDAO.deleteUser(username);
		}
		else {
			throw new IllegalArgumentException("User cannot be deleted because user does not exist.");
		}
	}

	public static void updateFN(String username, String updatedFirstName) {
		userDAO.updateFirstName(username,updatedFirstName);
	}
	
	public static void updateLN(String username, String updatedLastName) {
		userDAO.updateLastName(username,updatedLastName);
	}

	public static void updatePassword(String username, String updatedPassword) {
		userDAO.updatePassword(username,updatedPassword);
	}

	public static void updateEmail(String username, String updatedEmail) {
		userDAO.updateEmail(username,updatedEmail);
	}
	
	public static void updateLastSeen(String username, Long time) {
		String lastSeen = Long.toString(time);
		userDAO.updateLastSeen(username, lastSeen);
	}
}