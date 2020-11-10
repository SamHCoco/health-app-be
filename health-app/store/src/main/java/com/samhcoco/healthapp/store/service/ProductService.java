package com.samhcoco.healthapp.store.service;

import com.samhcoco.healthapp.core.model.Paging;
import com.samhcoco.healthapp.store.model.Product;
import org.springframework.data.domain.Page;

public interface ProductService {

    Product getById(Long id);

    Product save(Product product);

    Product update(Product product);

    void delete(Long id);

    Page<Product> findAll(Paging paging);

}
