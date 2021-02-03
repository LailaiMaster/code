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

    Optional<User> findByOpenid(String openid);

    User findByEmail(String email);

    User findFirstById(Long id);

    User findByUnifyUid(Long id);

}
