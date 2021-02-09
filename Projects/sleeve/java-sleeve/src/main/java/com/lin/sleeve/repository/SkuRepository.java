package com.lin.sleeve.repository;

import com.lin.sleeve.model.Sku;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/22 21:21
 */
public interface SkuRepository extends JpaRepository<Sku, Long> {

    List<Sku> findAllByIdIn(List<Long> idList);

    /*一条语句实现：查询、对比、修改。*/
    @Modifying
    @Query("update Sku s \n" +
            "set s.stock = s.stock - :quantity\n" +
            "where s.id = :sid\n" +
            "and s.stock >= :quantity")
    int reduceStock(@Param("sid") Long sid, @Param("quantity") Long quantity);

    @Modifying
    @Query("update Sku s set s.stock = s.stock + (:quantity) where s.id = :sid")
    int recoverStock(@Param("sid") Long sid, @Param("quantity") Integer quantity);

}
