package com.lin.sleeve.api.v1;

import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.NotFoundException;
import com.lin.sleeve.model.Banner;
import com.lin.sleeve.service.BannerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;


/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/15 21:24
 */
//@Controller
//@ResponseBody/*ResponseBody 表示返回的数据以响应体的方式返回，加来类上，就表示所有方法都是这中处理方式*/
@RestController/*@RestController = @Controller + @ResponseBody*/
@RequestMapping("/banner")
@Validated
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("/name/{name}")
    public Banner getByName(@PathVariable @NotBlank String name) {
        Banner banner = bannerService.getByName(name);
        if (banner == null) {
            throw new NotFoundException(ExceptionCodes.C_30005);
        }
        return banner;
    }

}
