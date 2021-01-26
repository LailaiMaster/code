package com.lin.sleeve.repository;

import com.lin.sleeve.model.Category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/21 15:11
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByIsRootOrderByIndexAsc(Boolean isRoot);

}
