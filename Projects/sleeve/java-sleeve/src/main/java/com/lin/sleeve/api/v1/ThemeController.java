package com.lin.sleeve.api.v1;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.NotFoundException;
import com.lin.sleeve.model.Theme;
import com.lin.sleeve.service.ThemeService;
import com.lin.sleeve.vo.ThemePureVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/22 20:10
 */
@RestController
@RequestMapping("/theme")
@Validated
public class ThemeController {

    @Autowired
    private ThemeService themeService;

    @GetMapping("/by/names")
    public List<ThemePureVO> getByNames(@RequestParam(name = "names") String names) {
        List<String> params = Arrays.asList(names.split(","));
        List<Theme> themes = themeService.findByNames(params);
        List<ThemePureVO> list = new ArrayList<>();
        themes.forEach(theme -> list.add(DozerBeanMapperBuilder.buildDefault().map(theme, ThemePureVO.class)));
        return list;
    }

    @GetMapping("/name/{name}/with_spu")
    public Theme getThemeByNameWithSpu(@PathVariable(name = "name") String themeName) {
        Optional<Theme> optional = themeService.findByName(themeName);
        return optional.orElseThrow(() -> new NotFoundException(ExceptionCodes.C_30003));
    }

}
