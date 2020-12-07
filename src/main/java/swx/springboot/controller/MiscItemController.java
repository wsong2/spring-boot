package swx.springboot.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import swx.springboot.model.MiscItem;
import swx.springboot.service.MiService;
import swx.springboot.utils.MiConverter;

@Service
@RestController
@RequestMapping("/item")
public class MiscItemController
{
	private static final Logger logger = LoggerFactory.getLogger(MiscItemController.class);
	
	@Autowired
	private MiService svce;
	
	@RequestMapping(value="/form", method=RequestMethod.POST, consumes={ "multipart/form-data" }, 
					produces=MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<MultiValueMap<String, Object>> create(
			@RequestParam("categ") String categ, 
			@RequestParam("value1") Optional<Integer> value1, 
			@RequestParam("value2") Optional<Double> value2, 
			@RequestParam("miChoice") Optional<String> choice, 
			@RequestParam("miMore") Optional<String> miMore,
			@RequestParam("miDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> miDate
	)
	{
		String price = value1.orElse(1) + (value2.isPresent() ? (" for " + value2.get()) : "");
		Map<String, String> map = Map.of(
				"categ", categ, 
				"price", price, 
				"choice", choice.orElse(""),
				"miDate", (miDate.isPresent() ? miDate.get().toString() : ""),
				"miMore", miMore.orElse("- not strict -")
		);
		String json = MiConverter.mapToJson(map);
		MultiValueMap<String, Object> mpr = new LinkedMultiValueMap<String, Object>();
	    var xHeader = new HttpHeaders();
	    xHeader.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<String> xPart = new HttpEntity<String>(json, xHeader);
	    mpr.add("response", xPart);
		return new ResponseEntity<MultiValueMap<String, Object>>(mpr, HttpStatus.OK);
	}

	@RequestMapping(value="/addnew", method=RequestMethod.POST)
	public Map<String, String> create(@RequestBody Map<String, Object> mapIn)
	{
		String itemName = (String)mapIn.get("itemName");
	    if (itemName == null || itemName.isBlank()) {
	    	return Map.of("op","new", "details", "missing item name");
	    }	    
	    var mi = new MiscItem();
	    mi.setProperty("itemName", itemName);
	    mi.setItemDate(MiConverter.parseLocalDate(mapIn.get("itemDate")));
	    mi.setProperty("descr", (String)mapIn.get("descr"));
	    mi.setValue1(MiConverter.parseIntValue(mapIn.get("value1")));
	    mi.setValue2(MiConverter.parseDoubleValue(mapIn.get("value2")));
	    mi.setProperty("more", (String)mapIn.get("more"));
		if (svce.addRecord(mi) > 0) {
			String idStr = String.valueOf(mi.getItemId());
			return Map.of("status", "OK", "op", "new", "itemId", idStr);		
		}
		return Map.of("status", "sql", "op", "new");
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public Map<String, String> update(@RequestBody Map<String, Object> map)
	{
		for (Map.Entry<String, Object> entry: map.entrySet()) {
			if (entry.getKey().isBlank())	continue;
			logger.info("** " + entry.getKey() + ": " + entry.getValue());
		}
    	Integer itemId = MiConverter.parseIntValue(map.get("itemId"));
    	if (itemId == null) {
    		return Map.of("status", "E", "details", "Missing Id");
    	}
	    List<String> props = new ArrayList<>();
	    String[] cols = {"itemName", "descr", "more"};    
	    var mi = new MiscItem();
	    mi.setItemId(itemId);
	    for (String cn: cols) {
		    if (!map.containsKey(cn)) continue;
		    props.add(cn);
		    mi.setProperty(cn, (String)map.get(cn));
	    }
	    if (map.containsKey("itemDate")) {
	    	props.add("itemDate");
	    	mi.setItemDate(MiConverter.parseLocalDate(map.get("itemDate")));
	    }
	    if (map.containsKey("value1")) {
	    	props.add("value1");
	    	mi.setValue1(MiConverter.parseIntValue(map.get("value1")));
	    }
	    if (map.containsKey("value2")) {
	    	props.add("value2");
	    	mi.setValue2(MiConverter.parseDoubleValue(map.get("value2")));
	    }
	    if (props.isEmpty()) {
    		return Map.of("status", "W", "details", "No field");
	    }

	    int retValue = svce.updateRecord(props, mi);
		if (retValue > 0) { // Convention
			return Map.of("status", "OK", "op", "update", "itemId", String.valueOf(itemId));
		} else if (retValue == 0) {
			return Map.of("status", "E", "details", itemId + ": ill-formatted sql");			
		} else {
			return Map.of("status", "sql", "op", "update", "details", itemId + ": execUpdate");
		}
	}
	
	@RequestMapping(value="/del/{id}", method=RequestMethod.DELETE)
	public Map<String, Object> delete(@PathVariable (value = "id") Integer id)
	{
		if (id == null) {
			return Map.of("status", "E", "itemId", "Null");			
		}
		String status = svce.deleteRecord(id.intValue()) ? "OK" : "sql";	
		return Map.of("status", status, "itemId", String.valueOf(id.intValue()));	
	}
	
	@RequestMapping(value="/onevalue/{id}/{property}", method=RequestMethod.GET)
	public Map<String, Object> getOneValue(
		@PathVariable (value = "id") Integer itemId, 
		@PathVariable (value = "property") String property)
	{
		return Map.of("status", "OK", "id", itemId, "property", property);
	}
	
	@RequestMapping(value="/all", method=RequestMethod.GET)
	public List<MiscItem> getAll()
	{
		return svce.getAll();
	}
}
