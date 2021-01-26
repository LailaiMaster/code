package com.lin.sleeve.service;

import com.lin.sleeve.model.Spu;
import com.lin.sleeve.repository.SpuRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/22 21:21
 */
@Service
public class SpuService {

    @Autowired
    private SpuRepository spuRepository;

    public Spu getSpu(Long id) {
        return spuRepository.findOneById(id);
    }

    public Page<Spu> getLatestSpuList(Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("createTime").descending());
        return spuRepository.findAll(pageable);
    }

    public Page<Spu> getSpuListByCategoryId(Long categoryId, Boolean isRoot, Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("createTime").descending());
        if (isRoot) {
            return spuRepository.findByRootCategoryIdOrderByCreateTime(categoryId, pageable);
        } else {
            return spuRepository.findByCategoryIdOrderByCreateTimeDesc(categoryId, pageable);
        }
    }

}
