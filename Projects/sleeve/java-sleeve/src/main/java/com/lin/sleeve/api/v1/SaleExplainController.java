package com.lin.sleeve.api.v1;

import com.lin.sleeve.exception.http.NotFoundException;
import com.lin.sleeve.model.SaleExplain;
import com.lin.sleeve.service.SaleExplainService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/sale_explain")
@RestController
public class SaleExplainController {

    @Autowired
    private SaleExplainService saleExplainService;

    @GetMapping("/fixed")
    public List<SaleExplain> getFixed() {
        List<SaleExplain> saleExplains = saleExplainService.getSaleExplainFixedList();
        if (saleExplains.isEmpty()) {
            throw new NotFoundException(30011);
        }
        return saleExplains;
    }

}