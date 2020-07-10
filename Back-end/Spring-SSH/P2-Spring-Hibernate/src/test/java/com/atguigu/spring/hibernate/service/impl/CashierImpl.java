package com.atguigu.spring.hibernate.service.impl;

import com.atguigu.spring.hibernate.service.BookShopService;
import com.atguigu.spring.hibernate.service.Cashier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CashierImpl implements Cashier {

    @Autowired
    private BookShopService bookShopService;

    @Override
    public void checkout(String username, List<String> isbns) {
        for (String isbn : isbns) {
            bookShopService.purchase(username, isbn);
        }
    }

}
