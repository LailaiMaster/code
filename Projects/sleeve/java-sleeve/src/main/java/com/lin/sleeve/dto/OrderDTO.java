package com.lin.sleeve.dto;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/4 14:42
 */
@Getter
@Setter
@ToString
public class OrderDTO {

    @DecimalMin(value = "0.00", message = "不合法范围内")
    @DecimalMax(value = "99999999.99", message = "不合法范围内")
    private BigDecimal totalPrice;

    private BigDecimal finalTotalPrice;

    private Long couponId;

    private List<SkuInfoDTO> skuInfoList;

    private OrderAddressDTO orderAddress;

}
