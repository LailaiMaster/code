package com.lin.sleeve.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/4 14:43
 */
@Getter
@Setter
@ToString
public class OrderAddressDTO {

    private String username;
    private String province;
    private String city;
    private String country;
    private String mobile;
    private String nationalCode;
    private String postalCode;
    private String detail;

}
