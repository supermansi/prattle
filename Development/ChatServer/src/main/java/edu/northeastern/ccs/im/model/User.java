package edu.northeastern.ccs.im.model;

public class User {
	
	private int userID;
	private String userFN;
	private String userLN;
	private String email;
	private String password;
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public String getUserFN() {
		return userFN;
	}
	public void setUserFN(String userFN) {
		this.userFN = userFN;
	}
	
	public String getUserLN() {
		return userLN;
	}
	public void setUserLN(String userLN) {
		this.userLN = userLN;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
