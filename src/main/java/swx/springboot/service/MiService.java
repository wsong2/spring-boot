package swx.springboot.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import swx.springboot.model.MiscItem;
import swx.store.dao.MiscItemDAO;

@Service
public class MiService
{
	@Autowired
	MiscItemDAO dao;
	
	public int addRecord(Map<String, Object> map)
	{
	    Integer ret = dao.addRecord(map);
	    return (ret == null) ? -1 : ret.intValue();
	}
	
	public Map<String, String> updateRecord(Map<String, Object> map)
	{
	    return dao.updateRecord(map);	    
	}
	
	public boolean deleteRecord(int itemId)
	{
		Integer row = dao.deleteRecord(itemId);
		return (row != null && row.intValue() > 0);
	}

	public List<MiscItem> getAll()
	{
		return dao.getAllRecords();
	}	

}
