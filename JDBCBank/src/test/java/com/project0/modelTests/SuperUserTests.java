package com.project0.modelTests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;

import com.project0.model.*;

public class SuperUserTests {
	
	private SuperUser admin = SuperUser.getAdmin();
	
	@Test
	public void getMasterUserNameTest() {
		String expected = "";
		InputStream in = null;
		Properties props = new Properties();
		try {
			in = new FileInputStream(new File("src/main/resources/admin.properties").getAbsolutePath());
			props.load(in);
			expected = props.getProperty("jdbc.masterID");
		} catch (Exception e) {
			
		}
		assertEquals(expected, admin.getMasterUserName());
	}

	@Test
	public void getMasterPassCodeTest() {
		String expected = "";
		InputStream in = null;
		Properties props = new Properties();
		try {
			in = new FileInputStream(new File("src/main/resources/admin.properties").getAbsolutePath());
			props.load(in);
			expected = props.getProperty("jdbc.masterPassword");
		} catch (Exception e) {
			
		}
		assertEquals(expected, admin.getMasterPassCode());
	}
	
	@Test
	public void getMasterPinTest() {
		String expected = "";
		InputStream in = null;
		Properties props = new Properties();
		try {
			in = new FileInputStream(new File("src/main/resources/admin.properties").getAbsolutePath());
			props.load(in);
			expected = props.getProperty("jdbc.masterPin");
		} catch (Exception e) {
			
		}
		assertEquals(expected, admin.getMasterPin());
	}
	
	@Test
	public void toStringTest() {
		String ID = "";
		String password = "";
		String pin = "";
		InputStream in = null;
		Properties props = new Properties();
		try {
			in = new FileInputStream(new File("src/main/resources/admin.properties").getAbsolutePath());
			props.load(in);
			ID = props.getProperty("jdbc.masterID");
			password = props.getProperty("jdbc.masterPassword");
			pin = props.getProperty("jdbc.masterPin");
		} catch (Exception e) {
			
		}
		String expected = "SuperUser [masterID=" + ID + ", masterPassWord=" + 
		password + ", masterPin=" + pin + "]";
		assertEquals(expected, admin.toString());
	}
	
	@Test
	public void hashCodeTest() {
		int result = 31 + (admin.getMasterUserName().hashCode());
		result = 31*result + admin.getMasterPassCode().hashCode();
		result = 31*result + admin.getMasterPin().hashCode();
		assertEquals(result, admin.hashCode());
	}
}
