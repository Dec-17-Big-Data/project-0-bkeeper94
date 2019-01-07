package com.project0.modelTests;

import static org.junit.Assert.*;

import java.sql.Timestamp;

import org.junit.Test;

import com.project0.model.*;

public class TransactionTests {
	private static Integer fakeTransactionID = 1;
	private static Integer fakeMemberID = 1000;
	private static Timestamp tm = new Timestamp(2000000000l);
	private static final Transaction tc = new Transaction(fakeTransactionID, "0000000000", "0000000000", fakeMemberID, 
			"deposit", 50000.00, new Timestamp(2000000000l));
	
	@Test
	public void getTransactionIDTest() {
		Integer expected = 1;
		assertEquals(expected, tc.getTransactionID());
	}
	
	@Test
	public void getAccountNumbersTests() {
		assertEquals("0000000000", tc.getSourceAccountNo());
		assertEquals("0000000000", tc.getEndAccountNo());
	}
	
	@Test
	public void getMemberIDTest() {
		Integer expected = 1000;
		assertEquals(expected, tc.getMemberID());
	}
	
	@Test
	public void getTransactionTypeTest() {
		assertEquals("deposit", tc.getTransactionType());
	}
	
	@Test
	public void getTransactionAmountTest() {
		Double amount = 50000.00;
		assertEquals(amount, tc.getTransactionAmount());
	}
	
	@Test
	public void getTransactionTimeTest() {
		assertTrue(tm.equals(tc.getTransactionTime()));
	}
	
	@Test
	public void toStringTest() {
		// Test to make sure the toString method prints each attribute in the correct order
		String expected = "Transaction [transactionID=" + tc.getTransactionID() + ", sourceAccountNo=" + 
		tc.getSourceAccountNo() + ", endAccountNo=" + tc.getEndAccountNo() + ", memberID=" + tc.getMemberID() +
		", transactionType=" + tc.getTransactionType() + ", transactionAmount=" + tc.getTransactionAmount() + 
		", transactionTime=" + tc.getTransactionTime() + "]";
		assertEquals(expected, tc.toString());
	}
	
	@Test
	public void hashCodeTest() {
		int result = 31 + tc.getEndAccountNo().hashCode();
		result = 31*result + tc.getMemberID().hashCode();
		result = 31*result + tc.getSourceAccountNo().hashCode();
		result = 31*result + tc.getTransactionTime().hashCode();
		result = 31*result + tc.getTransactionAmount().hashCode();
		result = 31*result + tc.getTransactionID().hashCode();
		result = 31*result + tc.getTransactionType().hashCode();
		assertEquals(result, tc.hashCode());
	}
	
	@Test
	public void equalsTest() {
		// Test to show that the equals method returns false when comparing two transactions that occur
		// simultaneously but differ in their transaction type
		Transaction tc1 = new Transaction(fakeTransactionID, "0000000000", "0000000000", fakeMemberID, 
				"deposit", 50000.00, new Timestamp(2000000000l));
		Transaction tc2 = new Transaction(fakeTransactionID, "0000000000", "0000000000", fakeMemberID, 
				"withdraw", 50000.00, new Timestamp(2000000000l));
		assertFalse(tc1.equals(tc2));
	}
	
}
