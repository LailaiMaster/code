package com.lin.sleeve.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lin.sleeve.core.enumeration.OrderStatus;
import com.lin.sleeve.dto.OrderAddressDTO;
import com.lin.sleeve.util.CommonUtil;
import com.lin.sleeve.util.OrderAddressAndJson;
import com.lin.sleeve.util.OrderSkuAndJson;

import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/4 14:27
 */
@Entity
@Table(name = "`Order`")
@Getter
@Setter
@ToString
//对所有查询都加上一个条件，应用场景：逻辑删除的数据不应该被查询出来。
@Where(clause = "delete_time is null")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNo;
    private Long userId;

    private Integer totalCount;
    private BigDecimal totalPrice;
    private BigDecimal finalTotalPrice;

    /*这两个字段 sql 中没有，要自己添加。*/
    private Date expiredTime;
    private Date placedTime;

    private String prepayId;

    /**
     * 订单状态
     */
    private Integer status;

    /*
     * 记录的应该是地址的快照信息，而不是原始信息，因为原始信息可能会发生改变
     */
    private String snapImg;
    private String snapTitle;

    @Convert(converter = OrderSkuAndJson.class)
    private List<OrderSku> snapItems;

    @Convert(converter = OrderAddressAndJson.class)
    private OrderAddressDTO snapAddress;

    @JsonIgnore
    public Boolean needCancel() {
        if (!OrderStatus.UNPAID.equals(getStatusEnum())) {
            return true;
        }
        return CommonUtil.isOutOfDate(getExpiredTime());
    }

    public OrderStatus getStatusEnum() {
        return OrderStatus.toType(status);
    }

}
