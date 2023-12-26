package com.swx.springboot.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.swx.springboot.model.Product;

@Service
public class ProdctService implements IProductService {
	
	List<Product> addedProdcuts = new ArrayList<>();
	
	@Override
	public List<Product> findAll() {
			//creating an object of ArrayList  
			List<Product> products = new ArrayList<Product>();  
			//adding products to the List  
			products.add(new Product(100L, "Mobile", "CLK98123", 9000.00, 6));  
			products.add(new Product(101L, "Smart TV", "LGST09167", 60000.00, 3));  
			products.add(new Product(102L, "Washing Machine", "38753BK9", 9000.00, 7));  
			products.add(new Product(103L, "Laptop", "LHP29OCP", 24000.00, 1));  
			products.add(new Product(104L, "Air Conditioner", "ACLG66721", 30000.00, 5));  
			products.add(new Product(105L, "Refrigerator ", "12WP9087", 10000.00, 4));  
			
			products.addAll(addedProdcuts);
			//returns a list of product  
			return products;
		}

	@Override
	public Product saveProduct(Product product) {
		addedProdcuts.add(product);
		return product;
	}

}
