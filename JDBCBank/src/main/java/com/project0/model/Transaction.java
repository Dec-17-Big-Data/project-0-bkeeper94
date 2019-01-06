package com.project0.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Transaction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8652677745005482526L;
	private Integer transactionID;
	private String sourceAccountNo;
	private String endAccountNo;
	private Integer memberID;
	private String transactionType;
	private Double transactionAmount;
	private Timestamp tranctionTime;
	
	public Transaction() {
		super();
	}

	public Transaction(Integer transactionID, String sourceAccountNo, String endAccountNo, Integer memberID,
			String transactionType, Double transactionAmount, Timestamp tranctionTime) {
		super();
		this.transactionID = transactionID;
		this.sourceAccountNo = sourceAccountNo;
		this.endAccountNo = endAccountNo;
		this.memberID = memberID;
		this.transactionType = transactionType;
		this.transactionAmount = transactionAmount;
		this.tranctionTime = tranctionTime;
	}

	// No setters for these attributes as their values must be immutable
	public Integer getTransactionID() {
		return transactionID;
	}

	public String getSourceAccountNo() {
		return sourceAccountNo;
	}

	public String getEndAccountNo() {
		return endAccountNo;
	}

	public Integer getMemberID() {
		return memberID;
	}

	public String getTransactionType() {
		return transactionType;
	}
	
	public Double getTransactionAmount() {
		return transactionAmount;
	}
	
	public Timestamp getTranctionTime() {
		return tranctionTime;
	}

	@Override
	public String toString() {
		return "Transaction [transactionID=" + transactionID + ", sourceAccountID=" + sourceAccountNo
				+ ", endAccountID=" + endAccountNo + ", memberID=" + memberID + ", transactionType=" + transactionType
				+ ", transactionAmount=" + transactionAmount + ", tranctionTime=" + tranctionTime + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endAccountNo == null) ? 0 : endAccountNo.hashCode());
		result = prime * result + ((memberID == null) ? 0 : memberID.hashCode());
		result = prime * result + ((sourceAccountNo == null) ? 0 : sourceAccountNo.hashCode());
		result = prime * result + ((tranctionTime == null) ? 0 : tranctionTime.hashCode());
		result = prime * result + ((transactionAmount == null) ? 0 : transactionAmount.hashCode());
		result = prime * result + ((transactionID == null) ? 0 : transactionID.hashCode());
		result = prime * result + ((transactionType == null) ? 0 : transactionType.hashCode());
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
		Transaction other = (Transaction) obj;
		if (endAccountNo == null) {
			if (other.endAccountNo != null)
				return false;
		} else if (!endAccountNo.equals(other.endAccountNo))
			return false;
		if (memberID == null) {
			if (other.memberID != null)
				return false;
		} else if (!memberID.equals(other.memberID))
			return false;
		if (sourceAccountNo == null) {
			if (other.sourceAccountNo != null)
				return false;
		} else if (!sourceAccountNo.equals(other.sourceAccountNo))
			return false;
		if (tranctionTime == null) {
			if (other.tranctionTime != null)
				return false;
		} else if (!tranctionTime.equals(other.tranctionTime))
			return false;
		if (transactionAmount == null) {
			if (other.transactionAmount != null)
				return false;
		} else if (!transactionAmount.equals(other.transactionAmount))
			return false;
		if (transactionID == null) {
			if (other.transactionID != null)
				return false;
		} else if (!transactionID.equals(other.transactionID))
			return false;
		if (transactionType == null) {
			if (other.transactionType != null)
				return false;
		} else if (!transactionType.equals(other.transactionType))
			return false;
		return true;
	}
	
}
