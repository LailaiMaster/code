package com.lin.sleeve.api.v1;

import com.lin.sleeve.core.interceptors.ScopeLevel;
import com.lin.sleeve.lib.SleeveWxNotify;
import com.lin.sleeve.service.WxPaymentNotifyService;
import com.lin.sleeve.service.WxPaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/8 13:59
 */
@RestController/*@RestController = @Controller + @ResponseBody*/
@RequestMapping("/payment")
@Validated
public class PaymentController {

    @Autowired
    private WxPaymentService wxPaymentService;

    @Autowired
    private WxPaymentNotifyService wxPaymentNotifyService;

    @ScopeLevel
    @PostMapping("/pay/order/{id}")
    public Map<String, String> preWxOrder(
            @PathVariable("id") @Positive Long id,
            HttpServletRequest request
    ) {
        return wxPaymentService.preOrder(id, request);
    }

    /**
     * 支付回调，由为微信后台调用。
     * <p>
     * todo：这里有个问题，如果此时延迟消息队列已经将订单取消，那么归还的库存和优惠券如何处理？
     */
    @ScopeLevel
    @PostMapping("/wx/notify")
    public String payCallback(HttpServletRequest request) {
        InputStream inputStream;

        String content = "";
        try {
            inputStream = request.getInputStream();
            content = SleeveWxNotify.readNotify(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.wxPaymentNotifyService.processPayNotify(content);
        } catch (Exception e) {
            return SleeveWxNotify.fail();
        }

        return SleeveWxNotify.success();
    }

}
