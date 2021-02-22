package com.lin.sleeve.api.v1;

import com.lin.sleeve.dto.TokenDTO;
import com.lin.sleeve.dto.TokenGetDTO;
import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.NotFoundException;
import com.lin.sleeve.service.AuthenticationService;
import com.lin.sleeve.util.JwtToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/26 15:08
 */
@RestController
@RequestMapping("/token")
@Validated
public class TokenController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("")
    public Map<String, String> getToken(@RequestBody @Validated TokenGetDTO tokenGetDTO) {
        System.out.println(tokenGetDTO);
        Map<String, String> map = new HashMap<>();
        switch (tokenGetDTO.getLoginType()) {
            case USER_WX: {
                String session = authenticationService.wxCode2Session(tokenGetDTO.getAccount());
                map.put("token", session);
                break;
            }
            case USER_Email: {

                break;
            }
            default:
                throw new NotFoundException(ExceptionCodes.C_10003);
        }
        return map;
    }

    @PostMapping("/verify")
    public Map<String, Boolean> verify(@RequestBody @Validated TokenDTO tokenDTO) {
        Map<String, Boolean> map = new HashMap<>();
        boolean verified = JwtToken.verifyToken(tokenDTO.getToken());
        map.put("is_valid", verified);
        return map;
    }

}