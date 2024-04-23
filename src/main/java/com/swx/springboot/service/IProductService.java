package com.swx.springboot.service;

import java.util.List;

import com.swx.springboot.model.Product;

public interface IProductService {
	List<Product> findAll();
	Product saveProduct(Product product);
	String greet();
}
