package com.lin.sleeve.service;

import com.lin.sleeve.model.Banner;
import com.lin.sleeve.repository.BannerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/20 18:03
 */
@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerRepository mBannerRepository;

    @Override
    public Banner getByName(String name) {
        return mBannerRepository.findOneByName(name);
    }

}
