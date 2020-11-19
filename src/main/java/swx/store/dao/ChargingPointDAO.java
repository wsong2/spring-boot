package swx.store.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
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

import swx.springboot.model.ChargeDevice;

@Repository
public class ChargingPointDAO
{
	Logger logger = LoggerFactory.getLogger(ChargingPointDAO.class);
	
    @Autowired
    JdbcTemplate jdbcTemplate;

	public Integer addRecord(ChargeDevice rec)
	{
	    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
	            .withProcedureName("dbo.AddChargingPoint")
	            .withoutProcedureColumnMetaDataAccess()
	            .declareParameters(
	                    new SqlParameter("chargeDeviceID", Types.NVARCHAR),
	                    new SqlParameter("reference", Types.NVARCHAR),
	                    new SqlParameter("longitude", Types.DECIMAL),
	                    new SqlParameter("latitude", Types.DECIMAL),
	                    new SqlOutParameter("@indicator", Types.INTEGER)
	                    );

	    SqlParameterSource in = new MapSqlParameterSource()
	    			.addValue("chargeDeviceID", rec.getDeviceID())
	    			.addValue("reference", rec.getName())
	    			.addValue("longitude", rec.getLongitude())
	    			.addValue("latitude", rec.getLatitude());

	    Map<String, Object> out = call.execute(in);
	    return (Integer)out.get("@indicator");
	}
	
	public Integer deleteRecord(String deviceId)
	{
	    SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
	            .withProcedureName("dbo.DeleteChargingPoint")
	            .withoutProcedureColumnMetaDataAccess()
	            .declareParameters(
	                    new SqlParameter("chargeDeviceID", Types.NVARCHAR),
	                    new SqlOutParameter("@indicator", Types.INTEGER)
	                    );

	    SqlParameterSource in = new MapSqlParameterSource()
	    			.addValue("chargeDeviceID", deviceId);

	    Map<String, Object> out = call.execute(in);
	    return (Integer)out.get("@indicator");
	}
	
	public List<ChargeDevice> allRecord()
	{
		List<ChargeDevice> results = new ArrayList<>();
		try (
			Connection conn= jdbcTemplate.getDataSource().getConnection();
			CallableStatement cstmt = conn.prepareCall("{call dbo.allChargingPoints}");
		) {
	        cstmt.execute();
	        ResultSet rs = cstmt.getResultSet();
	        while (rs.next())
	        {
	        	String id = rs.getString("id");
	        	String ref = rs.getString("ref");
	        	Double latitude = rs.getDouble("latitude");
	        	Double longitude = rs.getDouble("longitude");
	        	results.add(new ChargeDevice(id, ref, latitude, longitude));
	        }
	        rs.close();
	        return results;
		} catch (SQLException e) {
			logger.error("allRecord", e);
			return null;
		}
	}
}
