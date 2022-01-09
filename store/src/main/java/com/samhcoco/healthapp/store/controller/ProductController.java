package com.samhcoco.healthapp.store.controller;

import com.samhcoco.healthapp.store.model.Product;
import com.samhcoco.healthapp.store.model.ProductPaging;
import com.samhcoco.healthapp.store.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@EnableSwagger2
@RequiredArgsConstructor
@RestController
public class ProductController {

    private static final String V1 = "api/v1/product";

    private final ProductService productService;

    @GetMapping(V1 + "/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @GetMapping(V1 + "/query")
    public Page<Product> query(ProductPaging productPaging) {
        return productService.search(productPaging);
    }

    @GetMapping(V1 + "/all")
    public ResponseEntity<List<Product>> listAll() {
        try {
            return new ResponseEntity<>(productService.listAll(), OK);
        } catch (Exception e) {
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

}
