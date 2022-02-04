package swx.store.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import swx.springboot.model.MiscItem;
import swx.springboot.utils.MiConverter;

@Repository
public class MiscItemDAO
{
	Logger logger = LoggerFactory.getLogger(MiscItemDAO.class);
	
	private static class ColumnInfo {
		int sqlType;
		String columnName;
		Object columnValue;
	}
	
	private final static String CATEG = "categ";
	private final static String VALUE_CATEG = "sp.api";
	private final static String VALUE1 = "value1";
	private final static String VALUE2 = "value2";

	private final static ColumnDfn[] COLUMN_DFN = {
		new ColumnDfn("item_name", Types.NVARCHAR),
        new ColumnDfn("item_date", Types.DATE),
        new ColumnDfn(CATEG, Types.NVARCHAR),
        new ColumnDfn("descr", Types.NVARCHAR),
        new ColumnDfn(VALUE1, Types.INTEGER),
        new ColumnDfn(VALUE2, Types.DECIMAL),
        new ColumnDfn("more", Types.NVARCHAR)			
	};
	
	private boolean addParamValue(String columnId, String dbColumn, Object objValue, MapSqlParameterSource mapSqlParam) {
		if ("itemDate".equals(columnId)) {
			MiConverter.DateResult dateResult = MiConverter.parseLocalDate(objValue);
			if (dateResult.outcome == MiConverter.PARSE_ERR) {
				logger.error("DAO.addParamValue: date param parse error");
				return false;
			}
			LocalDate localDate = (dateResult.outcome == MiConverter.BLANK_INPUT) ? LocalDate.now() : dateResult.localDate;			
			mapSqlParam.addValue(dbColumn, java.sql.Date.valueOf(localDate));
			return true;
		}
		if (CATEG.equals(columnId)) {
			mapSqlParam.addValue(dbColumn, VALUE_CATEG); 
		} else if (VALUE1.equals(columnId)) {
    	    mapSqlParam.addValue(dbColumn, MiConverter.parseIntValue(objValue));
		} else if (VALUE2.equals(columnId)) {
    	    mapSqlParam.addValue(dbColumn, MiConverter.parseDoubleValue(objValue));
		} else {
			mapSqlParam.addValue(dbColumn, (String)objValue);
		}
		return true;
	}
	
	private ColumnInfo findColumnInfo(String sKey) {
		for (ColumnDfn dfn: COLUMN_DFN) {
			if (!dfn.columnId.equals(sKey))
				continue;
			String cn = dfn.columnName;
			ColumnInfo info = new ColumnInfo();
			info.columnName = VALUE1.equals(cn) ? "valueN1" : (VALUE2.equals(cn) ? "valueD1" : cn);
			info.sqlType = dfn.sqlType;
			return info;
		}
		logger.info("** DB: unmatched key - " + sKey);
		return null;
	}

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Integer addRecord(Map<String, Object> mapValue)
    {
    	final String paramPK = "@item_id";
    	List<SqlParameter> params = Stream.of(COLUMN_DFN).map(r -> new SqlParameter(r.columnName,r.sqlType)).collect(Collectors.toList());
    	params.add(new SqlOutParameter(paramPK, Types.INTEGER));
    	SqlParameter[] declareParams = new SqlParameter[params.size()+1];
    	
	    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
	            .withProcedureName("dbo.AddItem")
	            .withoutProcedureColumnMetaDataAccess()
	            .declareParameters(params.toArray(declareParams));

	    MapSqlParameterSource mapSqlParam = new MapSqlParameterSource();
	    for (ColumnDfn dfn: COLUMN_DFN) {
	    	if (!addParamValue(dfn.columnId, dfn.columnName, mapValue.get(dfn.columnId), mapSqlParam))
	    		return -1;
	    }

	    Map<String, Object> out = call.execute(mapSqlParam);
	    return (Integer)out.get(paramPK);    	
    }
    
    public Map<String, String> updateRecord(Map<String, Object> mapValue)
    {
    	Integer itemId = null;
	    List<ColumnInfo> columns = new ArrayList<>();
	    
    	StringBuilder sbColumn = new StringBuilder(1000);
    	for (Map.Entry<String, Object> entry : mapValue.entrySet()) {
			if ("itemId".equals(entry.getKey())) {
				itemId = MiConverter.parseIntValue(entry.getValue());
				continue;
			}     		
			ColumnInfo info = findColumnInfo(entry.getKey());
			if (info != null) {
				info.columnValue = entry.getValue();
				sbColumn.append(",[").append(info.columnName).append("]=?");
				columns.add(info);
			}
    	}
    	if (itemId == null) 	return Map.of("status", "E", "details", "Missing Id");
	    if (columns.isEmpty())	return Map.of("status", "W", "details", "No field");
	    
    	sbColumn.insert(0, "UPDATE dbo.MiscItems SET [dttm2]=sysdatetime()");
	    sbColumn.append(" WHERE [item_id]=? AND [categ]=?");
	    String sql = sbColumn.toString();
		logger.info("SQL stmt: " + sql);
		
		Boolean result = execUpdate(sql, itemId.intValue(), columns);
		if (result == null)			return Map.of("status", "sql", "op", "update", "details", itemId + ": execUpdate");
		if (result.booleanValue())	return Map.of("status", "OK", "op", "update", "itemId", String.valueOf(itemId));
		return Map.of("status", "E", "details", itemId + ": ill-formatted sql");
    }
    
