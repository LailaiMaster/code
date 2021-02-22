package com.lin.sleeve.repository;

import com.lin.sleeve.model.SaleExplain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleExplainRepository extends JpaRepository<SaleExplain, Long> {

    List<SaleExplain> findByFixedOrderByIndex(Boolean fixed);

}