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
	private final static ColumnDfn VALUE_N1 = new ColumnDfn("valueN1", Types.INTEGER);
	private final static ColumnDfn VALUE_D1 = new ColumnDfn("valueD1", Types.DECIMAL);
	
	private static void addParamValue(String columnId, String dbColumn, Object objValue, MapSqlParameterSource mapSqlParam) {
		if (CATEG.equals(columnId)) {
			mapSqlParam.addValue(dbColumn, VALUE_CATEG); 
		} else if ("itemDate".equals(columnId)) {
    	    LocalDate localDate = MiConverter.parseLocalDate(objValue);
    	    mapSqlParam.addValue(dbColumn, java.sql.Date.valueOf(localDate));
		} else if (VALUE1.equals(columnId)) {
    	    mapSqlParam.addValue(dbColumn, MiConverter.parseIntValue(objValue));
		} else if (VALUE2.equals(columnId)) {
    	    mapSqlParam.addValue(dbColumn, MiConverter.parseDoubleValue(objValue));
		} else {
			mapSqlParam.addValue(dbColumn, (String)objValue);
		}
	}
	
	private static ColumnDfn findColumnDfn(String sKey) {
		for (ColumnDfn dfn: COLUMN_DFN) {
			if (dfn.columnId.equals(sKey))
				return  VALUE1.equals(dfn.dbColumn) ? VALUE_N1 : (
						VALUE2.equals(dfn.dbColumn) ? VALUE_D1 : dfn);
		}
		return null;
	}

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Integer addRecord(Map<String, Object> mapValue)
    {
    	final String paramPK = "@item_id";
    	List<SqlParameter> params = Stream.of(COLUMN_DFN).map(r -> new SqlParameter(r.dbColumn,r.sqlType)).collect(Collectors.toList());
    	params.add(new SqlOutParameter(paramPK, Types.INTEGER));
    	SqlParameter[] declareParams = new SqlParameter[params.size()+1];
    	
	    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
	            .withProcedureName("dbo.AddItem")
	            .withoutProcedureColumnMetaDataAccess()
	            .declareParameters(params.toArray(declareParams));

	    MapSqlParameterSource mapSqlParam = new MapSqlParameterSource();
	    for (ColumnDfn dfn: COLUMN_DFN) {
	    	addParamValue(dfn.columnId, dfn.dbColumn, mapValue.get(dfn.columnId), mapSqlParam);
	    }

	    Map<String, Object> out = call.execute(mapSqlParam);
	    return (Integer)out.get(paramPK);    	
    }
    
    public Map<String, String> updateRecord(Map<String, Object> mapValue)
    {
    	Integer itemId = null;
	    List<ColumnDfn> columns = new ArrayList<>();
	    
    	StringBuilder sbColumn = new StringBuilder(1000);
    	for (Map.Entry<String, Object> entry : mapValue.entrySet()) {
			if ("itemId".equals(entry.getKey())) {
				itemId = MiConverter.parseIntValue(entry.getValue());
				continue;
			}     		
			ColumnDfn dfn = findColumnDfn(entry.getKey());
			if (dfn != null) {
				sbColumn.append(",[").append(dfn.dbColumn).append("]=?");
				columns.add(dfn);
			}
    	}
    	if (itemId == null) 	return Map.of("status", "E", "details", "Missing Id");
	    if (columns.isEmpty())	return Map.of("status", "W", "details", "No field");
	    
    	sbColumn.insert(0, "UPDATE dbo.MiscItems SET [dttm2]=sysdatetime()");
	    sbColumn.append(" WHERE [item_id]=? AND [categ]=?");
	    String sql = sbColumn.toString();
		logger.info("SQL stmt: " + sql);
	    
		try (
			Connection conn= jdbcTemplate.getDataSource().getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
		) {
		    final int nColumn = columns.size()+1;
		    for (int iCol=1; iCol < nColumn; iCol++) {
		    	ColumnDfn dfn = columns.get(iCol-1);
		    	Object obj = mapValue.get(dfn.columnId);
		    	if (Types.DATE == dfn.sqlType) {
	    	   		LocalDate localDate = MiConverter.parseLocalDate(obj);
	        		pstmt.setDate(iCol, java.sql.Date.valueOf(localDate));		    		
		    	} else if (Types.INTEGER == dfn.sqlType) {
	    	   		Integer iValue = MiConverter.parseIntValue(obj);
	    			pstmt.setInt(iCol, iValue.intValue());		    		
		    	} else if (Types.DECIMAL == dfn.sqlType) {
	    	   		Double dValue = MiConverter.parseDoubleValue(obj);
	    	    	pstmt.setBigDecimal(iCol, BigDecimal.valueOf(dValue));		    		
		    	} else {
	    			pstmt.setString(iCol, (String)obj);		    		
		    	}
		    }
			//logger.info(String.format("** pstmt %d,%s", nColumn, CATEG));
			pstmt.setInt(nColumn, itemId.intValue());
			pstmt.setString(nColumn+1, VALUE_CATEG);
			if (pstmt.executeUpdate() > 0) {
				return Map.of("status", "OK", "op", "update", "itemId", String.valueOf(itemId));
			}
			return Map.of("status", "E", "details", itemId + ": ill-formatted sql");
		} catch (SQLException e) {
			logger.error("updateRecord", e);
			return Map.of("status", "sql", "op", "update", "details", itemId + ": execUpdate");
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
