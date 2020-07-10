package com.atguigu.service;

import com.atguigu.bean.Book;
import com.atguigu.bean.Page;

import java.util.List;

public interface BookService {

    // 增
    public boolean save(Book book) throws Exception;

    // 删
    public boolean deleteById(int id) throws Exception;

    // 改
    public boolean update(Book book) throws Exception;

    // 查
    public List<Book> queryAllBook() throws Exception;

    public Book findBookById(int id) throws Exception;

    public Page<Book> page(long pageNo, long pageSize) throws Exception;

    public Page<Book> pageByPrice(long pageNo, long pageSize, double min, double max) throws Exception;

}
