package swx.store.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	public final static String CATEG = "sp.api";

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Integer addRecord(MiscItem rec)
    {
	    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
	            .withProcedureName("dbo.AddItem")
	            .withoutProcedureColumnMetaDataAccess()
	            .declareParameters(
	                    new SqlParameter("item_name", Types.NVARCHAR),
	                    new SqlParameter("item_date", Types.DATE),
	                    new SqlParameter("categ", Types.NVARCHAR),
	                    new SqlParameter("descr", Types.NVARCHAR),
	                    new SqlParameter("value1", Types.INTEGER),
	                    new SqlParameter("value2", Types.DECIMAL),
	                    new SqlParameter("more", Types.NVARCHAR),
	                    new SqlOutParameter("@item_id", Types.INTEGER)
	                    );

	    SqlParameterSource in = new MapSqlParameterSource()
	    			.addValue("item_name", rec.getItemName())
	    			.addValue("item_date", java.sql.Date.valueOf(rec.getItemDate()))
	    			.addValue("categ", CATEG)
	    			.addValue("descr", rec.getDescr())
	    			.addValue("value1", rec.getValue1())
	    			.addValue("value2", rec.getValue2())
	    			.addValue("more", rec.getMore());

	    Map<String, Object> out = call.execute(in);
	    return (Integer)out.get("@item_id");    	
    }
    
    public int updateRecord(List<String> props, MiscItem rec)
    {
    	final String[] sqlColumn = {"item_name", "item_date", "descr", "valueN1", "valueD1", "[more]"};
    	final List<String> colNames = List.of("itemName", "itemDate", "descr", "value1", "value2", "more");

    	StringBuilder sbColumn = new StringBuilder(100);	    
	    for (int iProp=0; iProp < props.size(); iProp++) {
	    	int iColumn = colNames.indexOf(props.get(iProp));
	    	if (iColumn < 0) {
				logger.error("** Unmatched property: " + props.get(iProp));
	    		return 0;	    	
	    	}
	    	sbColumn.append(',').append(sqlColumn[iColumn]).append("=?");	    	
	    }
	    
	    sbColumn.insert(0, "UPDATE dbo.MiscItems SET dttm2=sysdatetime()");
	    sbColumn.append(" WHERE [item_id]=? AND categ=?");
	    String sql = sbColumn.toString();
		logger.info("SQL stmt: " + sql);
	    
		try (
			Connection conn= jdbcTemplate.getDataSource().getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
		) {
		    for (int iProp=1; iProp <= props.size(); iProp++) {
		    	String cn = props.get(iProp-1);
	    	   	if ("itemName".equals(cn)) {
	        		pstmt.setString(iProp, rec.getItemName());
	    	   	} else if ("itemDate".equals(cn)) {
	        		pstmt.setDate(iProp, java.sql.Date.valueOf(rec.getItemDate()));
	    	   	} else if ("descr".equals(cn)) {
	    			pstmt.setString(iProp, rec.getDescr());
	    	   	} else if ("value1".equals(cn)) {
	    			pstmt.setInt(iProp, rec.getValue1().intValue());
	    	   	} else if ("value2".equals(cn)) {
	    	    	pstmt.setBigDecimal(iProp, rec.getValue2());
	    	   	} else if ("more".equals(cn)) {
	    			pstmt.setString(iProp, rec.getMore());
	        	}   	    		    		    	
		    }
		    int nColumn = props.size()+1;
			//logger.info(String.format("** pstmt %d,%s", nColumn, CATEG));
			pstmt.setInt(nColumn, rec.getItemId());
			pstmt.setString(nColumn+1, CATEG);
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("updateRecord", e);
	    	return -1;
		} 
    }
    
	public Integer deleteRecord(int itemId)
	{
	    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
	    		.withProcedureName("dbo.DeleteItem")
	            .withoutProcedureColumnMetaDataAccess()
	            .declareParameters(
	            	new SqlParameter("item_id", Types.INTEGER),
	            	new SqlParameter("categ", Types.NVARCHAR),
                    new SqlOutParameter("@row", Types.INTEGER)
	            );
	    SqlParameterSource in = new MapSqlParameterSource().addValue("itemId", itemId).addValue("categ", CATEG);
	    Map<String, Object> out = call.execute(in);
	    return (Integer)out.get("@row");    	
	}
	
	public List<MiscItem> getAllRecords()
	{
		try (
			Connection conn= jdbcTemplate.getDataSource().getConnection();
			PreparedStatement pstmt = conn.prepareStatement("{call dbo.GetItem(?,?)}");
		) {
			pstmt.setInt(1, 0);
			pstmt.setString(2, CATEG);
	        List<MiscItem> ret = new ArrayList<>();
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next())
	        {
	        	var mi = new MiscItem();
	        	mi.setItemId(rs.getInt("item_id"));
	        	mi.setProperty("itemName", rs.getString("item_name"));
	        	mi.setProperty("descr", rs.getString("descr"));
	        	mi.setValue1(rs.getInt("value1"));
	        	mi.setValue2(rs.getDouble("value2"));
	        	LocalDateTime dttm = MiConverter.parseDttm(rs.getString("dttmstr"));
	        	if (dttm != null) {
	        		mi.setDttm(dttm);
	        	}
	        	mi.setProperty("more", rs.getString("more"));
	        	java.sql.Date itemDate = rs.getDate("item_date");
	        	if (itemDate != null) {
		        	mi.setItemDate(itemDate.toLocalDate());
	        	}
	        	ret.add(mi);
				//logger.info(String.format("** %1$d, %2$tF", mi.getItemId(), mi.getItemDate()));
	        }
	        return ret;
		} catch (SQLException e) {
			logger.error("getAllRecord", e);
			return null;
		}
	}
	
}
