package com.samhcoco.healthapp.store.repository;

import com.samhcoco.healthapp.store.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>{

    Product findById(long id);

    boolean existsById(Long id);

    List<Product> findAll();

    Page<Product> findAll(Specification specification, Pageable page);

    Page<Product> findAll(Pageable page);

}
