package constant;

public class SQLIQConst {
	
	public static final StringBuilder DB_INSERT_BSE_SP_TABLE = new StringBuilder()
			.append("INSERT INTO tab_bse_stock_price ")
			.append(" (isin, stock_date, open, high, low, close, prev_close, volume, number_of_trades, net_turn_over, face_value) ")
			.append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	
	public static final StringBuilder DB_INSERT_NSE_SP_TABLE = new StringBuilder()
			.append("INSERT INTO tab_nse_stock_price ")
			.append(" (isin, stock_date, open, high, low, close, prev_close, volume, number_of_trades, net_turn_over, face_value) ")
			.append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

}
