package com.project0.model;

import java.io.Serializable;

public class BankAccount implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9190801140617881665L;
	private Integer accountID;
	private String accountType;
	private String accountNo;
	private Integer memberID;
	private Double balance;

	public BankAccount() {
		super();
	}

	public BankAccount(Integer accountID, String accountType, String accountNo, Integer memberID, Double balance) {
		super();
		this.accountID = accountID;
		this.accountType = accountType;
		this.accountNo = accountNo;
		this.memberID = memberID;
		this.balance = balance;
	}

	public Integer getAccountID() {
		return accountID;
	}

	// no setter for accountID as it is the primary key for the "bankaccounts" SQL
	// table

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountNo() {
		return accountNo;
	}

	// no setter for accountNo as it is generated once upon account creation and is
	// never modified

	public Integer getMemberID() {
		return memberID;
	}

	// no setter for memberID as it is a foreign key in the "bankaccounts" SQL table
	// that points to the primary key for the "bankmembers" table

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "BankAccount [accountID=" + accountID + ", accountType=" + accountType + ", accountNo=" + accountNo
				+ ", memberID=" + memberID + ", balance=" + balance + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountID == null) ? 0 : accountID.hashCode());
		result = prime * result + ((accountNo == null) ? 0 : accountNo.hashCode());
		result = prime * result + ((accountType == null) ? 0 : accountType.hashCode());
		result = prime * result + ((balance == null) ? 0 : balance.hashCode());
		result = prime * result + ((memberID == null) ? 0 : memberID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BankAccount other = (BankAccount) obj;
		if (accountID == null) {
			if (other.accountID != null)
				return false;
		} else if (!accountID.equals(other.accountID))
			return false;
		if (accountNo == null) {
			if (other.accountNo != null)
				return false;
		} else if (!accountNo.equals(other.accountNo))
			return false;
		if (accountType == null) {
			if (other.accountType != null)
				return false;
		} else if (!accountType.equals(other.accountType))
			return false;
		if (balance == null) {
			if (other.balance != null)
				return false;
		} else if (!balance.equals(other.balance))
			return false;
		if (memberID == null) {
			if (other.memberID != null)
				return false;
		} else if (!memberID.equals(other.memberID))
			return false;
		return true;
	}

}
