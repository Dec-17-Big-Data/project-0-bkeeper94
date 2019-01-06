package com.project0.services;

import com.project0.model.*;
import com.project0.exceptions.*;
import com.project0.dao.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

public class BankAccountService {

	private static BankAccountService bankAccountService;
	final static BankAccountDao bankAccountDao = BankAccountOracle.getDao();

	private BankAccountService() {

	}

	public static BankAccountService getService() {
		if (bankAccountService == null) {
			bankAccountService = new BankAccountService();
		}

		return bankAccountService;
	}

	// Getter for entries in the bankaccounts SQL table (Unit test this method)
	public BankAccount getBankAccount(String accountNumber) {
		try {
			Optional<BankAccount> bankAccountOpt = bankAccountDao.getBankAccount(accountNumber);
			if (bankAccountOpt.isPresent()) {
				return bankAccountOpt.get();
			} else {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	// Checks for valid pin input (Unit test this method)
	public boolean pinInputAuthentication(String pinInput) {
		try {
			if (pinInput.length() != 4) {
				throw new InvalidPinException();
			}
			for (int i = 0; i < pinInput.length(); i++) {
				if (pinInput.substring(i, i + 1).matches("[^0-9]")) {
					throw new InvalidPinException();
				}
			}
			return true;
		} catch (InvalidPinException e) {
			return false;
		}
	}

	// Creates a list of a bank member's accounts (Unit test this method)
	public List<BankAccount> retrieveAMembersBankAccounts(BankMember member) {
		try {
			Optional<List<BankAccount>> baListOpt = bankAccountDao.getAMembersAccounts(member);
			if (!baListOpt.isPresent()) {
				throw new NoSuchElementException();
			}
			return baListOpt.get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	// Part of the view
	public void printAMembersBankAccounts(List<BankAccount> baList, Scanner UI) {
		System.out.println("Here are your current account(s) and their balance(s):");
		System.out.println("");
		System.out.println("Account Number" + "          " + "Account Type" + "          " + "Balance");
		System.out.println("----------------------------------------------------------------------");
		for (BankAccount ba : baList) {
			System.out.println(ba.getAccountNo() + "          " + ba.getAccountType() + "          " + "$"
					+ String.format("%.2f", ba.getBalance()));
			System.out.println("");
		}
		System.out.println("");
		System.out.println("");
		System.out.print("Press the enter key to continue: ");
		UI.nextLine();
		System.out.println("");
	}
	
	// Checks that the account type the user puts in is one of the two accepted words (Unit test this method)
	public boolean accountTypeChecker(String accountTypeInput) {
		try {
			switch (accountTypeInput) {
			case "checking":
			case "savings":
				return true;
			default:
				throw new InvalidAccountTypeException();
			}
		} catch (InvalidAccountTypeException e) {
			return false;
		}
	}
	
	// Checks that the user typed in a properly formatted dollar amount (Unit test
	// this method)
	public boolean amountInputChecker(String value) {
		try {
			if (value.length() == 0) {
				throw new InvalidAmountException();
			}
			for (int i = 0; i < value.length(); i++) {
				if (value.substring(i, i + 1).matches("[^0-9,$.]")) {
					throw new InvalidAmountException();
				}
			}
		} catch (InvalidAmountException e) {
			return false;
		}
		return true;
	}

	// Changes the amount input by the user from a string to a double (Unit Test
	// this method)
	public Double amountInputAsNumber(String amount) {
		amount = amount.replaceAll("[$,]", "");
		return Math.round(100.0 * Double.valueOf(amount)) / 100.0;
	}

	// Opens a new account (Unit test this method)
	public boolean addAccount(BankMember member, String accountType, Double valueAsNum) {
		try {
			Optional<Integer> resultOpt = bankAccountDao.openNewBankAccount(member, accountType, valueAsNum);
			if (!resultOpt.isPresent() || resultOpt.get() == -1) {
				throw new NoSuchElementException();
			}
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	// Part of the view
	public boolean openNewAccount(BankMember member, Scanner UI) {
		System.out.println("You will now open an account with the Bank of OOPs");
		System.out.println("");
		System.out.println("First, specify the type of account you wish to open");
		String accountTypeInput = "";
		while (true) {
			System.out.println("We offer checking accounts and savings accounts");
			System.out.print("Type in either 'checking' or 'savings' and then press the enter key: ");
			accountTypeInput = UI.nextLine();
			if (!accountTypeChecker(accountTypeInput)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("Invalid account type");
				System.out.println("");
				System.out.println("Acceptable account types are checking and savings");
				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				continue;
			}
			break;
		}
		System.out.println("");
		System.out.println("Account type is valid");
		System.out.println("");
		String value = "";
		while (true) {
			System.out.print("Type in the amount you wish to place into the account and then press enter: ");
			value = UI.nextLine();
			if (!amountInputChecker(value)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("The value you entered is invalid");
				System.out.println("You must type in a dollar amount ($ and , are valid symbols)");
				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			break;
		}
		System.out.println("");
		System.out.println("Amount entered is valid");
		System.out.println("Creating account...");
		System.out.println("");
		Double valueAsNum = amountInputAsNumber(value);
		if (!addAccount(member, accountTypeInput, valueAsNum)) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("We were unable to open a new account due to an unexpected error.");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return false;
		}
		System.out.println("Account creation successful");
		System.out.println("");
		System.out.print("Returning you to your portal. Press the enter key to continue: ");
		UI.nextLine();
		return true;
	}

	// Checks that a user typed in an account number containing valid characters
	// (Unit test this method)
	public boolean checkForValidAcctNo(String accountNumber) {
		try {
			if (accountNumber.length() != 10) {
				throw new InvalidAccountNumberException();
			}
			for (int i = 0; i < accountNumber.length(); i++) {
				if (accountNumber.substring(i, i + 1).matches("[^0-9]")) {
					throw new InvalidAccountNumberException();
				}
			}
		} catch (InvalidAccountNumberException e) {
			return false;
		}
		return true;
	}

	// Account number mismatch checker (Unit test this method)
	public boolean accountNumberMismatchCheck(String accountNumber) {
		try {
			if (getBankAccount(accountNumber) == null) {
				throw new AccountNumberMismatchException();
			}
		} catch (AccountNumberMismatchException e) {
			return false;
		}
		return true;
	}

	// Empty account balance checker (Unit test this method)
	public boolean accountBalanceIsEmpty(String accountNumber) {
		if (getBankAccount(accountNumber) != null) {
			if (getBankAccount(accountNumber).getBalance() == 0) {
				return true;
			}
		}
		return false;
	}

	// Closes an empty bank account (Unit test this method)
	public boolean closeAccount(BankMember member, String accountNumber) {
		try {
			Optional<Integer> resultOpt = bankAccountDao.closeOldBankAccount(member, accountNumber);
			if (!resultOpt.isPresent()) {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}

	// Part of the view
	public boolean closeEmptyBankAccount(BankMember member, Scanner UI) {
		List<BankAccount> baList = retrieveAMembersBankAccounts(member);
		while (true) {
			System.out.println("You will now close one of your accounts with the Bank of OOPs");
			System.out.println("");
			printAMembersBankAccounts(baList, UI);
			System.out.print("Enter the number of the account you would like to close and then press the enter key: ");
			String accountNumber = UI.nextLine();
			System.out.println("");
			System.out.println("Checking our records for the account number you entered...");
			System.out.println("");
			if (accountNumber.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (!checkForValidAcctNo(accountNumber)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("The credentials you entered contained invalid characters");
				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (!accountNumberMismatchCheck(accountNumber)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("The account number entered does not match our records");
				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (!accountBalanceIsEmpty(accountNumber)) {
				System.out.println("Our records show that there is a still a balance on this account");
				System.out.println("Please transfer the funds to another account or ");
				System.out.println("withdraw the funds before closing this account");
				System.out.println("");
				System.out.print("Returning you to your portal. Press the enter key to continue: ");
				UI.nextLine();
				return false;
			}
			System.out.println("The account numbered you entered is valid and the balance on the account is $0");
			System.out.println("");
			System.out.println("Removing account...");
			System.out.println("");
			if (!closeAccount(member, accountNumber)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("This account could not be closed due to an unexpected error.");
				System.out.print("Press the enter key to exit: ");
				UI.nextLine();
				return false;
			}
			System.out.println("Account closed successfully");
			System.out.println("");
			System.out.print("Returning you to your portal. Press the enter key to continue: ");
			UI.nextLine();
			return true;
		}
	}

	// Check for pin number mismatch (Unit test this method)
	public boolean pinMismatchCheck(BankMember member, String pinNumber) {
		try {
			if (pinNumber.compareTo(member.getPinNumber()) != 0) {
				throw new PinMismatchException();
			}
		} catch (PinMismatchException e) {
			return false;
		}
		return true;
	}

	// Part of the view
	public void pinNumberAuthentication(BankMember member, Scanner UI) {
		while (true) {
			System.out.print("Please type in your 4-digit pin and then press the enter key: ");
			String pinInput = UI.nextLine();
			if (pinInput.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (!pinInputAuthentication(pinInput)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("The pin you entered is invalid");
				System.out.println("Pins must be four-digit numbers");
				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (!pinMismatchCheck(member, pinInput)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("The pin you entered does not match our records");
				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			System.out.println("");
			System.out.println("Pin authentication successful");
			System.out.println("");
			break;
		}
	}

	// Part of the view
	public String acctNoAuthenticationDeposit(BankMember member, Scanner UI) {
		List<BankAccount> baList = retrieveAMembersBankAccounts(member);
		while (true) {
			printAMembersBankAccounts(baList, UI);
			System.out
					.print("Provide the account number you wish to deposit funds into and then press the enter key: ");
			String accountNumber = UI.nextLine();
			if (accountNumber.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (!checkForValidAcctNo(accountNumber)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("The credentials you entered contained invalid characters");
				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (!accountNumberMismatchCheck(accountNumber)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("The account number you entered does not match our records");
				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			System.out.println("");
			System.out.println("The account number you entered is valid");
			System.out.println("");
			return accountNumber;
		}
	}

	// Part of the view
	public String amountEntry(Scanner UI) {
		while (true) {
			System.out.print("Type in the amount you wish to deposit, withdraw, or transfer and then press enter: ");
			String amount = UI.nextLine();
			if (amount.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (!amountInputChecker(amount)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("The value you entered is invalid");
				System.out.println("You must type in a dollar amount ('$', ',', and '.' are valid symbols)");
				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			return amount;
		}
	}

	// Performs deposit of funds (Unit test this method)
	public boolean performDeposit(String accountNumber, Double depositAmount) {
		try {
			Optional<Integer> resultOpt = bankAccountDao.depositFunds(accountNumber, depositAmount);
			if (!resultOpt.isPresent() || resultOpt.get() == -1) {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}

	// Part of the view
	public boolean deposit(TransactionService ts, BankMember member, Scanner UI) {
		System.out.println("");
		System.out.println("You have selected to deposit funds into an account.");
		System.out.println("");
		pinNumberAuthentication(member, UI);
		String accountNumber = acctNoAuthenticationDeposit(member, UI);
		String depositAmount = amountEntry(UI);
		System.out.println("");
		System.out.println("Depositing funds...");
		System.out.println("");
		Double depositAsNum = amountInputAsNumber(depositAmount);
		if (!ts.addNewTransaction(member, accountNumber, accountNumber, depositAsNum, "deposit")) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("This transaction could not be completed due to an unexpected error");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return false;
		}
		if (!performDeposit(accountNumber, depositAsNum)) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("Deposit failed due to an unexpected error.");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return false;
		}
		System.out.println("Deposit Successful");
		System.out.println("");
		System.out.print("Returning you to your portal. Press the enter key to continue: ");
		UI.nextLine();
		return true;
	}

	// Part of the view
	public String acctNoAuthenticationWithdraw(BankMember member, Scanner UI) {
		List<BankAccount> baList = retrieveAMembersBankAccounts(member);
		while (true) {
			printAMembersBankAccounts(baList, UI);
			System.out
					.print("Provide the account number you wish to withdraw funds from and then press the enter key: ");
			String accountNumber = UI.nextLine();
			if (accountNumber.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (!checkForValidAcctNo(accountNumber)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("The credentials you entered contained invalid characters");
				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (!accountNumberMismatchCheck(accountNumber)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("The account number you entered does not match our records");
				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			System.out.println("");
			System.out.println("The account number you entered is valid");
			System.out.println("");
			return accountNumber;
		}
	}

	// Performs the withdraw action and checks for overdraw (Unit test this method)
	public int performWithdraw(String accountNumber, Double withdrawAmount) {
		try {
			Optional<Integer> resultOpt = bankAccountDao.withdrawFunds(accountNumber, withdrawAmount);
			if (!resultOpt.isPresent() || resultOpt.get() == -2) {
				throw new NoSuchElementException();
			} else if (resultOpt.get() == -1) {
				throw new OverDrawException();
			}
		} catch (OverDrawException e) {
			return 0;
		} catch (NoSuchElementException e) {
			return -1;
		}
		return 1;
	}

	// Part of the view
	public boolean withdraw(TransactionService ts, BankMember member, Scanner UI) {
		System.out.println("");
		System.out.println("You have selected to withdraw funds from an account.");
		System.out.println("");
		pinNumberAuthentication(member, UI);
		String accountNumber = acctNoAuthenticationWithdraw(member, UI);
		String withdrawAmount = amountEntry(UI);
		System.out.println("");
		System.out.println("Attempting to withdraw funds...");
		System.out.println("");
		Double withdrawAsNum = amountInputAsNumber(withdrawAmount);
		if (!ts.addNewTransaction(member, accountNumber, accountNumber, withdrawAsNum, "withdraw")) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("This transaction could not be completed due to an unexpected error");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return false;
		}
		if (performWithdraw(accountNumber, withdrawAsNum) == 0) {
			System.out.println("Insufficient funds");
			System.out.println("");
			System.out.println("Withdraw Denied");
			System.out.println("");
			System.out.print("Returning you to your portal. Press the enter key to continue: ");
			UI.nextLine();
			return false;
		} else if (performWithdraw(accountNumber, withdrawAsNum) == -1) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("Withdraw failed due to an unexpected error.");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return false;
		}
		System.out.println("Withdraw Successful");
		System.out.println("");
		System.out.print("Returning you to your portal. Press the enter key to continue: ");
		UI.nextLine();
		return true;
	}

	// Perform the transfer action and checks for overdraw (Unit test this method)
	public int performTransfer(String sourceAccountNumber, String endAccountNumber, Double transferAmount) {
		try {
			Optional<Integer> resultOpt = bankAccountDao.transferFunds(sourceAccountNumber, endAccountNumber,
					transferAmount);
			if (!resultOpt.isPresent() || resultOpt.get() == -2) {
				throw new NoSuchElementException();
			} else if (resultOpt.get() == -1) {
				throw new OverDrawException();
			}
		} catch (OverDrawException e) {
			return 0;
		} catch (NoSuchElementException e) {
			return -1;
		}
		return 1;
	}

	// Part of the view
	public boolean transferFunds(TransactionService ts, BankMember member, Scanner UI) {
		System.out.println("");
		System.out.println("You have selected to transfer funds from one of your accounts to another ");
		System.out.println("");
		pinNumberAuthentication(member, UI);
		String sourceAccountNumber = acctNoAuthenticationWithdraw(member, UI);
		String endAccountNumber = acctNoAuthenticationDeposit(member, UI);
		String transferAmount = amountEntry(UI);
		System.out.println("");
		System.out.println("Attempting to transfer funds...");
		System.out.println("");
		Double transferAsNum = amountInputAsNumber(transferAmount);
		if (!ts.addNewTransaction(member, endAccountNumber, sourceAccountNumber, transferAsNum, "transfer")) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("This transaction could not be completed due to an unexpected error");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return false;
		}
		if (performTransfer(sourceAccountNumber, endAccountNumber, transferAsNum) == 0) {
			System.out.println("Insufficient funds");
			System.out.println("");
			System.out.println("Transfer Denied");
			System.out.println("");
			System.out.print("Returning you to your portal. Press the enter key to continue: ");
			UI.nextLine();
			return false;
		} else if (performTransfer(sourceAccountNumber, endAccountNumber, transferAsNum) == -1) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("Transfer failed due to an unexpected error.");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return false;
		}
		System.out.println("Transfer Successful");
		System.out.println("");
		System.out.print("Returning you to your portal. Press the enter key to continue: ");
		UI.nextLine();
		return true;
	}
}
