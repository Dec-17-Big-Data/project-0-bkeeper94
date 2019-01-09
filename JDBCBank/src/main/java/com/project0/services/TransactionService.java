package com.project0.services;

import java.util.Scanner;

import com.project0.dao.*;
import com.project0.model.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class TransactionService {
	private static TransactionService transactionService;
	final static TransactionDao transactionDao = TransactionOracle.getDao();

	private TransactionService() {

	}

	public static TransactionService getService() {
		if (transactionService == null) {
			transactionService = new TransactionService();
		}

		return transactionService;
	}
	
	// Performs the collection of each transaction by a user (Unit test this method)
	public List<Transaction> performGetMemberTransactions(BankMember member) {
		try {
			Optional<List<Transaction>> tlOpt = transactionDao.getAMembersTransactions(member);
			if (!tlOpt.isPresent()) {
				throw new NoSuchElementException();
			}
			return tlOpt.get();
		} catch (NoSuchElementException e) {
			return null;
		}
		
		
	}
	
	// Part of the view
	public boolean getAMembersTransactions(BankMember member, Scanner UI) {
		System.out.println("");
		System.out.println("You have selected to view all of your transactions as a member of the Bank of OOPs");
		System.out.println("");
		System.out.println("Here are all of the transactions you have made with us:");
		System.out.println("");
		String tableTitle= "Account Number(s) Involved " + "        " + "Transaction Type" + "      " + 
				"Transaction Amount" + "           " + "Date/Time of Transaction";
		System.out.println(tableTitle);
		System.out.println("----------------------------------------------------------------------------------------------------------------");
		List<Transaction> tl = performGetMemberTransactions(member);
		if (tl == null) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("We were unable to show you your transactions due to an unexpected error.");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return false;
		}
		
		for (Transaction tn : tl) {
			if (tn.getSourceAccountNo().compareTo(tn.getEndAccountNo()) == 0) {
				System.out.println( tn.getSourceAccountNo() + "                           " + tn.getTransactionType() + 
						"             " + "$"+ String.format("%.2f", tn.getTransactionAmount()) + 
						"                  " + tn.getTransactionTime().toString());
			} else {
				System.out.println("from account "+ tn.getSourceAccountNo() +"              " + tn.getTransactionType() +
						"             " + "$"+ String.format("%.2f", tn.getTransactionAmount()) + "                  " + 
						tn.getTransactionTime().toString());
				System.out.println(" to account " + tn.getEndAccountNo());
			}
			System.out.println("");
		}
		System.out.println("");
		System.out.println("");
		System.out.print("Press the enter key to return to your portal when you are finished: ");
		UI.nextLine();
		System.out.println("");
		return true;
	}
	
	// Adds a transaction record to the database after each transfer, withdraw, and deposit
	// (Unit test this method)
	public boolean addNewTransaction(BankMember member, String sourceAccountNo, String endAccountNo,
			Double amount, String transactionType) {
		try {
			Optional<Integer> resultOpt = transactionDao.addNewTransaction(member, sourceAccountNo, 
					endAccountNo, amount, transactionType);
			if (!resultOpt.isPresent()) {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}
}
