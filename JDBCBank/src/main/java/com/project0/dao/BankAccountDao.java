package com.project0.dao;

import java.util.List;
import java.util.Optional;

import com.project0.model.*;

public interface BankAccountDao {

	Optional<BankAccount> getBankAccount(String accountNumber);

	Optional<List<BankAccount>> getAMembersAccounts(BankMember member);

	Optional<Integer> openNewBankAccount(BankMember member, String accountType, Double amount);

	Optional<Integer> closeOldBankAccount(BankMember member, String accountNumber);

	Optional<Integer> depositFunds(String accountNumber, Double amount);

	Optional<Integer> withdrawFunds(String accountNumber, Double amount);

	Optional<Integer> transferFunds(String sourceAccountNumber, String endAccountNumber, Double amount);

}
