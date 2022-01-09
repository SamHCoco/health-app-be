package com.samhcoco.healthapp.store.service;

import com.samhcoco.healthapp.store.model.Product;
import com.samhcoco.healthapp.store.model.ProductPaging;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Product getById(Long id);

    Product save(Product product);

    Product update(Product product);

    void delete(Long id);

    List<Product> listAll();

    Page<Product> search(ProductPaging productPaging);

}
