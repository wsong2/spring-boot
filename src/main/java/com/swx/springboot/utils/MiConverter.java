 package com.swx.springboot.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MiConverter {
	private static final Logger logger = LoggerFactory.getLogger(MiConverter.class);
	
	public static final int PARSE_OK = 0;
	public static final int PARSE_ERR = 2;
	public static final int BLANK_INPUT = 1;
	
	public static class DateResult
	{
		public final LocalDate localDate;
		public final int outcome;
		
		private DateResult(LocalDate localDate, int outcome) {
			this.localDate = localDate;
			this.outcome = outcome;
		}
	}
	
	public static Integer parseIntValue(Object val)
	{
		if (val == null) return null;
		if (val instanceof Integer) {
			return (Integer)val;
		}
		String strVal = (String)val;
	    try {
	    	return Integer.parseInt(strVal);
	    }
	    catch (NumberFormatException e) {
            logger.warn("** bad int val: {}", strVal);
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
		if (val == null) {
			return null;
		}
		String strVal = (String)val;
	    try {
	    	return Double.parseDouble(strVal);
	    }
	    catch (NumberFormatException e) {
            logger.warn("** bad double: {}", strVal);
	    	return null;
	    }
	}

	public static DateResult parseLocalDate(Object val)
	{
		if (val instanceof LocalDate) {
			return new DateResult((LocalDate)val, PARSE_OK);
		}
		String dateStr = (String)val;
		if (dateStr == null || dateStr.isBlank()) {	// Browser may submit blank value
			return new DateResult(null, BLANK_INPUT);
		}		
	    try {
	    	LocalDate localDate = LocalDate.parse(dateStr);
	    	return new DateResult(localDate, PARSE_OK);
	    }
	    catch (DateTimeParseException e) {
            logger.warn("** bad LocalDate: {}", dateStr);
			return new DateResult(null, PARSE_ERR);
	    }
	}
	
	public static LocalDateTime parseDttm(Object val)
	{
		if (val == null) return null;
		if (val instanceof LocalDateTime) {
			return (LocalDateTime)val;
		}
		String dttmStr = (String)val;
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
            logger.error("** EX.JsonProcessing: {}", msg);
			return msg;
		}	
	}
	
}
