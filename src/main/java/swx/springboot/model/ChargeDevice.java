package swx.springboot.model;

import java.math.BigDecimal;
import java.util.Map;

public class ChargeDevice
{
	private final static String CHARGE_DEVICE_ID = "charge-device-id";
	private final static String DEVICE_REF = "reference";
	private final static String LATITUDE = "latitude";
	private final static String LONGITUDE = "longitude";
	
	private final String deviceId;
	private final String refName;
	private final BigDecimal latitude;
	private final BigDecimal longitude;
	
	public ChargeDevice(String id, String ref, double latitude, double longitude)
	{
		deviceId = id;
		refName = ref;
		this.latitude = BigDecimal.valueOf(latitude);
		this.longitude = BigDecimal.valueOf(longitude);
	}
	
	public static ChargeDevice makeInstance(Map<String, Object> map)
	{
		String id = (String)map.get(CHARGE_DEVICE_ID);
		String ref = (String)map.get(DEVICE_REF);
		Double latitude = (Double)map.get(LATITUDE);
		Double longitude = (Double)map.get(LONGITUDE);
		
    	if (id == null || ref == null || latitude == null || longitude == null)
    	{
			return null;
    	}    	
    	return new ChargeDevice(id, ref, latitude, longitude);     		
	}
	
	public String getDeviceID()
	{
		return deviceId;
	}
	
	public String getName()
	{
		return refName;
	}
	
	public BigDecimal getLatitude()
	{
		return latitude;
	}
	
	public BigDecimal getLongitude()
	{
		return longitude;
	}
	
	public String getStringValue(String columnName)
	{
		switch (columnName)
		{
			case CHARGE_DEVICE_ID: return deviceId;
			case DEVICE_REF:	return refName;
			case LATITUDE:		return latitude.toString();
			case LONGITUDE:		return longitude.toString();
			default: return null;
		}
	}
}
