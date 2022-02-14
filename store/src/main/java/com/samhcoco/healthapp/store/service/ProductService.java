package com.samhcoco.healthapp.store.service;

import com.samhcoco.healthapp.store.model.Product;
import com.samhcoco.healthapp.store.model.ProductPage;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Product getById(Long id);

    Product save(Product product);

    Product update(Product product);

    void delete(Long id);

    List<Product> listAll();

    Page<Product> findAll(ProductPage productPage);

    Page<Product> search(ProductPage productPaging);

}
