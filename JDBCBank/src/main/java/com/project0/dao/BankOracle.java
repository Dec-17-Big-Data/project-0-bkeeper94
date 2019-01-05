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

public class BankOracle implements BankDao {

	private static BankOracle bankOracle;

	private static final Logger log = LogManager.getLogger(BankOracle.class);

	private BankOracle() {

	}

	public static BankDao getDao() {
		if (bankOracle == null) {
			bankOracle = new BankOracle();
		}
		return bankOracle;
	}

	@Override
	public Optional<BankMember> getBankMember(String userName) {
		log.traceEntry("getBankMember: " + "Parameter= {}", userName);

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "select * from bankmembers where user_name = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();

			BankMember member = null;

			while (rs.next()) {
				member = new BankMember(rs.getInt("member_id"), rs.getString("user_name"), rs.getString("first_name"),
						rs.getString("last_name"), rs.getString("user_password"), rs.getString("pin_number"));
			}

			if (member == null) {
				log.traceExit(Optional.of(new BankMember()));
				return Optional.of(new BankMember());
			}

			return log.traceExit(Optional.of(member));
		} catch (SQLException e) {
			log.catching(e);
			log.error("SQL exception occurred", e);
		}

		log.traceExit(Optional.empty());
		return Optional.empty();

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

	public Optional<List<BankMember>> getAllMembers() {
		log.traceEntry("getAllMembers: ");

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "select * from bankmembers";
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			List<BankMember> bl = new ArrayList<BankMember>();

			while (rs.next()) {
				bl.add(new BankMember(rs.getInt("member_id"), rs.getString("user_name"), rs.getString("first_name"),
						rs.getString("last_name"), rs.getString("user_password"), rs.getString("pin_number")));
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
	public Optional<Integer> addNewUser(String firstName, String lastName, String userName, String passWord,
			String pinNumber) {

		log.traceEntry("getAMembersAccounts: " + "Parameter= {}", firstName);
		log.traceEntry("getAMembersAccounts: " + "Parameter= {}", lastName);
		log.traceEntry("getAMembersAccounts: " + "Parameter= {}", userName);
		log.traceEntry("getAMembersAccounts: " + "Parameter= {}", passWord);
		log.traceEntry("getAMembersAccounts: " + "Parameter= {}", pinNumber);

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call add_member(?,?,?,?,?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, firstName);
			cb.setString(2, lastName);
			cb.setString(3, userName);
			cb.setString(4, passWord);
			cb.setString(5, pinNumber);
			cb.registerOutParameter(6, java.sql.Types.INTEGER);
			cb.execute();
			int result = cb.getInt(6);

			return log.traceExit(Optional.of(result));
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
	public Optional<Integer> removeInactiveBankMember(BankMember member) {
		log.traceEntry("removeInactiveBankMember: " + "Parameter= {}", member.toString());

		Connection con = ConnectionUtil.getConnection();

		if (!getBankMember(member.getUserName()).isPresent()) {
			log.traceExit(Optional.empty());
			return Optional.of(-1);
		}

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call delete_member(?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, member.getUserName());
			cb.registerOutParameter(2, java.sql.Types.INTEGER);
			cb.execute();
			int result = cb.getInt(2);
			
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

	@Override
	public Optional<Integer> updateUserName(BankMember member, String newUserName) {
		log.traceEntry("updateUserName: " + "Parameter= {}", member.toString());
		log.traceEntry("updateUserName: " + "Parameter= {}", newUserName);

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call change_user_name(?,?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, newUserName);
			cb.setInt(2, member.getUserID());
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
	public Optional<Integer> updatePassWord(BankMember member, String newPassWord) {
		log.traceEntry("updatePassWord: " + "Parameter= {}", member.toString());
		log.traceEntry("updatePassWord: " + "Parameter= {}", newPassWord);

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call change_password(?,?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, newPassWord);
			cb.setInt(2, member.getUserID());
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
	public Optional<Integer> updatePinNumber(BankMember member, String newPinNumber) {
		log.traceEntry("updatePinNumber: " + "Parameter= {}", member.toString());
		log.traceEntry("updatePinNumber: " + "Parameter= {}", newPinNumber);

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call change_pin(?,?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, newPinNumber);
			cb.setInt(2, member.getUserID());
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
	public Optional<Integer> updateFirstName(BankMember member, String newFirstName) {
		log.traceEntry("updateFirstName: " + "Parameter= {}", member.toString());
		log.traceEntry("updateFirstName: " + "Parameter= {}", newFirstName);

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call change_first_name(?,?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, newFirstName);
			cb.setInt(2, member.getUserID());
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
	public Optional<Integer> updateLastName(BankMember member, String newLastName) {
		log.traceEntry("updateLastName: " + "Parameter= {}", member.toString());
		log.traceEntry("updateLastName: " + "Parameter= {}", newLastName);

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call change_last_name(?,?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, newLastName);
			cb.setInt(2, member.getUserID());
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
}
