package com.atguigu.spring.transaction.xml.service.impl;

import com.atguigu.spring.transaction.xml.service.BookShopService;
import com.atguigu.spring.transaction.xml.service.Cashier;

import java.util.List;

public class CashierImpl implements Cashier {

    private BookShopService bookShopService;

    public void setBookShopService(BookShopService bookShopService) {
        this.bookShopService = bookShopService;
    }

    @Override
    public void checkout(String username, List<String> isbns) {
        for (String isbn : isbns) {
            bookShopService.purchase(username, isbn);
        }
    }

}
