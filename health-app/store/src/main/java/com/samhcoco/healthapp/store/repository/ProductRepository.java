package com.samhcoco.healthapp.store.repository;

import com.samhcoco.healthapp.store.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>{

    Product findById(long id);

    boolean existsById(Long id);

    Page<Product> findAll(Pageable pageable, Specification<Product> specification);

}
