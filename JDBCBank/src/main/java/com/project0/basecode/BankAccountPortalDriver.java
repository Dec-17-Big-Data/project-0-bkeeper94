package com.project0.basecode;

import java.util.List;
import java.util.Scanner;

import com.project0.exceptions.*;
import com.project0.model.*;
import com.project0.services.*;

public abstract class BankAccountPortalDriver {

	static SuperUser admin = SuperUser.getAdmin();

	// This application assumes that the user will be able to use
	// the enter key to navigate through each screen

	// Welcome Screen
	private static String startScreen(Scanner UI) {
		while (true) {
			System.out.println("Welcome to the Bank of Evil!");
			System.out.println("");
			System.out.println("");
			System.out.println("Do you have an existing account with us?");
			System.out.println("Answer 'yes' if you do.  Answer 'no' if you do not.");
			System.out.println("If you would instead like to exit this program, answer 'out'");
			System.out.print("Type your answer here and then press the enter key: ");
			String s = UI.nextLine();
			if (s.compareToIgnoreCase("yes") == 0 || s.compareToIgnoreCase("no") == 0
					|| s.compareToIgnoreCase("out") == 0) {
				return s;
			} else {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
		}
	}

	// Helper method for login authentication (unit test this method)
	public static boolean loginAuthentication(BankService bs, String userNameInput, String passWordInput) {
		try {
			if (bs.getBankMember(userNameInput).toString().compareTo(new BankMember().toString()) == 0) {
				throw new LoginMismatchException();
			} else if (bs.getBankMember(userNameInput).get().getPassWord().compareTo(passWordInput) != 0) {
				throw new LoginMismatchException();
			} else {
				return true;
			}
		} catch (LoginMismatchException e) {
			return false;
		} 
	}
	
	// Profile Log-In Screen
	private static String[] loginScreen(BankService bs, Scanner UI, SuperUser admin) {
		// first instantiate the array to be returned by this method
		// this array contains the user name and if the current login was done by admin
		String[] results = new String[2];
		results[1] = "false";
		while (true) {
			System.out.println("User Login Page");
			System.out.println("");
			System.out.println("");
			System.out.print("Type in your user name and then press the enter key: ");
			String userNameInput = UI.nextLine();
			if (userNameInput.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			System.out.print("Type in your password and then press the enter key: ");
			String passWordInput = UI.nextLine();
			if (passWordInput.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			}
			// check for an admin login first
			if (userNameInput.compareTo(admin.getMasterUserName()) == 0
					&& passWordInput.compareTo(admin.getMasterPassCode()) == 0) {
				results[0] = admin.getMasterUserName();
				results[1] = "true";
				return results;
			}
			if (!loginAuthentication(bs, userNameInput, passWordInput)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.print("The user name or password entered does not match our records! "
						+ "Press the enter key to try again: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
			} else {
				results[0] = userNameInput;
				results[1] = "false";
				return results;
			}
		}
	}

	// User Portal
	private static String userPortalOverviewScreen(BankService bs, Scanner UI, String userName) {
		// retrieve the instance of BankAccount created for the user identified by userName
		BankMember member = bs.getBankMember(userName).get();
		System.out.println("Welcome, " + member.getUserName());
		System.out.println("");
		System.out.println("");
		List<BankAccount> baList = bs.retrieveAMembersBankAccounts(member);
		if (baList == null) {
			System.out.println("");
			System.out.println("Your accounts could not be displayed due to an unexpected error");
			System.out.println("Your portal cannot be viewed at this time");
			System.out.println("");
			System.out.println("This program will now be closed");
			return "out";
		}
		// Handle two cases when the user has no bank accounts on file
		if (baList.isEmpty()) {
			while (true) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("You currently have no accounts with us");
				System.out.println("If you are logging in for the first time, answer with 'first'");
				System.out.println("If you have closed all your accounts with us and no longer "
						+ "wish to remain a member, answer with 'out'");
				System.out.print("Type your answer here and then press the enter key: ");
				String s = UI.nextLine();
				if (s.compareToIgnoreCase("first") == 0) {
					bs.openNewAccount(member, UI);
					break;
				} else if (s.compareToIgnoreCase("out") == 0) {
					if (bs.removeInactiveMember(member, UI)) {
						System.out.println("");
						System.out.println("This program will now be closed");
						return "out";
					} else {
						for (int i = 0; i < 50; ++i)
							System.out.println();
						return userPortalOverviewScreen(bs, UI, userName);
					}
				} else {
					for (int i = 0; i < 50; ++i)
						System.out.println();
					continue;
				}
			}
			return userPortalOverviewScreen(bs, UI, userName);
		}
		bs.printAMembersBankAccounts(baList, UI);
		// Handle case when all of the accounts on a user's portal show $0 in funds
		if (baList.get(0).getBalance() == 0) {
			int flag = 0;
			for (BankAccount ba : baList) {
				if (ba.getBalance() > 0) {
					flag = 1;
				}
			}
			if (flag == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				System.out.println("According to our records, you have accounts with us ");
				System.out.println("but they have 0 funds within them");
				System.out.println("");
				System.out.println("We highly recommend you deposit funds into at least one account ");
				System.out.println("to maintain your status as an active member of the Bank of Evil");
				System.out.println("");
				System.out.println("If you no longer wish to remain a member of the Bank of Evil, ");
				System.out.println("please close all of your empty accounts first and then you will be guided ");
				System.out.println("through the process of removing your user profile");
				System.out.println("");
				System.out.print("Press the enter key to continue: ");
				UI.nextLine();
			}
		}
		System.out.println("");
		System.out.println("");
		System.out.println("Your options as an active member of the Bank of Evil: ");
		System.out.println("");
		System.out.println("1: Open a new account");
		System.out.println("2: Close an account with a balance of $0");
		System.out.println("3: Deposit funds into an existing account");
		System.out.println("4: Withdraw funds from an existing account");
		System.out.println("5: Transfer funds to another one of your accounts");
		System.out.println("6: Change user name");
		System.out.println("7: Change password");
		System.out.println("8: Change pin number");
		System.out.println("To logout, simply press the enter key when prompted to make a selection from the above options");
		System.out.println("");
		System.out.print("Select one of the options here by its number and then "
				+ "press the enter key (for example, type in '1' if you want to open a new account): ");

		String option = UI.nextLine();
		System.out.println("");
		System.out.println("");

		switch (option) {
		case "1":
			if (bs.openNewAccount(member, UI)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				return userPortalOverviewScreen(bs, UI, userName);
			} else {
				break;
			}
		case "2":
			if (bs.closeEmptyBankAccount(member, UI)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				return userPortalOverviewScreen(bs, UI, userName);
			} else {
				break;
			}
		case "3":
			if (bs.deposit(member, UI)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				return userPortalOverviewScreen(bs, UI, userName);
			} else {
				break;
			}
		case "4":
			if (bs.withdraw(member, UI)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				return userPortalOverviewScreen(bs, UI, userName);
			}
			else {
				break;
			}
		case "5":
			if (bs.transferFunds(member, UI)) {	
				for (int i = 0; i < 50; ++i)
					System.out.println();
				return userPortalOverviewScreen(bs, UI, userName);
			} else {
				break;
			}
		case "6":
			if (bs.updateUserName(member, UI)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				return userPortalOverviewScreen(bs, UI, userName);
			} else {
				break;
			}
		case "7":
			if (bs.updatePassWord(member, UI)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				return userPortalOverviewScreen(bs, UI, userName);
			} else {
				break;
			}
		case "8":
			if (bs.updatePinNumber(member, UI)) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				return userPortalOverviewScreen(bs, UI, userName);
			} else {
				break;
			}
		default:
			break;
		}
		//refactor this block of code
		while (true) {
			System.out.println("You will now leave your user portal");
			System.out.println("To confirm your selection, type 'yes'. If you would like to be returned to your "
					+ "portal, type 'no'");
			System.out.print("Type in your answer here and then press the enter key: ");
			String answer = UI.nextLine();
			if (answer.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			} else if (answer.compareToIgnoreCase("yes") != 0 || answer.compareToIgnoreCase("no") != 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			} else if (answer.compareToIgnoreCase("no") == 0) {
				System.out.println("");
				System.out.println("Returning you to your main portal overview");
				System.out.print("Press the enter key to continue: ");
				UI.nextLine();
				for (int i = 0; i < 50; ++i)
					System.out.println();
				return userPortalOverviewScreen(bs, UI, userName);
			}
			System.out.println("");
			System.out.println("Would you like to exit this program or would you like to be logged out?");
			System.out.println("Type in 'exit' if you would like to leave the program or "
					+ "type in either 'log out' or 'logout' if you would like to be logged out");
			System.out.print("Input your answer here and then press the enter key: ");
			String nextAnswer = UI.nextLine();
			if (nextAnswer.length() == 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			} else if (nextAnswer.compareToIgnoreCase("logout") != 0
					|| nextAnswer.compareToIgnoreCase("log out") != 0
					|| nextAnswer.compareToIgnoreCase("exit") != 0) {
				for (int i = 0; i < 50; ++i)
					System.out.println();
				continue;
			} else if (nextAnswer.compareToIgnoreCase("exit") == 0) {
				return "out";
			} else {
				return logOut(UI);
			}
		}
	}

	// Administrator Portal
	private static void adminPortalOverviewScreen(BankService bs, Scanner UI, SuperUser admin) {
		System.out.println("Welcome, Admin");
		System.out.println("");
		System.out.println("");
		System.out.println("Here are your options as admin:");
		System.out.println("");
		System.out.println("1: Add a new user to the database");
		System.out.println("2: Remove a user from the database");
		System.out.println("3: Change a user name");
		System.out.println("4: Change a password");
		System.out.println("5: View all user's as a list");
		System.out.println("");
		System.out.println("To logout, simply press the enter key when ");
		System.out.println("prompted to make a selection from the above options");
		System.out.println("");
		System.out.print("Select one of the options here by its number and then "
				+ "press the enter key (for example, type in '1' if you want to add a new user): ");
		String option = UI.nextLine();
		System.out.println("");
		System.out.println("");
		switch (option) {
		case "1":
			bs.portalCreation(UI);
			for (int i = 0; i < 50; ++i)
				System.out.println();
			adminPortalOverviewScreen(bs, UI, admin);
		case "2":
			bs.adminRemoveUser(UI, admin);
			for (int i = 0; i < 50; ++i)
				System.out.println();
			adminPortalOverviewScreen(bs, UI, admin);
		case "3":
			bs.adminUpdateUserName(UI, admin);
			for (int i = 0; i < 50; ++i)
				System.out.println();
			adminPortalOverviewScreen(bs, UI, admin);
		case "4":
			bs.adminUpdatePassWord(UI, admin);
			for (int i = 0; i < 50; ++i)
				System.out.println();
			adminPortalOverviewScreen(bs, UI, admin);
		case "5":
			bs.adminViewAllUsers(UI, admin);
			for (int i = 0; i < 50; ++i)
				System.out.println();
			adminPortalOverviewScreen(bs, UI, admin);
		default:
			break;
		}
		System.out.println("");
		System.out.println("You can either be logged out of the admin portal or exit this program");
		while(true) {
			System.out.println("Type 'exit' to exit the program or type 'logout' to be logged out");
			System.out.print("Type in your answer and then press the enter key: ");
			String answer = UI.nextLine();
			if (answer.compareToIgnoreCase("exit") == 0) {
				return;
			} else if (answer.compareToIgnoreCase("logout") == 0) {
				break;
			}
		}
		logOut(UI);
	}

	// Portal Logout
	public static String logOut(Scanner UI) {
		for (int i = 0; i < 50; ++i)
			System.out.println();
		System.out.println("Logging you out of your portal...");
		System.out.print("Press enter to continue: ");
		UI.nextLine();
		for (int i = 0; i < 50; ++i)
			System.out.println();
		return startScreen(UI);
	}

	public static void main(String[] args) {
		BankService bs = BankService.getService();
		Scanner UI = new Scanner(System.in);
		String resultOfStart = startScreen(UI);
		String[] results = new String[2];
		if (resultOfStart.compareToIgnoreCase("yes") == 0) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			results = loginScreen(bs, UI, admin);
			if (results[1].compareToIgnoreCase("True") == 0) {
				adminPortalOverviewScreen(bs, UI, admin);
				System.out.println("");
				System.out.println("");
				System.out.println("Terminating program...");
				UI.close();
				return;
			}
		} else if (resultOfStart.compareToIgnoreCase("no") == 0) {
			for (int i = 0; i < 50; ++i)
				System.out.println();
			results[0] = bs.portalCreation(UI);
		} else if (resultOfStart.compareToIgnoreCase("out") == 0) {
			System.out.println("");
			System.out.println("Terminating program...");
			UI.close();
			return;
		}
		for (int i = 0; i < 50; ++i)
			System.out.println();
		if (results[0].compareTo("no portal") == 0) {
			System.out.println("");
			System.out.println("");
			System.out.println("Terminating program...");
			UI.close();
			return;
		}
		String s = userPortalOverviewScreen(bs, UI, results[0]);
		if (s.compareToIgnoreCase("out") == 0) {
			System.out.println("");
			System.out.println("");
			System.out.println("Terminating program...");
			UI.close();
			return;
		}
	}
	
}
