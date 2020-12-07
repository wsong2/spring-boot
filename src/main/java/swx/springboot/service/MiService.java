package swx.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import swx.springboot.model.MiscItem;
import swx.store.dao.MiscItemDAO;

@Service
public class MiService
{
	@Autowired
	MiscItemDAO dao;
	
	public int addRecord(MiscItem mi)
	{
	    Integer ret = dao.addRecord(mi);
	    if (ret == null) {
	    	return -1;
	    }
	    mi.setItemId(ret.intValue());
	    return mi.getItemId();
	}
	
	public int updateRecord(List<String> props, MiscItem mi)
	{
	    return dao.updateRecord(props, mi);	    
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
