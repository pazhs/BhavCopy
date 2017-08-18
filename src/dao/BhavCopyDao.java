package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

import bean.StockPriceBean;
import constant.SQLIQConst;
import constant.SQLSQConst;

public class BhavCopyDao {

	public static Connection conn = null;

	public BhavCopyDao() throws ClassNotFoundException, SQLException {
		conn = getConnection();
	}

	public void closeConnection() {

		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}

		} catch (SQLException ex) {

		}
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 * @throws SQLException
	 *             the SQL exception
	 * @throws ClassNotFoundException
	 */
	public Connection getConnection() throws SQLException, ClassNotFoundException {
		// JDBC driver name and database URL
		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
		final String DB_URL = "jdbc:mysql://localhost/allcapz";

		// Database credentials
		final String USER = "root";
		final String PASS = "root";

		try {
			// register JDBC driver
			Class.forName(JDBC_DRIVER);

			// open database connection
			return DriverManager.getConnection(DB_URL, USER, PASS);

		} catch (SQLException ex) {

		}
		return null;
	}

	/**
	 * Close connection.
	 *
	 * @param stmt
	 *            the stmt
	 * @param conn
	 *            the conn
	 * @return true, if successful
	 * @throws SQLException
	 *             the SQL exception
	 */
	public boolean closeConnection(final Statement stmt, final Connection conn) throws SQLException {
		if (stmt != null) {
			stmt.close();
		}
		if (conn != null) {
			conn.close();
		}
		return true;
	}

	public Map<String, Double> obtainSIDate() throws SQLException {

		Map<String, Double> siMap = null;

		ResultSet rs = null;
		Statement stmt = null;

		String isin = null;
		Double faceValue = 0.0;

		try {
			siMap = new LinkedHashMap<String, Double>();

			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQLSQConst.DB_SELECT_SIDATA.toString());

			while (rs.next()) {
				isin = rs.getString("isin");
				faceValue = rs.getDouble("face_value");

				siMap.put(isin, faceValue);
			}

			// DB_SELECT_SIDATA
		} catch (SQLException ex) {

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		return siMap;
	}

	public boolean saveBCData(Map<java.sql.Date, Map<String, StockPriceBean>> spMap, boolean isBse) throws SQLException {

		int counter = 0;
		int[] committedList = null;
		PreparedStatement preStmt = null;

		java.sql.Date stockDate = null;
		Map<String, StockPriceBean> spSecondaryMap = null;
		
		String strIsin = null;
		double open;
		double high;
		double low;
		double close;
		double prevClose;
		int volume;
		int numberOfTrades;
		double netTurnover;
		double faceValue;
		Date tradingDate;
		
		StockPriceBean spBean = null;

		try {

			if (isBse) {
				// NSE stockPrice data
				preStmt = conn.prepareStatement(SQLIQConst.DB_INSERT_BSE_SP_TABLE.toString());
			} else {
				// NSE StockPrice data
				preStmt = conn.prepareStatement(SQLIQConst.DB_INSERT_NSE_SP_TABLE.toString());
			}

			conn.setAutoCommit(false);

			for (Map.Entry<java.sql.Date, Map<String, StockPriceBean>> entry : spMap.entrySet()) {
				spSecondaryMap = entry.getValue();
				for (Map.Entry<String, StockPriceBean> secEntry : spSecondaryMap.entrySet()) {
					
					spBean = secEntry.getValue();

					// (isin, stock_date, open, high, low, close, prev_close,
					// volume, number_of_trades, net_turn_over, face_value) ")

					preStmt.setString(1, spBean.getIsin());
					
					preStmt.setDate(2, spBean.getTradingDate());
					preStmt.setDouble(3, spBean.getOpen());
					preStmt.setDouble(4, spBean.getHigh());
					preStmt.setDouble(5, spBean.getLow());
					preStmt.setDouble(6, spBean.getClose());
					preStmt.setDouble(7, spBean.getPrevClose());
					
					preStmt.setInt(8, spBean.getVolume());
					preStmt.setInt(9, spBean.getNumberOfTrades());
					preStmt.setDouble(10, spBean.getNetTurnover());
					preStmt.setDouble(11, spBean.getFaceValue());
					
					preStmt.addBatch();
					
					if(counter++ == 100){
						committedList = preStmt.executeBatch();
						counter = 0;
					}
				}
			}
			if(counter < 100){
				committedList = preStmt.executeBatch();
			}
			conn.commit();
		} catch (SQLException ex) {

		} finally {
			if(preStmt != null){
				preStmt.close();
			}
		}
		return true;
	}
}
