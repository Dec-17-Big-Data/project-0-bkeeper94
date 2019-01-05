package com.project0.model;

import java.io.Serializable;

public class BankMember implements Serializable{
	// Bean that represents a record in the "bankmembers" SQL table
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -886643837728319518L;
	
	private Integer userID;
	private String userName;
	private String firstName;
	private String lastName;
	private String passWord;
	private String pinNumber;
	
	public BankMember() {
		
	}

	public BankMember(Integer userID, String userName, String firstName, String lastName, String passWord, String pinNumber) {
		this.userID = userID;
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.passWord = passWord;
		this.pinNumber = pinNumber;
	}

	public Integer getUserID() {
		return userID;
	}
	
	// No setter for userID as it is the primary key for the "bankmembers" SQL table
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	
	public String getPinNumber() {
		return pinNumber;
	}

	public void setPinNumber(String pinNumber) {
		this.pinNumber = pinNumber;
	}
	
	@Override
	public String toString() {
		return "BankMember [userID=" + userID + ", userName=" + userName + ", firstName=" + firstName + ", lastName="
				+ lastName + ", passWord=" + passWord + ", pinNumber=" + pinNumber + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((passWord == null) ? 0 : passWord.hashCode());
		result = prime * result + ((pinNumber == null) ? 0 : pinNumber.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
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
		BankMember other = (BankMember) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (passWord == null) {
			if (other.passWord != null)
				return false;
		} else if (!passWord.equals(other.passWord))
			return false;
		if (pinNumber == null) {
			if (other.pinNumber != null)
				return false;
		} else if (!pinNumber.equals(other.pinNumber))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
}
