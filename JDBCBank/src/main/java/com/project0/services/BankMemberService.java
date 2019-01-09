package com.project0.services;

import com.project0.model.*;
import com.project0.exceptions.*;
import com.project0.dao.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

public class BankMemberService {

	private static BankMemberService bankService;
	final static BankMemberDao bankMemberDao = BankMemberOracle.getDao();

	private BankMemberService() {

	}

	public static BankMemberService getService() {
		if (bankService == null) {
			bankService = new BankMemberService();
		}

		return bankService;
	}

	// Getter for entries in the bankmembers SQL table (Unit test this method)
	public BankMember getBankMember(String userName) {
		try {
			Optional<BankMember> bankMemberOpt = bankMemberDao.getBankMember(userName);
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

	// Checks to make sure the input for first name and last name consist only of
	// letters
	// (Unit test this method)
	public boolean nameInputChecker(String name) {
		try {
			if (name.length() == 0) {
				throw new InvalidNameInputException();
			}
			for (int i = 0; i < name.length(); i++) {
				if (name.substring(i, i + 1).matches("[^-A-Za-z ]")) {
					throw new InvalidNameInputException();
				}
			}
			return true;
		} catch (InvalidNameInputException e) {
			return false;
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
			Optional<Integer> result = bankMemberDao.addNewUser(firstName, lastName, userNameInput, passWordInput,
					pinInput);
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
				if (!nameInputChecker(firstName)) {
					for (int i = 0; i < 50; ++i)
						System.out.println();
					continue;
				}
				break;
			}
			while (true) {
				System.out.print("Type in your last name and then press the enter key: ");
				lastName = UI.nextLine();
				if (!nameInputChecker(lastName)) {
					for (int i = 0; i < 50; ++i)
						System.out.println();
					continue;
				}
				break;
			}
			System.out.println("");
			System.out.println("Now you will create the user name and password you will use to access your portal");
			System.out.println("The user name you enter will be checked against our records to ensure uniqueness");
			System.out.println("");
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
			System.out.println("");
			System.out.println(
					firstName + ", " + lastName + ", " + userNameInput + ", " + passWordInput + ", " + pinInput);
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
			if (member == null) {
				return null;
			}
			Optional<List<BankAccount>> baListOpt = bankMemberDao.getAMembersAccounts(member);
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
		System.out.println("Account Number" + "      " + "Account Type" + "        " + "Balance");
		System.out.println("--------------------------------------------------------");
		for (BankAccount ba : baList) {
			System.out.println(ba.getAccountNo() + "          " + ba.getAccountType() + "            " + "$"
					+ String.format("%.2f", ba.getBalance()));
			System.out.println("");
		}
		System.out.println("");
		System.out.print("Press the enter key to continue: ");
		UI.nextLine();
		System.out.println("");
	}

	// Performs removal of inactive bank member (Unit test this method)
	public boolean performMemberRemoval(BankMember member) {
		try {
			Optional<Integer> resultOpt = bankMemberDao.removeInactiveBankMember(member);
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
			System.out.println("");
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
			if (!performMemberRemoval(member)) {
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

	// Performs the user name update (Unit test this method)
	public int performUsernameUpdate(BankMember member, String newUserName) {
		try {
			Optional<Integer> resultOpt = bankMemberDao.updateUserName(member, newUserName);
			if (!resultOpt.isPresent()) {
				throw new NoSuchElementException();
			} else if (resultOpt.get() == -1) {
				throw new DuplicateUserNameException();
			}
		} catch (DuplicateUserNameException e) {
			return 0;
		} catch (NoSuchElementException e) {
			return -1;
		}
		return 1;
	}

	// Part of the view
	public boolean updateUserName(BankMember member, Scanner UI) {
		while (true) {
			System.out.println("");
			System.out.println("You have selected to update a user name");
			System.out.println("");
			System.out.print("Type in a new user name and then press the enter key: ");
			String newUserName = UI.nextLine();
			System.out.println("");
			System.out.println("Here is the new user name: ");
			System.out.println(newUserName);
			System.out.println("");
			System.out.println("Confirm this new user name by typing in 'yes'");
			System.out.println("OR if you would like to retry updating the user name, type in 'no'");
			System.out.println("");
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
				System.out.println("User name update failed");
				System.out.println("The new user name was already found in the system");

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
			Optional<Integer> resultOpt = bankMemberDao.updatePassWord(member, newPassword);
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
			System.out.print("Type in the new password and then press the enter key: ");
			String newPassword = UI.nextLine();
			System.out.println("");
			System.out.println("Here is the new password: ");
			System.out.println(newPassword);
			System.out.println("");
			System.out.println("Confirm this new password by typing in 'yes'");
			System.out.println("OR if you would like to retry updating the password, type in 'no'");
			System.out.println("");
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

	// Performs the pin number update (Unit test this method)
	public boolean performPinUpdate(BankMember member, String newPinNumber) {
		try {
			Optional<Integer> resultOpt = bankMemberDao.updatePinNumber(member, newPinNumber);
			if (!resultOpt.isPresent()) {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}

	// Part of the view
	public void updatePinNumber(BankMember member, Scanner UI) {
		String newPinNumber = "";
		while (true) {
			System.out.println("");
			System.out.println("You have selected to update a user's pin number ");
			System.out.println("");
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
			System.out.println("");
			System.out.println("The new pin number was authenticated successfully");
			System.out.println("");
			System.out.println("Here is the new pin number: ");
			System.out.println(newPinNumber);
			System.out.println("");
			System.out.println("Confirm this new pin by typing in 'yes'");
			System.out.println("OR if you would like to retry updating the pin, type in 'no'");
			System.out.println("");
			System.out.print("Type in your answer here and then press the enter key: ");
			String answer = UI.nextLine();
			if (answer.compareToIgnoreCase("no") == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
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

	// Performs the update of the user's first name (Unit test this method)
	public boolean performFirstNameUpdate(BankMember member, String newFirstName) {
		try {
			Optional<Integer> resultOpt = bankMemberDao.updateFirstName(member, newFirstName);
			if (!resultOpt.isPresent()) {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}

	// Part of the view
	public void updateFirstName(BankMember member, Scanner UI) {
		System.out.println("");
		System.out.println("You have selected to update a user's first name");
		System.out.println("");
		String newFirstName = "";
		while (true) {
			System.out.print("Enter the new first name and then press the enter key: ");
			newFirstName = UI.nextLine();
			if (!nameInputChecker(newFirstName)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			System.out.println("");
			System.out.println("Here is the new first name: ");
			System.out.println(newFirstName);
			System.out.println("");
			System.out.println("Confirm the new first name by typing in 'yes'");
			System.out.println("OR if you would like to retry updating the first name, type in 'no'");
			System.out.println("");
			System.out.print("Type in your answer here and then press the enter key: ");
			String answer = UI.nextLine();
			if (answer.compareToIgnoreCase("no") == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			break;
		}
		System.out.println("Updating first name...");
		System.out.println("");
		if (!performFirstNameUpdate(member, newFirstName)) {
			System.out.println("The first name could not be updated due to an unexpected error");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return;
		}
		System.out.println("Successful update.  Press the enter key to exit the screen: ");
		UI.nextLine();
	}

	// Performs the update of the user's last name (Unit test this method)
	public boolean performLastNameUpdate(BankMember member, String newLastName) {
		try {
			Optional<Integer> resultOpt = bankMemberDao.updateLastName(member, newLastName);
			if (!resultOpt.isPresent()) {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			return false;
		}
		return true;
	}

	// Part of the view
	public void updateLastName(BankMember member, Scanner UI) {
		System.out.println("");
		System.out.println("You have selected to update a user's last name");
		System.out.println("");
		String newLastName = "";
		while (true) {
			System.out.print("Enter the new last name and then press the enter key: ");
			System.out.println("");
			newLastName = UI.nextLine();
			if (!nameInputChecker(newLastName)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			break;
		}
		System.out.println("Updating last name...");
		System.out.println("");
		if (!performLastNameUpdate(member, newLastName)) {
			System.out.println("The last name could not be updated due to an unexpected error");
			System.out.print("Press the enter key to exit: ");
			UI.nextLine();
			return;
		}
		System.out.println("Successful update.  Press the enter key to exit the screen: ");
		UI.nextLine();
	}

	// Part of the view
	public void masterPinCheck(Scanner UI, SuperUser admin) {
		while (true) {
			System.out.println("Confirm your identity as admin by typing");
			System.out.print("in the master pin and then press the enter key: ");
			String pinInput = UI.nextLine();
			if (pinInput.compareTo(admin.getMasterPin()) != 0) {
				System.out.println("");
				System.out.println("Invalid pin input");
				System.out.print("Try again by pressing the enter key: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
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
				adminUpdateUserInformation(UI, admin);
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
			System.out.println("");
			System.out.println("To logout, press the enter key when prompted to make a selection");
			System.out.println("OR");
			System.out.println("Select one of the options above by typing in its number, ");
			System.out.print("and then press the enter key: ");
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
				updateFirstName(member, UI);
				break;
			case "5":
				updateLastName(member, UI);
				break;
			default:
				continue;
			}
			break;
		}
	}

	// Perform query to display all registered users (Unit test this method)
	public List<BankMember> showRegisteredUsers() {
		Optional<List<BankMember>> bmOpt = bankMemberDao.getAllMembers();
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
		System.out.println("Type in the user name of the profile you wish");
		System.out.print("to update and then press the enter key: ");
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
		System.out.println("You have selected the option to remove a user portal from the system");
		System.out.println("");
		masterPinCheck(UI, admin);
		System.out.println("");
		System.out.println("Pin approved");
		System.out.println("");
		String userToUpdate = retrieveAUserNameFromList(UI);
		BankMember bm = getBankMember(userToUpdate);
		if (bm == null) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("This user name is not in the database");
			System.out.print("Press the enter key to try another search: ");
			UI.nextLine();
			for (int i = 0; i < 50; ++i)
				System.out.println();
			adminRemoveUser(UI, admin);
		}
		List<BankAccount> ba = retrieveAMembersBankAccounts(bm); 
		if (ba == null) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("The user profile could not be removed.");
			System.out.print("Check SQL code or the database connection.");
			System.out.println("");
			System.out.print("Press the enter key to return to your portal: ");
			UI.nextLine();
			return;
		}
		if (!ba.isEmpty()) {
			System.out.println("");
			System.out.println("");
			System.out.println("This user has existing accounts");
			System.out.println("");
			System.out.println("This user should not be removed from the database");
			System.out.println("so that this user does not lose their funds");
			System.out.println("");
			System.out.print("Press the enter key to continue: ");
			UI.nextLine();
			return;
		}
		System.out.println("Removing user...");
		System.out.println("");
		if (!performMemberRemoval(bm)) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			System.out.println("The user profile could not be removed.");
			System.out.print("Check SQL code or the database connection.");
			System.out.println("");
			System.out.print("Press the enter key to return to your portal: ");
			UI.nextLine();
			return;
		}
		System.out.println("User removed successfully");
		System.out.println("");
		System.out.print("Press the enter key to return to your portal: ");
		UI.nextLine();
		return;
	}

}
