package com.lin.sleeve.repository;

import com.lin.sleeve.model.Banner;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/21 15:11
 */
public interface BannerRepository extends JpaRepository<Banner, Long> {

    Banner findOneById(Long id);

    Banner findOneByName(String name);

}
