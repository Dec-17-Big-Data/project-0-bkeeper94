package com.project0.serviceTests;

import com.project0.model.*;
import com.project0.exceptions.*;
import com.project0.services.*;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // Executes unit tests in ABC order

//Important note: Do not run these JUnit tests individually

//Important note: Connection failures cause these unit tests to fail (check logfile in "logs" folder for connection failures)
public class BankMemberServiceTests {
	
	private static BankMemberService bms = BankMemberService.getService();
	
	// Important note: Do not remove the test user's record from the database and do not add any accounts to the test user
	private static BankMember testUser = new BankMember(1, "testuser", "joe", 
			"blow", "password", "7890");
	
	@Test
	public void addNewUserTest() throws DuplicateUserNameException, NoSuchElementException {
		// Test adding a new test user
		String newUserFirstName = "jane";
		String newUserLastName = "blow";
		String newUserName = "testuser2";
		String newPassword = "password";
		String newPin = "3456";
		assertEquals(1, bms.addNewUser(newUserFirstName, newUserLastName, newUserName, newPassword, newPin));
		
		// Test an attempt to repeat adding the above test user  
		assertEquals(0, bms.addNewUser(newUserFirstName, newUserLastName, newUserName, newPassword, newPin));
		
		// Unable to find an edge case that would throw an SQL exception. No test for case when addNewUser method returns -1
		
		// Remove this test user so the JUnit test can pass multiple times
		bms.performMemberRemoval(bms.getBankMember(newUserName));
	}
	
	@Test
	public void getBankMemberTest() throws NoSuchElementException {
		// Test this getter on the testUser's user name
		assertTrue(bms.getBankMember(testUser.getUserName()).equals(testUser));
		
		// Test this getter on a user that is not in the database
		assertTrue(bms.getBankMember("linus.torvald") == null);
	}
	
	@Test
	public void loginAuthenticationTest() throws LoginMismatchException {
		// Test a user name input that is not in the database
		assertFalse(bms.loginAuthentication("linus.torvald", "password"));
		
		// Test a password input that does not match the password in the database
		assertFalse(bms.loginAuthentication("testuser", "password123"));
		
		// Test a correct combination of user name and password
		assertTrue(bms.loginAuthentication("testuser", "password"));
	}
	
	@Test
	public void nameInputCheckTest() throws InvalidNameInputException {
		// Test the empty string
		assertFalse(bms.nameInputChecker(""));
		
		// Test a valid name that has an unintentional trailing space
		assertTrue(bms.nameInputChecker("Ben "));
		
		// Test a valid name that has a hyphen in it
		assertTrue(bms.nameInputChecker("Smith-Schuster"));
		
		// Test a name surrounded by invalid symbols and numbers
		assertFalse(bms.nameInputChecker("!@#$Ben Keeper%^&*()"));
		
		// Test an invalid name
		assertFalse(bms.nameInputChecker(""));
	}
	
	@Test
	public void performFirstNameUpdate() throws NoSuchElementException {
		// Test an update of testUser's name 
		assertTrue(bms.performFirstNameUpdate(testUser, "Joseph"));
		bms.performFirstNameUpdate(testUser, "joe");
		
		// Unable to find an edge case that would throw an SQL exception
	}
	
	@Test
	public void performLastNameUpdate() throws NoSuchElementException {
		// Test an update of testUser's name 
		assertTrue(bms.performLastNameUpdate(testUser, "Bloe"));
		bms.performLastNameUpdate(testUser, "blow");
		
		// Unable to find an edge case that would throw an SQL exception
	}
		
	@Test
	public void performMemberRemovalTest() throws NoSuchElementException {
		// Add Jane Blow, the BankMember added in addNewUserTest, back in
		String newUserFirstName = "jane";
		String newUserLastName = "blow";
		String newUserName = "testuser2";
		String newPassword = "password";
		String newPin = "3456";
		bms.addNewUser(newUserFirstName, newUserLastName, newUserName, newPassword, newPin);
		
		// Test removing Jane Blow
		assertTrue(bms.performMemberRemoval(bms.getBankMember("testuser2")));
		
		// Test an attempt to remove Jane Blow again
		assertFalse(bms.performMemberRemoval(bms.getBankMember("testuser2")));
		
		// Unable to find an edge case that would throw an SQL exception
	}
	
	@Test
	public void performPasswordUpdate() throws NoSuchElementException {
		// Test an update of testUser's password
		assertTrue(bms.performPasswordUpdate(testUser, "pa55w0rd"));
		bms.performPasswordUpdate(testUser, "password");
		
		// Unable to find an edge case that would throw an SQL exception
	}
	
	@Test
	public void performPinUpdate() throws NoSuchElementException {
		// Test an update of testUser's name 
		assertTrue(bms.performPinUpdate(testUser, "0000"));
		bms.performPinUpdate(testUser, "7890");
		
		// Unable to find an edge case that would throw an SQL exception
	}
	
	@Test
	public void performUserNameUpdateTest() throws DuplicateUserNameException, NoSuchElementException {
		// Test a valid user name update
		assertEquals(1, bms.performUsernameUpdate(testUser, "testuser123"));
		bms.performUsernameUpdate(testUser, "testuser");
		
		// Test a user name update that would result in a duplicate user name
		// This test case uses another test user with user name 'benkeeper'
		// Important note: do not remove bankmember with user name 'benkeeper' 
		assertEquals(0, bms.performUsernameUpdate(testUser, "benkeeper"));
		
		// Unable to find an edge case that would throw an SQL exception
	}
	
	@Test
	public void retrieveAMembersBankAccountsTest() {
		// Test a BankMember that is null
		assertTrue(bms.retrieveAMembersBankAccounts(null) == null);
		
		// Test retrieving bank accounts for a member that is in the system but has not opened their first account
		assertTrue(bms.retrieveAMembersBankAccounts(testUser).isEmpty());
		
		// Test retrieving bank accounts for a member that is not in the database
		BankMember notJoeBlow = new BankMember(0, "testuser", "john", 
				"blow", "password", "7890");
		assertTrue(bms.retrieveAMembersBankAccounts(notJoeBlow).isEmpty());
		
		// Unable to find an edge case that would throw an SQL exception
		// No test case for when retrieveAMembersBankAccounts would return null 
	}
	
	@Test
	public void showRegisteredUsersTest() {
		// Test a request to display all registered users
		assertFalse(bms.showRegisteredUsers().isEmpty());
		
		// Unable to find an edge case that would throw an SQL exception
	}
}
