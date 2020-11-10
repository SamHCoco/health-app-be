package com.samhcoco.healthapp.store.service.impl;

import com.samhcoco.healthapp.core.exception.NotFoundException;
import com.samhcoco.healthapp.core.model.Paging;
import com.samhcoco.healthapp.store.model.Product;
import com.samhcoco.healthapp.store.repository.ProductRepository;
import com.samhcoco.healthapp.store.service.ProductService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Page;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public Product getById(@NonNull Long id) {
        val product = productRepository.findById((long) id);
        if (isNull(product)) {
            throw new NotFoundException(format("Product with ID '%s' could not be found.", id));
        }
        return product;
    }

    public Product save(@NonNull Product product) {
        return productRepository.save(product);
    }

    public Product update(@NonNull Product product) {
        if (productRepository.existsById(product.getId())) {
            return productRepository.save(product);
        }
        return null;
    }

    public void delete(@NonNull Long id) {
        productRepository.deleteById(id);
    }

    public Page<Product> findAll(@NonNull Paging paging) {
        Specification<Product> specification = (root, query, builder) -> {return null;}; // todo - define
        return productRepository.findAll(paging.buildRequest(), specification);
    }
}