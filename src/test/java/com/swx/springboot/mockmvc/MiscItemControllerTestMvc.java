package com.swx.springboot.mockmvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MiscItemControllerTestMvc {

	@Autowired
	private MockMvc mvc;
	
	@Test
	void doGetOneValue() throws Exception {
		String oneValuePath = "/item/onevalue/101/builtIn";
		
        mvc.perform(get(oneValuePath)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("OK"))
        .andExpect(jsonPath("$.id").value(101))
        .andExpect(jsonPath("$.property").value("builtIn"));

        MvcResult result = mvc.perform(get(oneValuePath)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

        String content = result.getResponse().getContentAsString();
        Map<String, Object> resultMap = new ObjectMapper().readValue(content, new TypeReference<>() {});

        assertEquals(3, resultMap.size());
        assertEquals("OK", resultMap.get("status"));
	}

}
