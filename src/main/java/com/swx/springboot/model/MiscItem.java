package com.swx.springboot.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = MiSerializer.class)
public class MiscItem {
	private int itemId;
	private String itemName;
	private LocalDate itemDate;
	private String descr;
	private Integer value1;
	private BigDecimal value2;
	private LocalDateTime dttm;
	private String more;

	public void setItemId(int id) { itemId = id; }
	public void setName(String s) {itemName = s;};
	public void setDescr(String s) {descr = s;};	
	public void setMore(String s) { more = s; }
	
	public void setItemDate(LocalDate dt) { itemDate = dt; }
	public void setDttm(LocalDateTime localDttm) { dttm = localDttm; }
	public void setValue1(Integer i) {value1 = i;}
	public void setValue2(Double d) {value2 = (d==null) ? null : BigDecimal.valueOf(d);}

	public int getItemId() { return itemId; }	
	public String getItemName() { return itemName; }
	public String getDescr() { return descr; }
	public String getMore() { return more; }

	public Integer getValue1() { return value1; }	
	public BigDecimal getValue2() { return value2; }
	public LocalDate getItemDate() { return itemDate; }	
	public LocalDateTime getDttm() { return dttm; }
}
