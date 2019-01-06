package com.project0.serviceTests;

import com.project0.model.*;
import com.project0.exceptions.*;
import com.project0.services.*;

import static org.junit.Assert.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING) // Executes unit tests in ABC order

// Important note: Do not run these JUnit tests individually
public class BankAccountServiceTests {

	private static BankAccountService bas = BankAccountService.getService();

	// Important note: Do not remove the test user's record from the database
	private static BankMember testUser = new BankMember(1, "testuser", "joe", "blow", "password", "7890");

	// Makes sure testUser has no accounts in the system prior to starting the unit tests
	@BeforeClass
	public static void removeTestUserAccountsBefore() {
		List<BankAccount> ba = bas.retrieveAMembersBankAccounts(testUser);
		if (!ba.isEmpty()) {
			for (BankAccount b : ba) {
				bas.closeAccount(testUser, b.getAccountNo());
			}
		}
	}
	
	@Test
	public void accountTypeCheckerTest() throws InvalidAccountTypeException {
		// Test that the strings 'checking' and 'savings' both pass
		assertTrue(bas.accountTypeChecker("checking"));
		assertTrue(bas.accountTypeChecker("savings"));
		
		// Test the empty string
		assertFalse(bas.accountTypeChecker(""));
		
		// Test a word that is neither 'checking' nor 'savings'
		assertFalse(bas.accountTypeChecker("money market"));
	}
	
	@Test
	public void addAccountTest() throws NoSuchElementException {
		// Test opening a new bank account for a user that is not in the SQL database
		BankMember userNotInSystem = new BankMember(-1, "john", "doe", "jdoe123", "password", "1234");
		String savingsAcct = "savings";
		Double amount1 = 10000.00;
		assertFalse(bas.addAccount(userNotInSystem, savingsAcct, amount1));

		// Test opening a new bank account for the test user
		String checkingAcct = "checking";
		Double amount2 = 10500.00;
		assertTrue(bas.addAccount(testUser, checkingAcct, amount2));
	}

	@Test
	public void amountInputAsNumberTest() {
		// Test proper dollar amount as input
		String properDollar = "$100,000.00";
		Double expected1 = 100000.00;
		assertEquals(expected1, bas.amountInputAsNumber(properDollar));

		// Test amount input that has commas but does not have a decimal point
		String numberWithCommas = "100,000,000";
		Double expected2 = 100000000.00;
		assertEquals(expected2, bas.amountInputAsNumber(numberWithCommas));

		// Test amount input with dollar sign but no commas and no decimal point
		String dollarAmountNoDecimal = "$10000";
		Double expected3 = 10000.00;
		assertEquals(expected3, bas.amountInputAsNumber(dollarAmountNoDecimal));
	}

	@Test
	public void amountInputCheckerTest() throws InvalidAmountException {
		// Test if the user accidentally pressed enter before inputting a desired amount
		assertFalse(bas.amountInputChecker(""));

		// Test proper dollar amount as input
		String properDollar = "$100,000.00";
		assertTrue(bas.amountInputChecker(properDollar));

		// Test a whole number as input
		String wholeNumber = "1000000";
		assertTrue(bas.amountInputChecker(wholeNumber));

		// Test string representation of a double with numbers after the decimal place
		String badDouble = "100000.0000";
		assertTrue(bas.amountInputChecker(badDouble));

		// Test input that has letters and other forbidden characters
		String badInput = "$100,ab0.0c0";
		assertFalse(bas.amountInputChecker(badInput));

		// Test to make sure negative numbers are rejected
		String negativeNumber = "-$100,000.00";
		assertFalse(bas.amountInputChecker(negativeNumber));
	}

	@Test
	public void balanceEmptyTest() {
		// Add a new account to test user
		String savingsAcct = "savings";
		Double amount = 0.00;
		bas.addAccount(testUser, savingsAcct, amount);

		// Test this new account to make sure the method returns the balance as empty
		List<BankAccount> ba = bas.retrieveAMembersBankAccounts(testUser); // has size 2
		assertTrue(bas.accountBalanceIsEmpty(ba.get(1).getAccountNo()));

		// Test the test user's checking account to make sure the method returns false
		assertFalse(bas.accountBalanceIsEmpty(ba.get(0).getAccountNo()));
	}

	@Test
	public void checkAccountNumberMismatchTest() throws AccountNumberMismatchException {
		// Test retrieving an account number that is not in the system
		String acctNoNotInSystem = "1234567890";
		assertFalse(bas.accountNumberMismatchCheck(acctNoNotInSystem));

		// Test retrieving an account number that is in the system

		List<BankAccount> ba = bas.retrieveAMembersBankAccounts(testUser);
		String checkingAccountOfTestUser = ba.get(0).getAccountNo();
		assertTrue(bas.accountNumberMismatchCheck(checkingAccountOfTestUser));
	}

	@Test
	public void checkForValidAcctNoTest() throws InvalidAccountNumberException {
		// Test a valid 10 digit account number
		String goodAcctNo = "1234567890";
		assertTrue(bas.checkForValidAcctNo(goodAcctNo));

		// Test account numbers of incorrect length
		String badAcctNoZero = "";
		String badAcctNoEleven = "12345678901";
		String badAcctNoFive = "12345";
		assertFalse(bas.checkForValidAcctNo(badAcctNoZero));
		assertFalse(bas.checkForValidAcctNo(badAcctNoEleven));
		assertFalse(bas.checkForValidAcctNo(badAcctNoFive));

		// Test account numbers with illegal characters
		String badAcctNoLetters = "abc1234567";
		String badAcctNoSymbols = "!@#$%^&789";
		assertFalse(bas.checkForValidAcctNo(badAcctNoLetters));
		assertFalse(bas.checkForValidAcctNo(badAcctNoSymbols));

	}

