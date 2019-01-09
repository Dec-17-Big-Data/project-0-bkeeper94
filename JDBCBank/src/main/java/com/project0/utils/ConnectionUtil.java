package com.project0.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionUtil {

	private static Connection connectionInstance = null;

	private static Logger log = LogManager.getLogger(ConnectionUtil.class);

	private ConnectionUtil() {

	}

	public static Connection getConnection() {
		if (ConnectionUtil.connectionInstance != null) {
			return ConnectionUtil.connectionInstance;
		}

		InputStream in = null;

		try {

			Properties props = new Properties();
			in = new FileInputStream(new File("src/main/resources/connection.properties").getAbsolutePath());
			props.load(in);

			Class.forName("oracle.jdbc.driver.OracleDriver"); // example of reflection
			Connection con = null;

			String endpoint = props.getProperty("jdbc.url");
			String username = props.getProperty("jdbc.username");
			String password = props.getProperty("jdbc.password");

			con = DriverManager.getConnection(endpoint, username, password);
			connectionInstance = con;
			log.trace("Connection Established");
			return con;
		} catch (Exception e) {
			log.error("Unable to get connection to database");
		} finally {
			try {
				in.close();
			} catch (IOException e) {

			}
		}
		return null;
	}
}
