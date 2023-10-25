package com.swx.springboot.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swx.springboot.dao.MiscItemDAO;
import com.swx.springboot.model.MiscItem;

@Service
public class MiService {
	
	@Autowired
	MiscItemDAO dao;
	
	public Map<String, String> addRecord(Map<String, Object> mapIn)
	{
		String itemName = (String)mapIn.get("itemName");
	    if (itemName == null || itemName.isBlank()) {
	    	return Map.of("op","new", "details", "missing item name");
	    }	    
	    Integer ret = dao.addRecord(mapIn);
	    int itemId = (ret == null) ? -1 : ret.intValue();
		if (itemId > 0) {
			return Map.of("status", "OK", "op", "new", "itemId", String.valueOf(itemId));		
		}
		return Map.of("status", "sql", "op", "new");
	}
	
	public Map<String, String> updateRecord(Map<String, Object> map)
	{
	    return dao.updateRecord(map);	    
	}
	
	public Map<String, String> deleteRecord(Integer itemId)
	{
		if (itemId == null) {
			return Map.of("status", "E", "itemId", "Null");			
		}
		Integer row = dao.deleteRecord(itemId);
		if (row != null && row.intValue() > 0) {
			return Map.of("status", "OK", "itemId", String.valueOf(itemId.intValue()));
		}
		return Map.of("status", "sql", "itemId", String.valueOf(itemId));	
	}

	public List<MiscItem> getAll()
	{
		return dao.getAllRecords();
	}	
}
