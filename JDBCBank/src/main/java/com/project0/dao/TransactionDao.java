package com.project0.dao;

import java.util.List;
import java.util.Optional;

import com.project0.model.*;

public interface TransactionDao {
	
	Optional<List<Transaction>> getAMembersTransactions(BankMember member);
	
	Optional<Integer> addNewTransaction(BankMember member, String sourceAccountNo, String endAccountNo, 
			Double amount, String transactionType);
	
}
