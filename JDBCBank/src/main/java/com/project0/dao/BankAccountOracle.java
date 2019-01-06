package com.project0.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.project0.model.*;
import com.project0.utils.ConnectionUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BankAccountOracle implements BankAccountDao {
	
	private static BankAccountOracle bankAccountOracle;

	private static final Logger log = LogManager.getLogger(BankAccountOracle.class);

	private BankAccountOracle() {

	}

	public static BankAccountDao getDao() {
		if (bankAccountOracle == null) {
			bankAccountOracle = new BankAccountOracle();
		}
		return bankAccountOracle;
	}

	@Override
	public Optional<BankAccount> getBankAccount(String accountNumber) {
		log.traceEntry("getBankAccount: " + "Parameter= {}", accountNumber);

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "select * from bankaccounts where account_no = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, accountNumber);
			ResultSet rs = ps.executeQuery();

			BankAccount ba = null;

			while (rs.next()) {
				ba = new BankAccount(rs.getInt("account_id"), rs.getString("account_type"), rs.getString("account_no"),
						rs.getInt("member_id"), rs.getDouble("balance"));
			}

			if (ba == null) {
				log.traceExit(Optional.of(new BankAccount()));
				return Optional.empty();
			}

			return log.traceExit(Optional.of(ba));
		} catch (SQLException e) {
			log.catching(e);
			log.error("SQL exception occurred", e);
		}

		log.traceExit(Optional.empty());
		return Optional.empty();

	}
	
	@Override
	public Optional<List<BankAccount>> getAMembersAccounts(BankMember member) {
		log.traceEntry("getAMembersAccounts: " + "Parameter= {}", member.toString());

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "select * from bankaccounts where member_id = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, member.getUserID());
			ResultSet rs = ps.executeQuery();

			List<BankAccount> bl = new ArrayList<BankAccount>();

			while (rs.next()) {
				bl.add(new BankAccount(rs.getInt("account_id"), rs.getString("account_type"),
						rs.getString("account_no"), rs.getInt("member_id"), rs.getDouble("balance")));
			}

			if (bl.isEmpty()) {
				log.traceExit(Optional.of(new ArrayList<BankAccount>()));
				return Optional.of(new ArrayList<BankAccount>());
			}

			return log.traceExit(Optional.of(bl));
		} catch (SQLException e) {
			log.catching(e);
			log.error("SQL exception occurred", e);
		}

		log.traceExit(Optional.empty());
		return Optional.empty();

	}
	
	@Override
	public Optional<Integer> openNewBankAccount(BankMember member, String accountType, Double amount) {
		log.traceEntry("openNewBankAccount: " + "Parameter= {}", member.toString());
		log.traceEntry("openNewBankAccount: " + "Parameter= {}", accountType);
		log.traceEntry("openNewBankAccount: " + "Parameter= {}", amount);

		// generate bank account number (random 10 digit number)
		String[] accountNoArray = new String[10];
		String accountNo = "";
		for (int i = 0; i < 10; i++) {
			int randNo = (int) Math.floor(10 * Math.random());
			if (randNo == 10) {
				randNo = 1;
			}
			accountNoArray[i] = String.valueOf(randNo);
			accountNo = accountNo + accountNoArray[i];
		}

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call add_account(?,?,?,?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, accountNo);
			cb.setInt(2, member.getUserID());
			cb.setString(3, accountType);
			cb.setDouble(4, amount);
			cb.registerOutParameter(5, java.sql.Types.INTEGER);
			cb.execute();
			int result = cb.getInt(5);
			
			// Case when account is not opened because the user id is not in the database
			if (result == -1) {
				return log.traceExit(Optional.empty());
			}
			
			return log.traceExit(Optional.of(result));
		} catch (SQLException e) {
			log.catching(e);
			log.error("SQL exception occurred", e);
		}

		log.traceExit(Optional.empty());
		return Optional.empty();

	}

	@Override
	public Optional<Integer> closeOldBankAccount(BankMember member, String accountNumber) {
		log.traceEntry("closeOldBankAccount: " + "Parameter= {}", member.toString());
		log.traceEntry("closeOldBankAccount: " + "Parameter= {}", accountNumber);

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call delete_account(?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, accountNumber);
			cb.registerOutParameter(2, java.sql.Types.INTEGER);
			cb.execute();
			int result = cb.getInt(2);
			
			if (result == -1) {
				return log.traceExit(Optional.empty());
			}
			
			return log.traceExit(Optional.of(result));
		} catch (SQLException e) {
			log.catching(e);
			log.error("SQL exception occurred", e);
		}

		log.traceExit(Optional.empty());
		return Optional.empty();

	}
	
	@Override
	public Optional<Integer> depositFunds(String accountNumber, Double amount) {
		log.traceEntry("depositFunds: " + "Parameter= {}", accountNumber);
		log.traceEntry("depositFunds: " + "Parameter= {}", amount);

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call deposit(?,?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, accountNumber);
			cb.setDouble(2, amount);
			cb.registerOutParameter(3, java.sql.Types.INTEGER);
			cb.execute();
			Integer result = cb.getInt(3);
			
			return log.traceExit(Optional.of(result));
		} catch (SQLException e) {
			log.catching(e);
			log.error("SQL exception occurred", e);
		}

		log.traceExit(Optional.empty());
		return Optional.empty();

	}

	@Override
	public Optional<Integer> withdrawFunds(String accountNumber, Double amount) {
		log.traceEntry("withdrawFunds: " + "Parameter= {}", accountNumber);
		log.traceEntry("withdrawFunds: " + "Parameter= {}", amount);

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call withdraw(?,?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, accountNumber);
			cb.setDouble(2, amount);
			cb.registerOutParameter(3, java.sql.Types.INTEGER);
			cb.execute();
			Integer result = cb.getInt(3);

			return log.traceExit(Optional.of(result));
		} catch (SQLException e) {
			log.catching(e);
			log.error("SQL exception occurred", e);
		}

		log.traceExit(Optional.empty());
		return Optional.empty();

	}

	@Override
	public Optional<Integer> transferFunds(String sourceAccountNumber, String endAccountNumber, Double amount) {
		log.traceEntry("transferFunds: " + "Parameter= {}", sourceAccountNumber);
		log.traceEntry("transferFunds: " + "Parameter= {}", endAccountNumber);
		log.traceEntry("transferFunds: " + "Parameter= {}", amount);

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call transfer(?,?,?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, sourceAccountNumber);
			cb.setString(2, endAccountNumber);
			cb.setDouble(3, amount);
			cb.registerOutParameter(4, java.sql.Types.INTEGER);
			cb.execute();
			Integer result = cb.getInt(4);

			return log.traceExit(Optional.of(result));
		} catch (SQLException e) {
			log.catching(e);
			log.error("SQL exception occurred", e);
		}

		log.traceExit(Optional.empty());
		return Optional.empty();
	}
	
}