	@Test
	public void closeAccountTest() throws NoSuchElementException {
		// Test closing the test user account with 0 balance
		List<BankAccount> ba = bas.retrieveAMembersBankAccounts(testUser);
		String savingsAccountOfTestUser = ba.get(1).getAccountNo();
		assertTrue(bas.closeAccount(testUser, ba.get(1).getAccountNo()));

		// Test attempting to close the same account again
		assertFalse(bas.closeAccount(testUser, savingsAccountOfTestUser));
	}

	@Test
	public void getBankAccountTest() throws NoSuchElementException {
		bas.addAccount(testUser, "savings", 10000.00);
		List<BankAccount> ba = bas.retrieveAMembersBankAccounts(testUser);

		// Test this getter on the account number generated by opening a new savings
		// account
		String accountNoToTest = ba.get(ba.size() - 1).getAccountNo();
		assertTrue(bas.getBankAccount(accountNoToTest).equals(ba.get(1)));
		
		bas.closeAccount(testUser, accountNoToTest);

		// Test this getter on an account number that is not in the system
		assertTrue(bas.getBankAccount("0000000000") == null);
	}

	@Test
	public void pinInputAuthenticationTest() throws InvalidPinException {
		// Test a pin number of length 0
		String pinZero = "";
		assertFalse(bas.pinInputAuthentication(pinZero));

		// Test a pin number of length 5
		String pinFive = "03109";
		assertFalse(bas.pinInputAuthentication(pinFive));

		// Test a pin number of correct length, but contains letters
		String pinBad = "ab03";
		assertFalse(bas.pinInputAuthentication(pinBad));

		// Test a valid pin number
		String pinGood = "1234";
		assertTrue(bas.pinInputAuthentication(pinGood));
	}

	@Test
	public void pinMismatchCheckTest() throws PinMismatchException {
		// Test with the test user's pin number
		String testPin = testUser.getPinNumber();
		assertTrue(bas.pinMismatchCheck(testUser, testPin));

		// Test a pin number that is not the same as testUser's pin
		String pinNotInSystem = "0000";
		assertFalse(bas.pinMismatchCheck(testUser, pinNotInSystem));
	}

	@Test
	public void performDepositTest() throws NoSuchElementException {
		// Test a deposit of funds into testUser's checking account
		List<BankAccount> ba = bas.retrieveAMembersBankAccounts(testUser);
		String checkingAccountNo = ba.get(0).getAccountNo();
		Double depositAmount = 1000.00;
		assertTrue(bas.performDeposit(checkingAccountNo, depositAmount));

		// Attempt to deposit funds into an account that is not in the system
		String accountNotInTable = "1234567890";
		assertFalse(bas.performDeposit(accountNotInTable, depositAmount));
	}

	@Test
	public void performTransferTest() throws OverDrawException, NoSuchElementException {
		bas.addAccount(testUser, "savings", 10000.00);
		List<BankAccount> ba = bas.retrieveAMembersBankAccounts(testUser);

		// Test a valid transfer between a savings account and a checking account
		int expected1 = 1; // Successful transfer
		assertEquals(expected1, bas.performTransfer(ba.get(1).getAccountNo(), ba.get(0).getAccountNo(), 1000.00));

		// Test an invalid transfer due to overdraw from the source account
		int expected2 = 0; // When the transfer does not happen due to overdraw from source account
		assertEquals(expected2, bas.performTransfer(ba.get(1).getAccountNo(), ba.get(0).getAccountNo(), 50000.00));

		// Test a transfer to an account number that does not belong to testUser
		int expected3 = -1; // When the transfer does not happen for reasons besides overdraw
		assertEquals(expected3, bas.performTransfer(ba.get(1).getAccountNo(), "0000000000", 100.00));
	}

	@Test
	public void performWithdrawTest() throws OverDrawException, NoSuchElementException {
		List<BankAccount> ba = bas.retrieveAMembersBankAccounts(testUser);

		// Test a valid withdraw from a savings account
		int expected1 = 1; // Successful withdraw
		assertEquals(expected1, bas.performWithdraw(ba.get(1).getAccountNo(), 1000.00));

		// Test an invalid withdraw due to overdraw
		int expected2 = 0; // When the withdraw does not happen due to overdraw
		assertEquals(expected2, bas.performWithdraw(ba.get(0).getAccountNo(), 50000.00));

		// Test a withdraw on an account number that does not belong to testUser
		int expected3 = -1; // When the transfer does not happen for reasons besides overdraw
		assertEquals(expected3, bas.performWithdraw("0000000000", 100.00));
	}

	@AfterClass
	public static void removeTestUsersAccountsAfter() {
		List<BankAccount> ba = bas.retrieveAMembersBankAccounts(testUser);
		if (!ba.isEmpty()) {
			for (BankAccount b : ba) {
				bas.closeAccount(testUser, b.getAccountNo());
			}
		}
	}
}
