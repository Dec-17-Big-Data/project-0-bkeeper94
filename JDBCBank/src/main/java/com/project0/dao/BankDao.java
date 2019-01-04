package com.project0.dao;

import java.util.List;
import java.util.Optional;

import com.project0.model.*;

public interface BankDao {
	
	Optional<BankMember> getBankMember(String userName);
	
	Optional<BankAccount> getBankAccount(String accountNumber);
	
	Optional<List<BankAccount>> getAMembersAccounts(BankMember member);
	
	Optional<List<BankMember>> getAllMembers();
	
	Optional<Integer> addNewUser(String firstName, String lastName, String userName, String passWord, String pinNumber);
	
	Optional<Integer> openNewBankAccount(BankMember member, String accountType, Double amount);
	
	Optional<Integer> closeOldBankAccount(BankMember member, String accountNumber);
	
	Optional<Integer> removeInactiveBankMember(BankMember member);
	
	Optional<Integer> depositFunds(String accountNumber, Double amount);
	
	Optional<Integer> withdrawFunds(String accountNumber, Double amount);
	
	Optional<Integer> transferFunds(String sourceAccountNumber, String endAccountNumber, Double amount);
	
	Optional<Integer> updateUserName(BankMember member, String newUserName);
	
	Optional<Integer> updatePassWord(BankMember member, String newPassWord);
	
	Optional<Integer> updatePinNumber(BankMember member, String newPinNumber);
	
	Optional<Integer> updateFirstName(BankMember member, String newFirstName);
	
	Optional<Integer> updateLastName(BankMember member, String newLastName);
}
