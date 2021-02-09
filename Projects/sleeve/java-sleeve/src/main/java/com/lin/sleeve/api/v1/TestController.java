package com.lin.sleeve.api.v1;

import com.lin.sleeve.dto.PersonDTO;
import com.lin.sleeve.manager.rocketmq.ProducerSchedule;

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

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/9 14:55
 */
@RestController/*@RestController = @Controller + @ResponseBody*/
@RequestMapping("/test")
@Validated
public class TestController {

    @Autowired
    private ProducerSchedule producerSchedule;

    ///////////////////////////////////////////////////////////////////////////
    // 测试代码
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 如果 "/test/{id}" 中的 id 与参数名 id 不一致，则需要在 PathVariable 中指定参数名为 id。
     */
    @GetMapping("/path/{id}")
    public String testPathParams(@PathVariable("id") @Range(min = 0, max = 100) Integer id) {
        System.out.println(id);
        return "你好，testPathParams";
    }

    @PostMapping("/request")
    public String testURLParams(@RequestParam Integer age) {
        System.out.println(age);
        return "你好，testURLParams";
    }

    @PostMapping("/body")
    public String testBodyParams(@RequestBody @Validated PersonDTO person) {
        System.out.println(person);
        return "你好，testBodyParams";
    }

    @PostMapping("/rocketmq/send")
    public String sendRocketMQ() throws Exception {
        producerSchedule.send("aaa", "bbb");
        return "你好，sendRocketMQ";
    }

}
