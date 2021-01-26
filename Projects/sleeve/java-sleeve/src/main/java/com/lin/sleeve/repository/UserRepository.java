package com.lin.sleeve.repository;

import com.lin.sleeve.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/26 17:21
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User>  findByEmail(String email);

    Optional<User> findByOpenid(String openid);

    Optional<User>  findFirstById(Long id);

    Optional<User>  findByUnifyUid(Long id);

}
