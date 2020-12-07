package swx.springboot.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiConverter
{
	private static final Logger logger = LoggerFactory.getLogger(MiConverter.class);
	
	public static Integer parseIntValue(Object val)
	{
		if (val instanceof Integer) {
			return (Integer)val;
		}
		String strVal = (String)val;
		if (strVal == null) {
			logger.error("** Integer value conversion: " + val);
			return null;
		}		
	    try {
	    	return Integer.parseInt(strVal);
	    }
	    catch (NumberFormatException e) {
	    	logger.warn("** bad int val: " + strVal);
	    	return null;
	    }
	}

	public static Double parseDoubleValue(Object val)
	{
		if (val instanceof Double) {
			return (Double)val;
		}
		if (val instanceof Integer) {
			return Double.valueOf((Integer)val);
		}
		String strVal = (String)val;
		if (strVal == null) {
			logger.error("** Double value conversion: " + val);
			return null;
		}		
	    try {
	    	return Double.parseDouble(strVal);
	    }
	    catch (NumberFormatException e) {
	    	logger.warn("** bad double: " + strVal);
	    	return null;
	    }
	}

	public static LocalDate parseLocalDate(Object val)
	{
		if (val instanceof LocalDate) {
			return (LocalDate)val;
		}
		String dateStr = (String)val;
		if (dateStr == null) {
			logger.error("** LocalDate conversion: " + val);
			return null;
		}		
	    try {
	    	return LocalDate.parse(dateStr);
	    }
	    catch (DateTimeParseException e) {
	    	logger.warn("** bad LocalDate: " + dateStr);
	    	return null;
	    }
	}
	
	public static LocalDateTime parseDttm(Object val)
	{
		if (val instanceof LocalDateTime) {
			return (LocalDateTime)val;
		}
		String dttmStr = (String)val;
		if (dttmStr == null) {
			logger.error("** LocalDateTime conversion: " + val);
			return null;
		}		
		try {
			String s19 = (dttmStr.length() > 19) ? dttmStr.substring(0, 19) : dttmStr;
			return LocalDateTime.parse(s19);
		} catch (DateTimeParseException e) {
			return null;
		}
	}
	
	public static String mapToJson(Map<String, String> map) {
		var mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			String msg = e.getMessage();
			logger.error("** EX.JsonProcessing: " + msg);
			return msg;
		}	
	}
	
}
