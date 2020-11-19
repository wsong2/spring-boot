package swx.springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import swx.springboot.model.ChargeDevice;
import swx.store.dao.ChargingPointDAO;

@Service
public class ChargingPointService
{
	@Autowired
	ChargingPointDAO dao;
	
	public boolean addRecord(ChargeDevice rec)
	{
		Integer ret = dao.addRecord(rec);
		return (ret > 0);
	}
	
	public boolean deleteRecord(String recId)
	{
		Integer ret = dao.deleteRecord(recId);
		return (ret > 0);
	}

	public String nearestChargingPoint(double latitude, double longiture)
	{	
		List<ChargeDevice> chargingPoints = dao.allRecord();
		if (chargingPoints == null)
		{
			return null;
		}
		if (chargingPoints.isEmpty())
		{
			return "";
		}
		
		double vMin = Double.MAX_VALUE;
		String result = "(none)";
		for (ChargeDevice rec: chargingPoints)
		{
			double latitudeValue = rec.getLatitude().doubleValue();
			double longitureValue = rec.getLongitude().doubleValue();
			double calc = ChargingPointService.distanceCoif(latitudeValue, longitureValue, latitude, longiture);
			if (calc < vMin)
			{
				result = rec.getDeviceID();
				vMin = calc;
			}
		}
		return result;
	}
	
	//
	private static double distanceCoif(double userLat, double userLng, double venueLat, double venueLng)
	{
		double latDistance = Math.toRadians(userLat - venueLat);
		double lngDistance = Math.toRadians(userLng - venueLng);

		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat)) * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

		return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	}
}
