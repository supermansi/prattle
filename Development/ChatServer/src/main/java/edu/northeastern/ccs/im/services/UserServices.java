package edu.northeastern.ccs.im.services;

import java.sql.SQLException;

import edu.northeastern.ccs.im.dao.UserDAO;
import edu.northeastern.ccs.im.model.User;

public class UserServices {
	
	private static UserDAO userDAO = UserDAO.getInstance();

	private UserServices() {
		//empty private constructor
	}
	
	public static boolean login(String username, String password) throws SQLException {
		return userDAO.validateUser(username,password);
	}
	
	public static boolean register(String username, String password, String userFN,
						String userLN, String email) throws SQLException {
		if(userDAO.isUserExists(username)) {
			return false; // user exists
		}
		else {
			User registerUser = new User(username, userFN, userLN, email, password);
			userDAO.createUser(registerUser);
			return true; // user does not exist and is created
		}
	}
	

}
