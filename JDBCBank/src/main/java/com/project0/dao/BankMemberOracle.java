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

public class BankMemberOracle implements BankMemberDao {

	private static BankMemberOracle bankOracle;

	private static final Logger log = LogManager.getLogger(BankMemberOracle.class);

	private BankMemberOracle() {

	}

	public static BankMemberDao getDao() {
		if (bankOracle == null) {
			bankOracle = new BankMemberOracle();
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
				log.traceExit(Optional.empty());
				return Optional.empty();
			}

			return log.traceExit(Optional.of(member));
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
	public Optional<Integer> removeInactiveBankMember(BankMember member) {
		// Catch case when bank member has already been removed but this method is called anyway
		if (member == null) {
			return log.traceExit(Optional.of(-1));
		}
		
		log.traceEntry("removeInactiveBankMember: " + "Parameter= {}", member.toString());

		Connection con = ConnectionUtil.getConnection();

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
