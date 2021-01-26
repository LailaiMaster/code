package com.lin.sleeve.service;

import com.lin.sleeve.model.GridCategory;
import com.lin.sleeve.repository.GridCategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/25 16:30
 */
@Service
public class GridCategoryService {

    @Autowired
    private GridCategoryRepository gridCategoryRepository;

    public List<GridCategory> getAll() {
        return gridCategoryRepository.findAll();
    }

}
