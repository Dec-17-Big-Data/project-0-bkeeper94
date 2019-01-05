package com.project0.modelTests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.project0.model.*;

public class BankAccountTests {
	
	private static final BankAccount ba = new BankAccount(1000, "checking", 
			"0000000000", 1000, 5000.00);
	
	@Test
	public void getAccountIDTest() {
		Integer expected = 1000;
		assertEquals(expected, ba.getAccountID());
	}

	@Test
	public void getAccountTypeTest() {
		assertEquals("checking", ba.getAccountType());
	}
	
	@Test
	public void setAccountTypeTest() {
		ba.setAccountType("savings");
		assertEquals("savings", ba.getAccountType());
		ba.setAccountType("checking");
	}
	
	@Test
	public void getAccountNoTest() {
		assertEquals("0000000000", ba.getAccountNo());
	}
	
	@Test
	public void getMemberIDTest() {
		Integer expected = 1000;
		assertEquals(expected, ba.getMemberID());
	}
	
	@Test
	public void getBalanceTest() {
		Double balance = 5000.00;
		assertEquals(balance, ba.getBalance());
	}
	
	@Test
	public void setBalanceTest() {
		Double newBalance = 10000.00;
		ba.setBalance(newBalance);
		assertEquals(newBalance, ba.getBalance());
		Double oldBalance = 5000.00;
		ba.setBalance(oldBalance);
	}
	
	@Test
	public void toStringTest() {
		// Test to make sure the toString method prints each attribute in the correct order
		String expected = "BankAccount [accountID=" + ba.getAccountID() + ", accountType=" + 
		ba.getAccountType() + ", accountNo=" + ba.getAccountNo() + ", memberID=" + ba.getMemberID() +
		", balance=" + ba.getBalance() + "]";
		assertEquals(expected, ba.toString());
	}
	
	@Test
	public void hashCodeTest() {
		int result = 31 + (ba.getAccountID().hashCode());
		result = 31*result + ba.getAccountNo().hashCode();
		result = 31*result + ba.getAccountType().hashCode();
		result = 31*result + ba.getBalance().hashCode();
		result = 31*result + ba.getMemberID().hashCode();
		assertEquals(result, ba.hashCode());
	}
	
	@Test
	public void equalsTest() {
		// Test two completely different objects to make sure equals returns false
		BankAccount fakeAccount = new BankAccount();
		assertFalse(fakeAccount.equals(ba));
		
		// Test two objects that differ only by one attribute to make sure equals still returns false
		BankAccount almostBA = new BankAccount(1001, "checking", "0000000000", 1000, 5000.00);
		assertFalse(almostBA.equals(ba));		
	}
}
