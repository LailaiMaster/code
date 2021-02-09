package com.github.wxpay.sdk;

import java.io.InputStream;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/8 14:44
 */
public class SleeveWxConfig extends WXPayConfig {

    @Override
    public String getAppID() {
        return "???";
    }

    @Override
    String getMchID() {
        return "???";
    }

    @Override
    public String getKey() {
        return "???";
    }

    /*微信支付不需要配置*/
    @Override
    InputStream getCertStream() {
        return null;
    }

    /*微信支付不需要配置*/
    @Override
    IWXPayDomain getWXPayDomain() {
        return new IWXPayDomain() {
            @Override
            public void report(String domain, long elapsedTimeMillis, Exception ex) {

            }

            @Override
            public DomainInfo getDomain(WXPayConfig config) {
                return new IWXPayDomain.DomainInfo(WXPayConstants.DOMAIN_API, true);
            }
        };
    }

}