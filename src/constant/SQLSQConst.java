package constant;

public class SQLSQConst {
	
	// get data from table tab_resource
	public static final String DB_SELECT_RESOURCE_TABLE = "SELECT stock_all, stock_all_seq FROM tab_resource";
	
	/** The Constant DB_SELECT_RESOURCE_TABLE. */
	public static final StringBuilder DB_SELECT_SIDATA = new StringBuilder("SELECT isin, face_value")
		.append(" FROM tab_stock_info WHERE face_value > 0.00");
}
