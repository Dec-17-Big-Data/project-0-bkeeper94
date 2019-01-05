package com.project0.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SuperUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1555027246593813605L;
	private String masterID;
	private String masterPassWord;
	private String masterPin;
	
	private static Logger log = LogManager.getLogger(SuperUser.class);

	private static SuperUser adminInstance = null;

	private SuperUser() {
		InputStream in = null;
		Properties props = new Properties();
		try {
			in = new FileInputStream(new File("src/main/resources/admin.properties").getAbsolutePath());
			props.load(in);
			this.masterID = props.getProperty("jdbc.masterID");
			this.masterPassWord = props.getProperty("jdbc.masterPassword");
			this.masterPin = props.getProperty("jdbc.masterPin");
		} catch (Exception e) {
			log.error("Unable to obtain admin login information");
		}
	}

	public static SuperUser getAdmin() {
		if (adminInstance == null) {
			adminInstance = new SuperUser();
		}
		return adminInstance;
	}

	public String getMasterUserName() {
		return masterID;
	}

	public String getMasterPassCode() {
		return masterPassWord;
	}

	public String getMasterPin() {
		return masterPin;
	}

	// No setters for the attributes of SuperUser as they are never modified after assignment
	// To change these values, change them in the admin.properties file in src/main/resources
	
	@Override
	public String toString() {
		return "SuperUser [masterID=" + masterID + ", masterPassWord=" + masterPassWord + ", masterPin=" + masterPin
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((masterID == null) ? 0 : masterID.hashCode());
		result = prime * result + ((masterPassWord == null) ? 0 : masterPassWord.hashCode());
		result = prime * result + ((masterPin == null) ? 0 : masterPin.hashCode());
		return result;
	}

	// No override for the equals method because this class is a singleton
}
