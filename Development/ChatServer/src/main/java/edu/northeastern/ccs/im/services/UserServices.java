package edu.northeastern.ccs.im.services;

import edu.northeastern.ccs.im.dao.UserDAO;

public class UserServices {
	
	private UserDAO userDAO = new UserDAO();
	
	public boolean login(String username, String password) {
		return  true;
	}
	
	public boolean register(String username, String password, String userFN,
						String userLN, String email) {
		return true;
	}
	

}
