package com.lin.sleeve.service;

import com.github.wxpay.sdk.SleeveWxConfig;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.lin.sleeve.core.LocalUser;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.ForbiddenException;
import com.lin.sleeve.exception.http.NotFoundException;
import com.lin.sleeve.exception.http.ParameterException;
import com.lin.sleeve.exception.http.ServerErrorException;
import com.lin.sleeve.model.Order;
import com.lin.sleeve.repository.OrderRepository;
import com.lin.sleeve.util.CommonUtil;
import com.lin.sleeve.util.HttpRequestProxy;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/8 14:01
 */
@Service
public class WxPaymentService {

    private static final SleeveWxConfig SLEEVE_WX_CONFIG = new SleeveWxConfig();

    @Value("${sleeve.order.pay-callback-host}")
    private String payCallbackHost;

    @Value("${sleeve.order.pay-callback-path}")
    private String payCallbackPath;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 调用微信后台下单，然后生成支持签名。
     */
    public Map<String, String> preOrder(Long oid, HttpServletRequest request) {
        Long uid = LocalUser.getUser().getId();
        Order order = orderRepository.findFirstByUserIdAndId(uid, oid).orElseThrow(() -> new NotFoundException(ExceptionCodes.C_50009));
        /*需要被取消的订单，不应该支付。*/
        if (order.needCancel()) {
            throw new ForbiddenException((ExceptionCodes.C_50010));
        }

        //组装微信 Pay
        WXPay wxPay = assembleWxPayConfig();
        //请求微信服务器进行下单
        Map<String, String> wxOrder;
        try {
            wxOrder = wxPay.unifiedOrder(makePreOrderParams(order.getFinalTotalPrice(), order.getOrderNo(), request));
        } catch (Exception exception) {
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
        //保存微信生成的 prepay_id，如果这这次中断支付，下次调起支付时就不用重新生成了。
        String prepayId = unifiedOrderSuccess(wxOrder);
        orderService.updateOrderPrepayId(order.getId(), prepayId);
        //生成支付前名并返回
        return makePaySignature(wxOrder);
    }

    /**
     * 判断调用微信下单接口是否成功。
     */
    private String unifiedOrderSuccess(Map<String, String> wxOrder) {
        System.out.println("unifiedOrderSuccess result: " + wxOrder);
        if (!wxOrder.get("return_code").equals("SUCCESS") || !wxOrder.get("result_code").equals("SUCCESS")) {
            throw new ParameterException(ExceptionCodes.C_10007);
        }
        return wxOrder.get("prepay_id");
    }

    /**
     * 生成微信支付前名，客户端可用其调起微信支付。
     */
    private Map<String, String> makePaySignature(Map<String, String> wxOrder) {
        //生成前名：sign
        Map<String, String> wxPayMap = new HashMap<>();
        String packages = "prepay_id=" + wxOrder.get("prepay_id");
        wxPayMap.put("appId", SLEEVE_WX_CONFIG.getAppID());
        wxPayMap.put("timeStamp", CommonUtil.timestamp10());
        wxPayMap.put("nonceStr", RandomStringUtils.randomAlphanumeric(32));
        wxPayMap.put("package", packages);
        wxPayMap.put("signType", "HMAC-SHA256");
        String sign;
        try {
            sign = WXPayUtil.generateSignature(wxPayMap, SLEEVE_WX_CONFIG.getKey(), WXPayConstants.SignType.HMACSHA256);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerErrorException(9999);
        }

        //构建参数
        Map<String, String> miniPayParams = new HashMap<>();
        miniPayParams.put("paySign", sign);
        miniPayParams.putAll(wxPayMap);
        miniPayParams.remove("appId");//app id 不需要返回给前端

        //返回参数
        return miniPayParams;
    }

    /**
     * 按照微信官方文档，构建下单参数。
     */
    private Map<String, String> makePreOrderParams(BigDecimal serverFinalPrice, String orderNo, HttpServletRequest request) {
        String payCallbackUrl = this.payCallbackHost + this.payCallbackPath;
        Map<String, String> data = new HashMap<>();

        data.put("body", "Sleeve");
        data.put("out_trade_no", orderNo);
        data.put("device_info", "Sleeve");
        data.put("fee_type", "CNY");
        data.put("trade_type", "JSAPI");

        data.put("total_fee", CommonUtil.yuanToFenPlainString(serverFinalPrice));
        data.put("openid", LocalUser.getUser().getOpenid());
        data.put("spbill_create_ip", HttpRequestProxy.getRemoteRealIp(request));

        data.put("notify_url", payCallbackUrl);

        System.out.println("makePreOrderParams =" + data);

        return data;
    }

    public WXPay assembleWxPayConfig() {
        WXPay wxPay;
        try {
            wxPay = new WXPay(SLEEVE_WX_CONFIG);
        } catch (Exception exception) {
            throw new ServerErrorException(ExceptionCodes.C_9999);
        }
        return wxPay;
    }

}
