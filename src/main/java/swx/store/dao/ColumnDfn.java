package swx.store.dao;

class ColumnDfn
{
	final int	 sqlType;
	final String columnName;
	final String columnId;
	
	ColumnDfn(String columnName, int columnType) {
		this.columnName = columnName;
		this.sqlType = columnType;
		this.columnId = dbNameToCamelOne(columnName);
	}
	
	private static String dbNameToCamelOne(String dbName) {
		String[] ss = dbName.split("_");
		StringBuilder sb = new StringBuilder(ss[0]);
		for (int index=1; index<ss.length; index++) {
			sb.append(Character.toUpperCase(ss[index].charAt(0))).append(ss[index].substring(1));
		}
		return sb.toString();
	}	
}
