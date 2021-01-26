package com.lin.sleeve.repository;

import com.lin.sleeve.model.Spu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/22 21:21
 */
public interface SpuRepository extends JpaRepository<Spu, Long> {

    Spu findOneById(Long id);

    Page<Spu> findByCategoryIdOrderByCreateTimeDesc(Long cid, Pageable pageable);

    Page<Spu> findByRootCategoryIdOrderByCreateTime(Long cid, Pageable pageable);

}
