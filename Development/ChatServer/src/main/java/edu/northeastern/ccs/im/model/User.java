package edu.northeastern.ccs.im.model;

public class User {
	
	private int userID;
	private String username;
	private String userFN;
	private String userLN;
	private String email;
	private String password;
	private String lastSeen;
	
	public User(String username, String userFN, String userLN, String email, String password) {
		super();
		this.username = username;
		this.userFN = userFN;
		this.userLN = userLN;
		this.email = email;
		this.password = password;
	}

	public User(int userID, String username, String userFN, String userLN, String email, String password) {
		super();
		this.userID = userID;
		this.username = username;
		this.userFN = userFN;
		this.userLN = userLN;
		this.email = email;
		this.password = password;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
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

	public String getLastSeen() {
		return lastSeen;
	}
	public void setLastSeen(String lastSeen) {
		this.lastSeen = lastSeen;
	}

}
