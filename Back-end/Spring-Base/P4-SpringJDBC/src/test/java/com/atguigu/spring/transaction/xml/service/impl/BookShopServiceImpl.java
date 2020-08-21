package com.atguigu.spring.transaction.xml.service.impl;

import com.atguigu.spring.transaction.xml.BookShopDao;
import com.atguigu.spring.transaction.xml.service.BookShopService;


public class BookShopServiceImpl implements BookShopService {

    private BookShopDao bookShopDao;

    public void setBookShopDao(BookShopDao bookShopDao) {
        this.bookShopDao = bookShopDao;
    }

    @Override
    public void purchase(String username, String isbn) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignore) {
        }

        //1. 获取书的单价
        int price = bookShopDao.findBookPriceByIsbn(isbn);

        //2. 更新数的库存
        bookShopDao.updateBookStock(isbn);

        //3. 更新用户余额
        bookShopDao.updateUserAccount(username, price);
    }

}
