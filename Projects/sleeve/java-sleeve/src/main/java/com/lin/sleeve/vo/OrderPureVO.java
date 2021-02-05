package com.lin.sleeve.vo;

import com.lin.sleeve.model.Order;

import org.springframework.beans.BeanUtils;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/6 1:25
 */
@Getter
@Setter
@ToString
public class OrderPureVO extends Order {

    private Long period;
    private Date createTime;

    public OrderPureVO(Order order, Long period) {
        BeanUtils.copyProperties(order, this);
        this.period = period;
    }

}

