package com.project0.services;

import com.project0.model.*;
import com.project0.exceptions.*;
import com.project0.dao.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

public class BankService {

	private static BankService bankService;
	final static BankDao bankDao = BankOracle.getDao();

	private BankService() {

	}

	public static BankService getService() {
		if (bankService == null) {
			bankService = new BankService();
		}

		return bankService;
	}

	// Wrap in try catch for NoSuchElementException
	public Optional<BankMember> getBankMember(String userName) {
		return bankDao.getBankMember(userName);
	}

	// Wrap in try catch for NoSuchElementException
	public Optional<BankAccount> getBankAccount(BankMember member, String accountNumber) {
		return bankDao.getBankAccount(accountNumber);
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

	// Adds the user but first checks if the input user name is already registered
	// to another user (Unit test this method)
	public int addNewUser(String firstName, String lastName, String userNameInput, String passWordInput,
			String pinInput) {
		try {
			Optional<Integer> result = bankDao.addNewUser(firstName, lastName, userNameInput, passWordInput, pinInput);
			if (result.get() == -1) {
				throw new DuplicateUserNameException();
			} else if (!result.isPresent()) {
				throw new NoSuchElementException();
			}
			return 1;
		} catch (DuplicateUserNameException e) {
			return 0;
		} catch (NoSuchElementException e) {
			return -1;
		}
	}

	// Part of the view
	public String portalCreation(Scanner UI) {
		while (true) {
			System.out.print("Portal Creation Page");
			System.out.println("");
			System.out.println("");
			System.out.print("Type in your first name and then press the enter key: ");
			String firstName = UI.nextLine();
			if (firstName.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			System.out.print("Type in your last name and then press the enter key: ");
			String lastName = UI.nextLine();
			if (lastName.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			System.out.println("Now you will create the user name and password you will use to access your portal");
			System.out.println("The user name you enter will be checked against our records to ensure uniqueness");
			System.out.print("Type in your desired user name and then press the enter key: ");
			String userNameInput = UI.nextLine();
			if (userNameInput.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			System.out.println("");
			System.out.print("Type in your desired password and then press the enter key: ");
			String passWordInput = UI.nextLine();
			if (passWordInput.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			System.out.println("");
			System.out.print("To complete creating your profile, type in a "
					+ "four-digit pin number and then press the enter key: ");
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
			System.out.println("");
			System.out.println("Attempting account creation...");
			Integer addUserResult = addNewUser(firstName, lastName, userNameInput, passWordInput, pinInput);
			if (addUserResult == 1) {
				System.out.println("");
				System.out.println("Your account was created successfully");
				System.out.print("Press enter to continue to your portal: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				return userNameInput;
			} else if (addUserResult == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("User portal creation failed");
				System.out.println("User name already found in the system");
				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
			} else if (addUserResult == -1) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("User portal creation failed");
				System.out.println("due to a database malfunction or a connection malfunction");
				System.out.print("Press the enter key to exit: ");
				UI.nextLine();
				return "no portal";
			}
		}
	}

	// Creates a list of a bank member's accounts (Unit test this method)
	public List<BankAccount> retrieveAMembersBankAccounts(BankMember member) {
		try {
			Optional<List<BankAccount>> baListOpt = bankDao.getAMembersAccounts(member);
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
		System.out.println("---------------------------------------");
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

	// Checks that the user typed in a properly formatted dollar amount (Unit test
	// this method)
	public boolean amountInputChecker(String value) {
		try {
			for (int i = 0; i < value.length(); i++) {
				if (value.substring(i, i + 1).matches("[^0-9,$]")) {
					throw new NotANumberException();
				}
			}
		} catch (NotANumberException e) {
			return false;
		}
		return true;
	}

	// Opens a new account (Unit test this method)
	public boolean addAccount(BankMember member, String accountType, String value) {
		Double valueAsNum = Math.round(100.0 * Double.valueOf(value)) / 100.0;
		try {

			Optional<Integer> resultOpt = bankDao.openNewBankAccount(member, accountType, valueAsNum);
			if (!resultOpt.isPresent()) {
				throw new NoSuchElementException();
			}
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	// Part of the view
	public boolean openNewAccount(BankMember member, Scanner UI) {
		while (true) {
			System.out.println("You will now open an account with the Bank of Evil");
			System.out.println("First, specify the type of account you wish to open");
			System.out.println("We offer checking accounts and savings accounts");
			System.out.print("Type in either 'checking' or 'savings' and then press the enter key: ");
			String accountType = UI.nextLine();
			if (accountType.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (accountType.compareToIgnoreCase("checking") != 0 || accountType.compareToIgnoreCase("savings") != 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			System.out.println("");
			System.out.println("Account type is valid");
			System.out.println("");
			System.out.print("Type in the amount you wish to place into the account and then press enter: ");
			String value = UI.nextLine();
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
			System.out.println("");
			System.out.println("Amount entered is valid");
			System.out.println("Creating account...");
			System.out.println("");
			if (!addAccount(member, accountType, value)) {
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
			if (!bankDao.getBankAccount(accountNumber).isPresent()) {
				throw new AccountNumberMismatchException();
			}
		} catch (AccountNumberMismatchException e) {
			return false;
		}
		return true;
	}

	// Empty account balance checker (Unit test this method)
	public boolean accountBalanceIsEmpty(String accountNumber) {
		if (bankDao.getBankAccount(accountNumber).get().getBalance() == 0) {
			return true;
		}
		return false;
	}

	// Closes an empty bank account (Unit test this method)
	public boolean closeAccount(BankMember member, String accountNumber) {
		try {
			Optional<Integer> resultOpt = bankDao.closeOldBankAccount(member, accountNumber);
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
		while (true) {
			System.out.println("You will now close one of your accounts with the Bank of Evil");
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

	// Performs removal of inactive bank member (Unit test this method)
	public boolean performMemberRemoval(BankMember member) {
		try {
			Optional<Integer> resultOpt = bankDao.removeInactiveBankMember(member);
			if (!resultOpt.isPresent()) {
				throw new NoSuchElementException();
			} else if (resultOpt.get() == 0) {
				return false;
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}

	// Part of the view
	public boolean removeInactiveMember(BankMember member, Scanner UI) {
		while (true) {
			System.out.println("");
			System.out.println("You have decided to remove your Bank of Evil portal from the system");
			System.out.println("Are you sure you would like to remove your profile?");
			System.out.print("Confirm by typing in 'yes' and then press the enter key: ");
			String input = UI.nextLine();
			if (input.compareToIgnoreCase("yes") != 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			System.out.println("");
			System.out.println("We are sad to see you go");
			System.out.println("");
			System.out.println("Removing profile...");
			System.out.println("");
			if (performMemberRemoval(member)) {
				System.out.println("The user profile could not be removed due to an unexpected error.");
				System.out.print("Press the enter key to return to your portal: ");
				return false;
			}
			System.out.println("Profile removed successfully");
			System.out.println("");
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
				System.out.println("You must type in a dollar amount ($ and , are valid symbols)");
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
	public boolean performDeposit(String accountNumber, String depositAmount) {
		Double depositAsNum = Math.round(100.0 * Double.valueOf(depositAmount)) / 100.0;
		try {
			Optional<Integer> resultOpt = bankDao.depositFunds(accountNumber, depositAsNum);
			if (!resultOpt.isPresent()) {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}

	// Part of the view
	public boolean deposit(BankMember member, Scanner UI) {
		System.out.println("");
		System.out.println("You have selected to deposit funds into an account.");
		System.out.println("");
		pinNumberAuthentication(member, UI);
		String accountNumber = acctNoAuthenticationDeposit(member, UI);
		String depositAmount = amountEntry(UI);
		System.out.println("");
		System.out.println("Depositing funds...");
		System.out.println("");
		if (!performDeposit(accountNumber, depositAmount)) {
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
	public int performWithdraw(String accountNumber, String withdrawAmount) {
		Double withdrawAsNum = Math.round(100.0 * Double.valueOf(withdrawAmount)) / 100.0;
		try {
			Optional<Integer> resultOpt = bankDao.withdrawFunds(accountNumber, withdrawAsNum);
			if (!resultOpt.isPresent()) {
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
	public boolean withdraw(BankMember member, Scanner UI) {
		System.out.println("");
		System.out.println("You have selected to withdraw funds from an account.");
		System.out.println("");
		pinNumberAuthentication(member, UI);
		String accountNumber = acctNoAuthenticationWithdraw(member, UI);
		String withdrawAmount = amountEntry(UI);
		System.out.println("");
		System.out.println("Attempting to withdraw funds...");
		System.out.println("");
		if (performWithdraw(accountNumber, withdrawAmount) == 0) {
			System.out.println("Insufficient funds");
			System.out.println("");
			System.out.println("Withdraw Denied");
			System.out.println("");
			System.out.print("Returning you to your portal. Press the enter key to continue: ");
			UI.nextLine();
			return false;
		} else if (performWithdraw(accountNumber, withdrawAmount) == -1) {
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
	public int performTransfer(String sourceAccountNumber, String endAccountNumber, String transferAmount) {
		Double transferAsNum = Math.round(100.0 * Double.valueOf(transferAmount)) / 100.0;
		try {
			Optional<Integer> resultOpt = bankDao.transferFunds(sourceAccountNumber, endAccountNumber, transferAsNum);
			if (!resultOpt.isPresent()) {
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
	public boolean transferFunds(BankMember member, Scanner UI) {
		System.out.println("");
		System.out.println("You have selected to transfer funds from one of your accounts to another ");
		System.out.println("");
		pinNumberAuthentication(member, UI);
		String sourceAccountNumber = acctNoAuthenticationDeposit(member, UI);
		String endAccountNumber = acctNoAuthenticationWithdraw(member, UI);
		String transferAmount = amountEntry(UI);
		System.out.println("");
		System.out.println("Attempting to transfer funds...");
		System.out.println("");
		if (performTransfer(sourceAccountNumber, endAccountNumber, transferAmount) == 0) {
			System.out.println("Insufficient funds");
			System.out.println("");
			System.out.println("Transfer Denied");
			System.out.println("");
			System.out.print("Returning you to your portal. Press the enter key to continue: ");
			UI.nextLine();
			return false;
		} else if (performTransfer(sourceAccountNumber, endAccountNumber, transferAmount) == -1) {
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
	
	//Performs the user name update (Unit test this method)
	public boolean performUsernameUpdate(BankMember member, String newPassword) {
		try {
			Optional<Integer> resultOpt = bankDao.updateUserName(member, newPassword);
			if (!resultOpt.isPresent()) {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}
	
	public boolean updateUserName(BankMember member, Scanner UI) {
		// put user and/or admin interaction here
		return true;
	}
	
	//Performs the password update (Unit test this method)
	public boolean performPasswordUpdate(BankMember member, String newPassword) {
		try {
			Optional<Integer> resultOpt = bankDao.updatePassWord(member, newPassword);
			if (!resultOpt.isPresent()) {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}
	
	//Part of the view
	public boolean updatePassWord(BankMember member, Scanner UI) {
		System.out.println("");
		System.out.println("You have selected to update a password");
		System.out.println("");
		System.out.print("Type in a new password and then press the enter key: ");
		String newPassword = UI.nextLine();
		System.out.println("");
		System.out.println("Changing the password...");
		System.out.println("");
		if (!performPasswordUpdate(member, newPassword)) {
			System.out.println("This password could not be updated due to an unexpected error");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return false;
		}
		System.out.println("");
		System.out.print("Returning you to your portal. Press the enter key to continue: ");
		UI.nextLine();
		return true;
	}

	// Performs the pin number update (Unit test this method)
	public boolean performPinUpdate(BankMember member, String newPinNumber) {
		try {
			Optional<Integer> resultOpt = bankDao.updatePinNumber(member, newPinNumber);
			if (resultOpt.isPresent()) {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}
	
	//Part of the view
	public boolean updatePinNumber(BankMember member, Scanner UI) {
		System.out.println("");
		System.out.println("You have selected to update your pin number ");
		System.out.println("");
		while (true) {
			System.out.print("Type in your old pin number and then press the enter key: ");
			String oldPinNumber = UI.nextLine();
			if (oldPinNumber.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (!pinInputAuthentication(oldPinNumber)) {
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
			if (!pinMismatchCheck(member, oldPinNumber)) {
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
			System.out.println("Your old pin number was authenticated successfully");
			System.out.println("");
			break;
		}
		String newPinNumber = "";
		while (true) {
			System.out.print("Now enter your new pin number and then press the enter key: ");
			newPinNumber = UI.nextLine();
			if (newPinNumber.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			if (!pinInputAuthentication(newPinNumber)) {
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
			if (!pinMismatchCheck(member, newPinNumber)) {
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
			System.out.println("Your new pin number was authenticated successfully");
			System.out.println("");
			break;
		}
		System.out.println("Updating pin number...");
		System.out.println("");
		if (!performPinUpdate(member, newPinNumber)) {
			System.out.println("Your pin number could not be updated due to an unexpected error");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return false;
		}
		System.out.println("Your pin number was updated successfully");
		System.out.print("Returning you to your portal. Press the enter key to continue: ");
		UI.nextLine();
		return true;
	}
	
	//Part of the view
	public void adminUpdateUserName(Scanner UI, SuperUser admin) {
		
	}
	
	//Part of the view
	public void adminUpdatePassWord(Scanner UI, SuperUser admin) {
		
	}
	
	//Perform query to display all registered users (Unit test this method)
	public List<BankMember> showRegisteredUsers() {
		Optional<List<BankMember>> bmOpt = bankDao.getAllMembers();
		try {
			if (!bmOpt.isPresent()) {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return new ArrayList<BankMember>();
		}
		return bmOpt.get();
	}
	
	//Part of the view
	public void adminViewAllUsers(Scanner UI, SuperUser admin) {
		while (true) {
			System.out.print("Confirm this action by entering the master pin here: ");
			String pinInput = UI.nextLine();
			if (pinInput.compareTo(admin.getMasterPin()) != 0) {
				System.out.println("Invalid pin input");
				System.out.print("Try again by pressing the enter key: ");
				UI.nextLine();
			}
			break;
		}
		System.out.println("");
		System.out.println("Pin approved!");
		System.out.println("");
		System.out.println("Creating view of all users...");
		System.out.println("");
		List<BankMember> bm = showRegisteredUsers();
		if (bm.size() == 0) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("View of all users could not be created.");
			System.out.print("Check SQL code for errors. Press the enter key to continue: ");
			UI.nextLine();
			return;
		}
		System.out.println("List of all the registered users in the database");
		System.out.println("");
		System.out.println("");
		for (BankMember member : bm) {
			System.out.println(member.toString());
			System.out.println("");
		}
		System.out.println("");
		System.out.print("When finished viewing this list, press the enter key to return to you portal: ");
		UI.nextLine();
	}

	// Part of the view (re-factor this to call the pre-existing testable method for removing an inactive bank member)
	public void adminRemoveUser(Scanner UI, SuperUser admin) {
		System.out.print("Enter the user name of the profile to be removed: ");
		String userName = UI.nextLine();
		Optional<BankMember> memberOpt = bankDao.getBankMember(userName);
		if (!memberOpt.isPresent()) {
			System.out.println("This user name is not in the database");
			System.out.print("Press enter key to continue: ");
			UI.nextLine();
			return;
		}
		Optional<List<BankAccount>> baOpt = bankDao.getAMembersAccounts(memberOpt.get());
		if (baOpt.isPresent()) {
			System.out.println("This user has existing accounts");
			System.out.println("This user should not be removed from the database");
			System.out.println("so that this user does not lose their funds");
			System.out.print("Press enter key to continue: ");
			UI.nextLine();
			return;
		}
		try {
			if (bankDao.removeInactiveBankMember(memberOpt.get()).isPresent()) {
				while (true) {
					System.out.print("Confirm action by entering the master pin here: ");
					String pinInput = UI.nextLine();
					if (pinInput.compareTo(admin.getMasterPin()) != 0) {
						System.out.println("Invalid pin input");
						System.out.print("Try again by pressing the enter key: ");
						UI.nextLine();
					}
					break;
				}
				System.out.println("");
				System.out.println("Pin approved!");
				System.out.println("");
				System.out.println("User removed successfully");
			} else {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("The user profile could not be removed.");
			System.out.print("Check SQL code for errors.");
		}
	}

}
