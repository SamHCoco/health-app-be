package com.samhcoco.healthapp.store.controller;

import com.samhcoco.healthapp.store.model.Product;
import com.samhcoco.healthapp.store.model.ProductPaging;
import com.samhcoco.healthapp.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("product")
public class ProductController {

    private final ProductService productService;

    @GetMapping("{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @GetMapping("query")
    public Page<Product> query(ProductPaging productPaging) {
        return productService.query(productPaging);
    }

}
