package com.lin.sleeve.service;


import com.lin.sleeve.model.User;
import com.lin.sleeve.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/3 20:08
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findFirstById(id);
    }

}

