package com.swx.springboot.controller;

import java.time.LocalDate;
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

import com.swx.springboot.model.MiscItem;
import com.swx.springboot.service.MiService;
import com.swx.springboot.utils.MiConverter;

@Service
@RestController
@RequestMapping("/item")
public class MiscItemController {
	
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
	) {
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
		return svce.addRecord(mapIn);
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public Map<String, String> update(@RequestBody Map<String, Object> map)
	{
		for (Map.Entry<String, Object> entry: map.entrySet()) {
			String sKey = entry.getKey();
			if (!sKey.isBlank())	logger.info("** " + sKey + ": " +  entry.getValue());
		}
	    return svce.updateRecord(map);
	}
	
	@RequestMapping(value="/del/{id}", method=RequestMethod.DELETE)
	public Map<String, String> delete(@PathVariable (value = "id") Integer id)
	{
		return svce.deleteRecord(id);	
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