    private Boolean execUpdate(String sql, int itemId, List<ColumnInfo> columns)
    {
		try (
			Connection conn= jdbcTemplate.getDataSource().getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
		) {
		    final int nColumn = columns.size()+1;
		    for (int iCol=1; iCol < nColumn; iCol++) {
		    	ColumnInfo info = columns.get(iCol-1);
		    	if (Types.DATE == info.sqlType) {
					MiConverter.DateResult dateResult = MiConverter.parseLocalDate(info.columnValue);
					if (dateResult.outcome == MiConverter.PARSE_ERR) {
						logger.error("DAO.execUpdate: date param parse error - #" + iCol);
						return false;
					}
					LocalDate localDate = (dateResult.outcome == MiConverter.BLANK_INPUT) ? LocalDate.now() : dateResult.localDate;			
	        		pstmt.setDate(iCol, java.sql.Date.valueOf(localDate));		    		
		    	} else if (Types.INTEGER == info.sqlType) {
	    	   		Integer iValue = MiConverter.parseIntValue(info.columnValue);
	    			pstmt.setInt(iCol, iValue.intValue());		    		
		    	} else if (Types.DECIMAL == info.sqlType) {
	    	   		Double dValue = MiConverter.parseDoubleValue(info.columnValue);
	    	    	pstmt.setBigDecimal(iCol, BigDecimal.valueOf(dValue));		    		
		    	} else {
	    			pstmt.setString(iCol, (String)info.columnValue);		    		
		    	}
		    }
			//logger.info(String.format("** pstmt %d,%s", nColumn, CATEG));
			pstmt.setInt(nColumn, itemId);
			pstmt.setString(nColumn+1, VALUE_CATEG);
			return (pstmt.executeUpdate() > 0);
		} catch (SQLException e) {
			logger.error("DAO.execUpdate", e);
			return null;
		} 
    }
    
        
    
	public Integer deleteRecord(int itemId)
	{
	    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
	    		.withProcedureName("dbo.DeleteItem")
	            .withoutProcedureColumnMetaDataAccess()
	            .declareParameters(
	            	new SqlParameter("item_id", Types.INTEGER),
	            	new SqlParameter(CATEG, Types.NVARCHAR),
                    new SqlOutParameter("@row", Types.INTEGER)
	            );
	    SqlParameterSource in = new MapSqlParameterSource().addValue("itemId", itemId).addValue(CATEG, VALUE_CATEG);
	    Map<String, Object> out = call.execute(in);
	    return (Integer)out.get("@row");    	
	}

	/**
	 * see doc/GetItem.sql
	 * @return List<MiscItem>
	 */
	public List<MiscItem> getAllRecords()
	{
        List<MiscItem> ret = new ArrayList<>();
		try (
			Connection conn= jdbcTemplate.getDataSource().getConnection();
			PreparedStatement pstmt = conn.prepareStatement("{call dbo.GetItem(?,?)}");
		) {
			pstmt.setInt(1, 0);
			pstmt.setString(2, VALUE_CATEG);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	MiscItem mi = new MiscItem();
	        	mi.setItemId(rs.getInt("item_id"));
	        	mi.setName(rs.getString("item_name"));
	        	mi.setDescr(rs.getString("descr"));
	        	mi.setValue1(rs.getInt(VALUE1));
	        	mi.setValue2(rs.getDouble(VALUE2));
	        	LocalDateTime dttm = MiConverter.parseDttm(rs.getString("dttmstr"));
	        	if (dttm != null) {
	        		mi.setDttm(dttm);
	        	}
	        	mi.setMore(rs.getString("more"));
	        	java.sql.Date itemDate = rs.getDate("item_date");
	        	if (itemDate != null) {
		        	mi.setItemDate(itemDate.toLocalDate());
	        	}
	        	ret.add(mi);
				//logger.info(String.format("** %1$d, %2$tF", mi.getItemId(), mi.getItemDate()));
	        }
		} catch (SQLException e) {
			logger.error("DAO.getAllRecord", e);
		}
        return ret;
	}
}
