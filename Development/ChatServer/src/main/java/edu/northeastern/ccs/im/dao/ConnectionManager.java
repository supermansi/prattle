package edu.northeastern.ccs.im.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import edu.northeastern.ccs.im.ChatLogger;


public class ConnectionManager {
	
	Properties prop = new Properties();
	
	private static final String USER = "b9771ba524a91a";

	private static final String HOSTNAME = "us-cdbr-iron-east-03.cleardb.net";

	private static final int PORT = 3306;

	private static final String SCHEMA = "heroku_5b0785b4e92d159";

	public Connection getConnection() {
		Connection connection = null;
		
		try {
			InputStream inputStream = new FileInputStream("resources/config.properties");
			prop.load(inputStream);
		} catch (IOException e1) {
			ChatLogger.error(e1.getMessage());
		}
		try {
			Properties connectionProperties = new Properties();
			connectionProperties.put("user", USER);
			connectionProperties.put("password", prop.getProperty("password"));
			Class.forName("com.mysql.jdbc.Driver");

			connection = DriverManager.getConnection(
			    "jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" + SCHEMA,
			    connectionProperties);
		} catch (SQLException | ClassNotFoundException e) {
			ChatLogger.error(e.getMessage());
		}
		return connection;
	}

}
