package com.lin.sleeve.service;

import com.lin.sleeve.model.SaleExplain;
import com.lin.sleeve.repository.SaleExplainRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleExplainService {

    @Autowired
    private SaleExplainRepository saleExplainRepository;

    public List<SaleExplain> getSaleExplainFixedList() {
        return this.saleExplainRepository.findByFixedOrderByIndex(true);
    }

}
