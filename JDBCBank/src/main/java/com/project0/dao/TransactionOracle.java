package com.project0.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.project0.model.BankMember;
import com.project0.model.Transaction;
import com.project0.utils.ConnectionUtil;

public class TransactionOracle implements TransactionDao {
	private static TransactionOracle transactionOracle;

	private static final Logger log = LogManager.getLogger(BankAccountOracle.class);

	private TransactionOracle() {

	}

	public static TransactionDao getDao() {
		if (transactionOracle == null) {
			transactionOracle = new TransactionOracle();
		}
		return transactionOracle;
	}

	@Override
	public Optional<List<Transaction>> getAMembersTransactions(BankMember member) {
		log.traceEntry("getAMembersTransactions: " + "Parameter= {}", member.toString());

		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "select * from transactions where member_id = ?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, member.getUserID());
			ResultSet rs = ps.executeQuery();

			List<Transaction> ta = new ArrayList<Transaction>();

			while (rs.next()) {
				ta.add(new Transaction(rs.getInt("transaction_id"), rs.getString("source_account_id"), 
						rs.getString("end_account_id"), rs.getInt("member_id"), rs.getString("transaction_type"),
						rs.getDouble("transaction_amount"), rs.getTimestamp("transaction_time")));
			}

			return log.traceExit(Optional.of(ta));
		} catch (SQLException e) {
			log.catching(e);
			log.error("SQL exception occurred", e);
		}

		log.traceExit(Optional.empty());
		return Optional.empty();
	}

	@Override
	public Optional<Integer> addNewTransaction(BankMember member, String sourceAccountNo, String endAccountNo,
			Double amount, String transactionType) {
		log.traceEntry("addNewTransaction: " + "Parameter= {}", member.toString());
		log.traceEntry("addNewTransaction: " + "Parameter= {}", sourceAccountNo);
		log.traceEntry("addNewTransaction: " + "Parameter= {}", endAccountNo);
		log.traceEntry("addNewTransaction: " + "Parameter= {}", amount);
		log.traceEntry("addNewTransaction: " + "Parameter= {}", transactionType);
		
		Connection con = ConnectionUtil.getConnection();

		if (con == null) {
			log.traceExit(Optional.empty());
			return Optional.empty();
		}

		try {
			String sql = "Call add_new_transaction(?,?,?,?,?,?)";
			CallableStatement cb = con.prepareCall(sql);
			cb.setString(1, sourceAccountNo);
			cb.setString(2, endAccountNo);
			cb.setInt(3, member.getUserID());
			cb.setString(4, transactionType);
			cb.setDouble(5, amount);
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
	
}
