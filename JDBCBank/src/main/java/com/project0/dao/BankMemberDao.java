package com.project0.dao;

import java.util.List;
import java.util.Optional;

import com.project0.model.*;

public interface BankMemberDao {

	Optional<BankMember> getBankMember(String userName);

	Optional<List<BankAccount>> getAMembersAccounts(BankMember member);

	Optional<List<BankMember>> getAllMembers();

	Optional<Integer> addNewUser(String firstName, String lastName, String userName, String passWord, String pinNumber);

	Optional<Integer> removeInactiveBankMember(BankMember member);

	Optional<Integer> updateUserName(BankMember member, String newUserName);

	Optional<Integer> updatePassWord(BankMember member, String newPassWord);

	Optional<Integer> updatePinNumber(BankMember member, String newPinNumber);

	Optional<Integer> updateFirstName(BankMember member, String newFirstName);

	Optional<Integer> updateLastName(BankMember member, String newLastName);

}
