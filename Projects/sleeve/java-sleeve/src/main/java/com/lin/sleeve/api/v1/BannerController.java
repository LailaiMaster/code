package com.lin.sleeve.api.v1;

import com.lin.sleeve.dto.PersonDTO;
import com.lin.sleeve.model.test.Banner;
import com.lin.sleeve.service.BannerService;

import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    private BannerService mBannerService;

    @GetMapping("name/{name}")
    public Banner getByName(@PathVariable @NotBlank String name) {
        return mBannerService.getByName(name);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 测试代码
    ///////////////////////////////////////////////////////////////////////////
    /**
     * 如果 "/test/{id}" 中的 id 与参数名 id 不一致，则需要在 PathVariable 中指定参数名为 id。
     */
    @GetMapping("/test/path/{id}")
    public String testPathParams(@PathVariable("id") @Range(min = 0, max = 100) Integer id) {
        System.out.println(id);
        return "你好，testPathParams";
    }

    @PostMapping("/test/request")
    public String testURLParams(@RequestParam Integer age) {
        System.out.println(age);
        return "你好，testURLParams";
    }

    @PostMapping("/test/body")
    public String testBodyParams(@RequestBody @Validated PersonDTO person) {
        System.out.println(person);
        return "你好，testBodyParams";
    }

}
