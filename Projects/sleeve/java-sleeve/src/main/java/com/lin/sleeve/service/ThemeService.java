package com.lin.sleeve.service;

import com.lin.sleeve.model.Theme;
import com.lin.sleeve.repository.ThemeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/25 16:54
 */
@Service
public class ThemeService {

    @Autowired
    private ThemeRepository themeRepository;

    public List<Theme> findByNames(List<String> params) {
        return themeRepository.findByNames(params);
    }

    public Optional<Theme> findByName(String themeName) {
        return themeRepository.findByName(themeName);
    }

}
