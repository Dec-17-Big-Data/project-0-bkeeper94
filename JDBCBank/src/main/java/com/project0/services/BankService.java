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
	
	// Getter for entries in the bankmembers SQL table (Unit test this method)
	public BankMember getBankMember(String userName) {
		try {
			Optional<BankMember> bankMemberOpt = bankDao.getBankMember(userName);
			if (bankMemberOpt.isPresent()) {
				return bankMemberOpt.get();
			} else {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return null;
		}

	}
	
	// Helper method for application's login screen (Unit test this method)
	public boolean loginAuthentication(String userNameInput, String passWordInput) {
		try {
			BankMember bm = getBankMember(userNameInput);
			if (bm == null) {
				throw new LoginMismatchException();
			} else if (bm.getPassWord().compareTo(passWordInput) != 0) {
				throw new LoginMismatchException();
			} else {
				return true;
			}
		} catch (LoginMismatchException e) {
			return false;
		}
	}

	
	// Getter for entries in the bankaccounts SQL table (Unit test this method)
	public BankAccount getBankAccount(String accountNumber) {
		try {
			Optional<BankAccount> bankAccountOpt = bankDao.getBankAccount(accountNumber);
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

	// Adds the user but first checks if the input user name is already registered
	// to another user (Unit test this method)
	public int addNewUser(String firstName, String lastName, String userNameInput, String passWordInput,
			String pinInput) {
		try {
			Optional<Integer> result = bankDao.addNewUser(firstName, lastName, userNameInput, passWordInput, pinInput);
			if (!result.isPresent()) {
				throw new NoSuchElementException();
			} else if (result.get() == -1) {
				throw new DuplicateUserNameException();
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
			String firstName = "";
			String lastName = "";
			String userNameInput = "";
			String passWordInput = "";
			String pinInput = "";
			while (true) {
				System.out.print("Portal Creation Page");
				System.out.println("");
				System.out.println("");
				System.out.print("Type in your first name and then press the enter key: ");
				firstName = UI.nextLine();
				if (firstName.length() == 0) {
					for (int i = 0; i < 50; ++i)
						System.out.println();
					continue;
				}
				break;
			}
			while (true) {
				System.out.print("Type in your last name and then press the enter key: ");
				lastName = UI.nextLine();
				if (lastName.length() == 0) {
					for (int i = 0; i < 50; ++i)
						System.out.println();
					continue;
				}
				break;
			}
			System.out.println("");
			System.out.println("Now you will create the user name and password you will use to access your portal");
			System.out.println("The user name you enter will be checked against our records to ensure uniqueness");
			while (true) {
				System.out.print("Type in your desired user name and then press the enter key: ");
				userNameInput = UI.nextLine();
				if (userNameInput.length() == 0) {
					for (int i = 0; i < 50; ++i)
						System.out.println();
					continue;
				}
				break;
			}
			while (true) {
				System.out.println("");
				System.out.print("Type in your desired password and then press the enter key: ");
				passWordInput = UI.nextLine();
				if (passWordInput.length() == 0) {
					for (int i = 0; i < 50; ++i)
						System.out.println();
					continue;
				}
				break;
			}
			while (true) {
				System.out.println("");
				System.out.print("To complete creating your profile, type in a "
						+ "four-digit pin number and then press the enter key: ");
				pinInput = UI.nextLine();
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
				break;
			}
			System.out.println("");
			System.out.println("Here is all of your profile information: ");
			System.out.println(
					firstName + " ," + lastName + " ," + userNameInput + " ," + passWordInput + ", " + pinInput);
			System.out.println("");
			System.out.println("Confirm all of the above information by typing in 'yes'");
			System.out.println("OR if you would like to retry creating your profile, type in 'no'");
			System.out.print("Type in your answer here and then press the enter key: ");
			String answer = UI.nextLine();
			if (answer.compareToIgnoreCase("no") == 0) {
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
				System.out.println("User name was already found in the system");

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

	// Checks that the user typed in a properly formatted dollar amount (Unit test
	// this method)
	public boolean amountInputChecker(String value) {
		try {
			if (value.length() == 0) {
				return false;
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

	//Changes the amount input by the user from a string to a double (Unit Test this method)
	public Double amountInputAsNumber(String amount) {
		amount = amount.replaceAll("[$,]", "");
		return Math.round(100.0 * Double.valueOf(amount)) / 100.0;
	}
	
	// Opens a new account (Unit test this method)
	public boolean addAccount(BankMember member, String accountType, Double valueAsNum) {
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
		System.out.println("You will now open an account with the Bank of OOPs");
		System.out.println("First, specify the type of account you wish to open");
		String accountType = "";
		while (true) {
			System.out.println("We offer checking accounts and savings accounts");
			System.out.print("Type in either 'checking' or 'savings' and then press the enter key: ");
			accountType = UI.nextLine();
			if (accountType.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			switch (accountType) {
			case "checking":
			case "savings":
				break;
			default:
				for (int i = 0; i < 50; ++i)
					System.out.println();
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
		if (!addAccount(member, accountType, valueAsNum)) {
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
			System.out.println("You will now close one of your accounts with the Bank of OOPs");
			System.out.print("Enter the number of the account you would like to close and then press the enter key: ");
			String accountNumber = UI.nextLine();
			System.out.println("");
			System.out.println("Checking our records for the account number you entered...");
			System.out.println("");
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
			} else if (resultOpt.get() == -1) {
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
			System.out.println("You have decided to remove your Bank of OOPs portal from the system");
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
			System.out.print("Press the enter key to continue: ");
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
			Optional<Integer> resultOpt = bankDao.depositFunds(accountNumber, depositAmount);
			if (!resultOpt.isPresent() || resultOpt.get() == -1) {
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
		Double depositAsNum = amountInputAsNumber(depositAmount);
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
			Optional<Integer> resultOpt = bankDao.withdrawFunds(accountNumber, withdrawAmount);
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
		Double withdrawAsNum = amountInputAsNumber(withdrawAmount);
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
			Optional<Integer> resultOpt = bankDao.transferFunds(sourceAccountNumber, endAccountNumber, transferAmount);
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
		Double transferAsNum = amountInputAsNumber(transferAmount);
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

	// Performs the user name update (Unit test this method)
	public int performUsernameUpdate(BankMember member, String newUserName) {
		try {
			Optional<Integer> resultOpt = bankDao.updateUserName(member, newUserName);
			if (!resultOpt.isPresent()) {
				throw new NoSuchElementException();
			} else if (resultOpt.get() == -1) {
				return 0;
			}
		} catch (NoSuchElementException e) {
			return -1;
		}
		return 1;
	}

	public boolean updateUserName(BankMember member, Scanner UI) {
		while (true) {
			System.out.println("");
			System.out.println("You have selected to update a user name");
			System.out.println("");
			System.out.print("Type in a new user name and then press the enter key: ");
			String newUserName = UI.nextLine();
			System.out.println("");
			System.out.println("Here is your new user name: ");
			System.out.println(newUserName);
			System.out.println("");
			System.out.println("Confirm your new user name by typing in 'yes'");
			System.out.println("OR if you would like to retry updating your user name, type in 'no'");
			System.out.print("Type in your answer here and then press the enter key: ");
			String answer = UI.nextLine();
			if (answer.compareToIgnoreCase("yes") != 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			System.out.println("");
			System.out.println("Changing the user name...");
			System.out.println("");
			if (performUsernameUpdate(member, newUserName) == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("User portal creation failed");
				System.out.println("User name was already found in the system");

				System.out.print("Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			} else if (performUsernameUpdate(member, newUserName) == -1) {
				System.out.println("This user name could not be updated due to an unexpected error");
				System.out.print("Press the enter key to exit: ");
				UI.nextLine();
				return false;
			}
			System.out.println("");
			System.out.print("User name changed successfully.  Press the enter key to leave this screen: ");
			UI.nextLine();
			return true;
		}
	}

	// Performs the password update (Unit test this method)
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

	// Part of the view
	public boolean updatePassWord(BankMember member, Scanner UI) {
		while (true) {
			System.out.println("");
			System.out.println("You have selected to update a password");
			System.out.println("");
			System.out.print("Type in a new password and then press the enter key: ");
			String newPassword = UI.nextLine();
			System.out.println("");
			System.out.println("Here is your new password: ");
			System.out.println(newPassword);
			System.out.println("");
			System.out.println("Confirm your new password by typing in 'yes'");
			System.out.println("OR if you would like to retry updating your password, type in 'no'");
			System.out.print("Type in your answer here and then press the enter key: ");
			String answer = UI.nextLine();
			if (answer.compareToIgnoreCase("no") == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
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
			System.out.print("Password changed successfully. Press the enter key to leave this screen: ");
			UI.nextLine();
			return true;
		}
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

	// Part of the view
	public void updatePinNumber(BankMember member, Scanner UI) {
		System.out.println("");
		System.out.println("You have selected to update a pin number ");
		System.out.println("");
		String newPinNumber = "";
		while (true) {
			System.out.print("Enter the new pin number and then press the enter key: ");
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
			System.out.println("The new pin number was authenticated successfully");
			System.out.println("");
			break;
		}
		System.out.println("Updating pin number...");
		System.out.println("");
		if (!performPinUpdate(member, newPinNumber)) {
			System.out.println("The pin number could not be updated due to an unexpected error");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return;
		}
		System.out.println("The pin number was updated successfully.  Press the enter key to exit the screen: ");
		UI.nextLine();
	}
	
	// Part of the view
	public void masterPinCheck(Scanner UI, SuperUser admin) {
		while (true) {
			System.out.print("Confirm your identity as admin by typing in the master pin and then press the enter key: ");
			String pinInput = UI.nextLine();
			if (pinInput.compareTo(admin.getMasterPin()) != 0) {
				System.out.println("Invalid pin input");
				System.out.print("Try again by pressing the enter key: ");
				UI.nextLine();
			}
			break;
		}
	}
	
	// Part of the view
	public void adminUpdateUserInformation(Scanner UI, SuperUser admin) {
		System.out.println("You have selected the option to change a member's profile information");
		System.out.println("");
		masterPinCheck(UI, admin);
		System.out.println("");
		System.out.println("Pin approved");
		System.out.println("");
		BankMember member = null;
		while (true) {
			String userToUpdate = retrieveAUserNameFromList(UI);
			member = getBankMember(userToUpdate);
			if (member == null) {
				System.out.println("The user name you searched was not found in our records");
				System.out.println("");
				System.out.print("Press the enter key to try another search: ");
				UI.nextLine();
				adminUpdateUserInformation(UI,admin);
			}
			break;
		}
		while (true) {
			System.out.println("");
			System.out.println("");
			System.out.println("You may update any of the following pieces of user data:");
			System.out.println("1: Username");
			System.out.println("2: Password");
			System.out.println("3: Pin Number");
			System.out.println("4: First Name");
			System.out.println("5: Last Name");
			System.out.print("Select one of the options here by its number and then "
					+ "press the enter key (for example, type in '1' if you want to change a user name): ");
			String selection = UI.nextLine();
			switch (selection) {
				case "1":
					updateUserName(member, UI);
					break;
				case "2":
					updatePassWord(member, UI);
					break;
				case "3":
					updatePinNumber(member, UI);
					break;
				case "4":
					
					break;
				case "5":
					
					break;
				default:
					continue;
			}
			break;
		}
	}

	// Perform query to display all registered users (Unit test this method)
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
	
	// Part of the view
	public String retrieveAUserNameFromList(Scanner UI) {
		List<String> usersList = printListOfAllUsers(UI);
		System.out.print("Type in the user name of the profile you wish "
				+ "to update and then press the enter key: ");
		String input = UI.nextLine();
		if (usersList.contains(input)) {
			int loc = usersList.indexOf(input);
			return usersList.get(loc);
		} else {
			return "";
		}
	}
	
	// Part of the view
	public List<String> printListOfAllUsers(Scanner UI) {
		List<BankMember> bm = showRegisteredUsers();
		List<String> usersList = new ArrayList<String>();
		if (bm.size() == 0) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("View of all users could not be created.");
			System.out.print("Check SQL code for errors. Press the enter key to continue: ");
			UI.nextLine();
			return new ArrayList<String>();
		}
		System.out.println("List of all the registered users in the database");
		System.out.println("");
		System.out.println("");
		for (BankMember member : bm) {
			usersList.add(member.getUserName());
			System.out.println(member.toString());
			System.out.println("");
		}
		return usersList;
	}
	
	// Part of the view
	public void adminViewAllUsers(Scanner UI, SuperUser admin) {
		System.out.println("You have chosen to view all registered users");
		System.out.println("");
		masterPinCheck(UI, admin);
		System.out.println("");
		System.out.println("Pin approved");
		System.out.println("");
		System.out.println("Creating view of all users...");
		System.out.println("");
		printListOfAllUsers(UI);
		System.out.println("");
		System.out.print("When finished viewing this list, press the enter key to return to your portal: ");
		UI.nextLine();
	}

	// Part of the view
	public void adminRemoveUser(Scanner UI, SuperUser admin) {
		System.out.println("You have selected the option to change a member's profile information");
		System.out.println("");
		masterPinCheck(UI, admin);
		System.out.println("");
		System.out.println("Pin approved");
		System.out.println("");
		String userToUpdate = retrieveAUserNameFromList(UI);
		if (getBankMember(userToUpdate) == null) {
			System.out.println("This user name is not in the database");
			System.out.print("Press the enter key to try another search: ");
			UI.nextLine();
			adminRemoveUser(UI, admin);
		}
		if (!retrieveAMembersBankAccounts(getBankMember(userToUpdate)).isEmpty()) {
			System.out.println("This user has existing accounts");
			System.out.println("This user should not be removed from the database");
			System.out.println("so that this user does not lose their funds");
			System.out.print("Press the enter key to continue: ");
			UI.nextLine();
			return;
		}
		System.out.println("Removing user...");
		System.out.println("");
		if (!performMemberRemoval(getBankMember(userToUpdate))) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("The user profile could not be removed.");
			System.out.print("Check SQL code for errors.");
			System.out.println("");
			System.out.print("Press the enter key to return to your portal: ");
			UI.nextLine();
			return;
		}
		System.out.println("User removed successfully");
		System.out.println("");
		System.out.print("Press the enter key to return to your portal: ");
		UI.nextLine();
	}
}
