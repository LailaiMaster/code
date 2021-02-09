package com.lin.sleeve.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lin.sleeve.dto.TokenGetDTO;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.ParameterException;
import com.lin.sleeve.model.User;
import com.lin.sleeve.repository.UserRepository;
import com.lin.sleeve.util.JwtToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/26 15:38
 */
@Service
@PropertySource({"classpath:config/third.properties", "classpath:config/third-secret.properties"})
public class AuthenticationService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Value("${wx.appid}")
    private String wxAppId;

    @Value("${wx.appsecret}")
    private String wxAppSecret;

    @Value("${wx.code2Session}")
    private String wxCode2SessionURL;

    public String wxCode2Session(String code) {
        String url = MessageFormat.format(wxCode2SessionURL, this.wxAppId, this.wxAppSecret, code);
        RestTemplate template = new RestTemplate();
        String wxResponse = template.getForObject(url, String.class);
        System.out.println("wxResponse = " + wxResponse);
        Map<String, Object> map = new HashMap<>();
        try {
            map = objectMapper.readValue(wxResponse, Map.class);
        } catch (JsonProcessingException e) {
            //todo：接口调用错误处理
            e.printStackTrace();
        }
        return registerUser(map);
    }

    private String registerUser(Map<String, Object> session) {
        String openId = (String) session.get("openid");
        if (StringUtils.isEmpty(openId)) {
            throw new ParameterException(ExceptionCodes.C_20004);
        }
        Optional<User> optionalUser = userRepository.findByOpenid(openId);
        User user;
        if (optionalUser.isPresent()) {
            //返回令牌
            user = optionalUser.get();
        } else {
            user = new User();
            user.setOpenid(openId);
            userRepository.save(user);//保存后会被赋值
        }
        return JwtToken.makeToken(user.getId());
    }

    public void getTokenByEmail(TokenGetDTO userData) {

    }

    public void validateByWx(TokenGetDTO userData) {

    }

    public void register() {

    }

}
