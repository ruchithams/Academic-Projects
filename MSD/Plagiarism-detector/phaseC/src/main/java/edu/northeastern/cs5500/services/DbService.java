package edu.northeastern.cs5500.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



import java.sql.*;

@Service
public class DbService {

	private String uRL="jdbc:mysql://cs5500-spring2018.ccvlhkhtgw2c.us-east-2.rds.amazonaws.com/Plagiarism_Detector";
	private String uSERNAME="Admin";
	private String cred="team_210";

	private int count=0;
	private int threshold = 0;
	private String role="";
	private String email="";
	private PreparedStatement statement1 = null;
	private PreparedStatement statement2 = null;
	private PreparedStatement statement3 = null;
	private PreparedStatement statement4 = null;
	private PreparedStatement statement5 = null;
	private PreparedStatement statement6 = null;
	private Connection connection = null;
	private ResultSet results1 = null;
	private ResultSet results2 = null;
	private ResultSet results3 = null;
	private ResultSet results4 = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private  static final String TRACE ="Just a stack TRACE";


	/**
	 *
	 * @return threshold value
	 * @throws SQLException
	 */
	private int getThresholdResult() throws SQLException {
		try {

			results1 = statement1.executeQuery();
			while (results1.next()) {
				threshold = results1.getInt("threshold");
			}
		}catch (Exception e){
			logger.debug("stack trace", e);
		}
		connection.close();
		return threshold;
	}


	/**
	 *
	 * @return threshold value
	 * @throws SQLException
	 */
	private int getThresholdQuery() throws SQLException {

		connection = DriverManager.getConnection(uRL, uSERNAME, cred);
		String sql = "SELECT threshold FROM Threshold";
		statement1 = connection.prepareStatement(sql);
		return getThresholdResult();
	}


	/**
	 *
	 * @return threshold value
	 * @throws SQLException
	 */
	public int getThresholdHelper() throws SQLException {
		int thresholdValue=0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			thresholdValue = getThresholdQuery();

		} catch (ClassNotFoundException  e) {
			logger.debug(TRACE,e);
		}
		return thresholdValue;
	}


	/**
	 * @return integer for db update success/failure
	 * @throws SQLException
	 */
	private int setThresholdResult() throws SQLException {
		int result=0;
		result = statement2.executeUpdate();
		connection.close();
		return result;
	}

	/**
	 * @return integer for db update success/failure
	 * @throws SQLException for database failures
	 */
	private int setThresholdQuery(int value) throws SQLException {
		connection = DriverManager.getConnection(uRL, uSERNAME, cred);
		String sql = "Update Threshold set threshold=?";
		statement2 = connection.prepareStatement(sql);
		statement2.setInt(1, value);
		return setThresholdResult();
	}

	/**
	 * @return integer for db update success/failure
	 * @throws SQLException for database failures
	 */
	public int setThresholdHelper(int value) throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return setThresholdQuery(value);

		} catch (ClassNotFoundException  e) {
			logger.debug(TRACE,e);
		}
		return 0;
	}


	/**
	 * @return usage count value
	 * @throws SQLException for database failures
	 */
	private int getUsageCount() throws SQLException {
		results2 = statement3.executeQuery();
		while (results2.next()) {
			count = results2.getInt("usagecount");
		}
		connection.close();
		return count;
	}

	/**
	 *
	 * @param uname username
	 * @return usage count value
	 * @throws SQLException for database failures
	 */
	private int getUsageQuery(String uname) throws SQLException {
		connection = DriverManager.getConnection(uRL, uSERNAME, cred);
		String sql = "SELECT usagecount FROM Login where username=?";
		statement3 = connection.prepareStatement(sql);
		statement3.setString(1, uname);
		return getUsageCount();
	}


	/**
	 *
	 * @param uname username
	 * @return usage count value
	 * @throws SQLException for database failures
	 */

	public int getUsageHelper(String uname) throws SQLException {
		int count=0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			count = getUsageQuery(uname);

		} catch (ClassNotFoundException  e) {
			logger.debug(TRACE,e);
		}
		return count;
	}


	/**
	 * @return integer for db update success/failure
	 * @throws SQLException for database failures
	 */
	private int setUsageCount() throws SQLException {
		int result=0;
		result = statement4.executeUpdate();
		connection.close();
		return result;
	}

	/**
	 * @return integer for db update success/failure
	 * @throws SQLException for database failures
	 */
	private int setUsageQuery(String uname) throws SQLException {
		connection = DriverManager.getConnection(uRL, uSERNAME, cred);
		String sql = "Update Login set usagecount=usagecount+1 where username=?";
		statement4 = connection.prepareStatement(sql);
		statement4.setString(1, uname);
		return setUsageCount();
	}

	/**
	 * @return integer for db update success/failure
	 * @throws SQLException for database failures
	 */
	public int setUsageHelper(String uname) throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return setUsageQuery(uname);

		} catch (ClassNotFoundException  e) {
			logger.debug(TRACE,e);
		}
		return 0;
	}


	/**
	 *
	 * @return role value
	 * @throws SQLException
	 */
	private String getRoleResult() throws SQLException {
		results3 = statement5.executeQuery();
		while (results3.next()) {
			role = results3.getString("role");
		}
		connection.close();
		return role;
	}

	/**
	 *
	 * @return role value
	 * @throws SQLException
	 */
	private String getRoleQuery(String uname) throws SQLException {
		connection = DriverManager.getConnection(uRL, uSERNAME, cred);
		String sql = "SELECT role FROM Login where username=?";
		statement5 = connection.prepareStatement(sql);
		statement5.setString(1, uname);
		return getRoleResult();
	}


	/**
	 *
	 * @return role value
	 * @throws SQLException
	 */
	public String getRoleHelper(String uname) throws SQLException {
		String role="";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			role = getRoleQuery(uname);

		} catch (ClassNotFoundException  e) {
			logger.debug(TRACE,e);
		}
		return role;
	}


}