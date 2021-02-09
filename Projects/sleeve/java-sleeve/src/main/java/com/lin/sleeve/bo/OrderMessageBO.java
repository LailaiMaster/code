package com.lin.sleeve.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/8 22:13
 */
@Setter
@Getter
@AllArgsConstructor
public class OrderMessageBO {

    private Long orderId;
    private Long userId;
    private Long couponId;

}
