package com.project0.modelTests;

import com.project0.model.*;

import static org.junit.Assert.*;

import org.junit.Test;

public class BankMemberTest {
	
	private static final BankMember bm = new BankMember(1000, "jrdoe", "jon", 
			"doe", "pa55w0rd", "0000");
	
	@Test
	public void getUserIDTest() {
		Integer expected = 1000;
		assertEquals(expected, bm.getUserID());
	}

	@Test
	public void getUserNameTest() {
		assertEquals("jrdoe", bm.getUserName());
	}
	
	@Test
	public void setUserNameTest() {
		bm.setUserName("jrdoe1");
		assertEquals("jrdoe1", bm.getUserName());
		bm.setUserName("jrdoe");
	}
	
	@Test
	public void getFirstNameTest() {
		assertEquals("jon", bm.getFirstName());
	}
	
	@Test
	public void setFirstNameTest() {
		bm.setFirstName("joe");
		assertEquals("joe", bm.getFirstName());
		bm.setFirstName("jon");
	}
	
	@Test
	public void getLastNameTest() {
		assertEquals("doe", bm.getLastName());
	}
	
	@Test
	public void setLastNameTest() {
		bm.setLastName("blow");
		assertEquals("blow", bm.getLastName());
		bm.setLastName("doe");
	}
	
	@Test
	public void getPassWordTest() {
		assertEquals("pa55w0rd", bm.getPassWord());
	}
	
	@Test
	public void setPassWordTest() {
		bm.setPassWord("p4ssw_rd");
		assertEquals("p4ssw_rd", bm.getPassWord());
		bm.setPassWord("pa55w0rd");
	}
	
	@Test
	public void getPinNumberTest() {
		assertEquals("0000", bm.getPinNumber());
	}
	
	@Test
	public void setPinNumberTest() {
		bm.setPinNumber("9876");
		assertEquals("9876", bm.getPinNumber());
		bm.setPinNumber("0000");
	}
	
	@Test
	public void toStringTest() {
		BankMember johnDoeLog4J = new BankMember(1, "jdoe123", "john", "doe", "password", "1234");
		// Test to show that the most current toString method outputs attributes in the correct order
		
		// Testing toString with old output from log.traceEntry on BankMember
		// that showed incorrect output from the toString method
		
		// If the toString method works correctly, then the two strings belowe will not be the same
		String oldLog4jToString = "BankMember [userName=john, passWord=password, "
				+ "firstName=doe, lastName=jdoe123, pinNumber=1234]";
		String correctToString = johnDoeLog4J.toString();
		assertFalse(oldLog4jToString.compareTo(correctToString) == 0);
	}
	
	@Test
	public void hashCodeTest() {
		int result = 31 + (bm.getFirstName().hashCode());
		result = 31*result + bm.getLastName().hashCode();
		result = 31*result + bm.getPassWord().hashCode();
		result = 31*result + bm.getPinNumber().hashCode();
		result = 31*result + bm.getUserName().hashCode();
		assertEquals(result, bm.hashCode());
	}
	
	@Test
	public void equalsTest() {
		// Test two completely different objects to make sure equals returns false
		BankMember notJonDoe = new BankMember();
		assertFalse(notJonDoe.equals(bm));
		
		// Test two objects that differ only by one attribute to make sure equals still returns false
		BankMember almostJonDoe = new BankMember(1001, "jrdoe2", "jon", 
				"doe", "pa55w0rd", "0000");
		assertFalse(almostJonDoe.equals(bm));
	}
}
