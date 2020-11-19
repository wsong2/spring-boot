package swx.springboot.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import swx.springboot.model.ChargeDevice;
import swx.springboot.service.ChargingPointService;

@Service
@RestController
@RequestMapping("/chargingpoint")
public class ChargingPointController
{
	@Autowired
	private ChargingPointService svce;
	
	@RequestMapping(value="/addnew", method=RequestMethod.POST)
	public Map<String, Object> create(@RequestBody Map<String, Object> map)
	{
		HashMap<String, Object> resp = new HashMap<>();
    	ChargeDevice rec = ChargeDevice.makeInstance(map);
    	if (rec == null) {
    		resp.put("status", "error: Invalid content for charging point.");
    	} else if (svce.addRecord(rec)) {
    		resp.put("status", "OK");
    		resp.put("charging-point", rec.getName());
    	} else {
    		resp.put("status", "info: Chargining point exists already.");    		
    		resp.put("charging-point", rec.getName());
    	}
		return resp;
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
	public Map<String, Object> delete(@PathVariable String id)
	{
		HashMap<String, Object> resp = new HashMap<>();
    	if (svce.deleteRecord(id))
    	{
    		resp.put("status", "DELETED");
    	}
    	else
    	{
    		resp.put("status", "info: deletion operated.");    		
    	}
		resp.put("charging-point-id", id);
		return resp;
	}
	
	@RequestMapping(value="/nearest/{latitude}/{longitude}", method=RequestMethod.GET)
	public Map<String, Object> getNearest(
				@PathVariable (value = "latitude") String latitude, 
				@PathVariable (value = "longitude") String longitude)
	{
		HashMap<String, Object> resp = new HashMap<>();
		try
		{
			double dLatitude = Double.parseDouble(latitude);
			double dLongitude = Double.parseDouble(longitude);
			String devId = svce.nearestChargingPoint(dLatitude, dLongitude);
			if (devId == null)
			{
				resp.put("status", "error: query on charging points");				
			}
			else if (devId == "")
			{
				resp.put("status", "warning: no charging point found");								
			}
			else
			{
	    		resp.put("status", "OK");
	    		resp.put("charging-point-id", devId);			
			}
		}
		catch (NumberFormatException | NullPointerException e)
		{
			resp.put("status", "error: Invalid latitude or longitude value");
		}
		return resp;
	}
}
