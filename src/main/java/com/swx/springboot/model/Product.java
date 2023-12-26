package com.swx.springboot.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {
	
	@JsonProperty("Id")
	private Long id;
	
	@JsonProperty("Pname")
    private String productName;
    
	@JsonProperty("BatchNo")
    private String batchno;
    
	@JsonProperty("Price")
    private double price;
    
	@JsonProperty("NoOfProduct")
    private int noOfProduct;

    public double getPrice() { return price; }
    
	public int getNoOfProduct() { return noOfProduct; }

	public String getProductName() { return productName; }

	public String getBatchNo() { return batchno; }
	
    public Long getId() { return id; }


    public Product() {}
    
    public Product(Long id, String name, String batchno, double price, int noOfProduct) {
        this.id = id;
        this.productName = name;
        this.batchno = batchno;
        this.price = price;
        this.noOfProduct = noOfProduct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product person = (Product) o;
        return Objects.equals(id, person.id) && Objects.equals(productName, person.productName);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (productName != null ? productName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Productn{" + "id=" + id + ", name='" + productName + '\'' + '}';
    }
}
