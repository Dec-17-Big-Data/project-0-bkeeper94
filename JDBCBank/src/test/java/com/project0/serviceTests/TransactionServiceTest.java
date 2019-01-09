package com.project0.serviceTests;

import com.project0.model.*;
import com.project0.services.*;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;

public class TransactionServiceTest {
	
	private static TransactionService ts = TransactionService.getService();
	
	// Important note: Do not remove the test user's record from the database and do not add any accounts to the test user
	private static BankMember testUser = new BankMember(1, "testuser", "joe", 
				"blow", "password", "7890");
	
	private static BankAccountService bas = BankAccountService.getService();  
	
	@Test
	public void addNewTransactionTest() throws NoSuchElementException {
		// Test a withdrawal with transaction logging and see if the withdraw amount
		// matches the value in the transaction entry 
		bas.addAccount(testUser, "checking", 100000.00);
		List<BankAccount> ba = bas.retrieveAMembersBankAccounts(testUser);
		Double withdrawAmount = 3000.00;
		bas.performWithdraw(ba.get(0).getAccountNo(), withdrawAmount);
		ts.addNewTransaction(testUser, ba.get(0).getAccountNo(),
				ba.get(0).getAccountNo(), 3000.00, "withdraw");
		List<Transaction> tr = ts.performGetMemberTransactions(testUser);
		assertEquals(withdrawAmount, tr.get(0).getTransactionAmount());
	}

}
