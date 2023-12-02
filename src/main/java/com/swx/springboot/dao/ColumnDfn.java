package com.swx.springboot.dao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ColumnDfn {
	final int	 sqlType;
	final String columnName;
	final String columnId;
	
	private final static Pattern PAT = Pattern.compile("_[a-zA-Z]");
	
	ColumnDfn(String columnName, int columnType) {
		this.columnName = columnName;
		this.sqlType = columnType;
		this.columnId = dbNameToCamelOne(columnName);
	}
	
	private static String dbNameToCamelOne(String dbName) {
		Matcher ma = PAT.matcher(dbName);
		return ma.replaceAll(m -> m.group().substring(1).toUpperCase());
	}
}
