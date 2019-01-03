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
			in = new FileInputStream(new File("src/main/resources/connection.properties").getAbsolutePath());
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SuperUser other = (SuperUser) obj;
		if (masterID == null) {
			if (other.masterID != null)
				return false;
		} else if (!masterID.equals(other.masterID))
			return false;
		if (masterPassWord == null) {
			if (other.masterPassWord != null)
				return false;
		} else if (!masterPassWord.equals(other.masterPassWord))
			return false;
		if (masterPin == null) {
			if (other.masterPin != null)
				return false;
		} else if (!masterPin.equals(other.masterPin))
			return false;
		return true;
	}
	
}
