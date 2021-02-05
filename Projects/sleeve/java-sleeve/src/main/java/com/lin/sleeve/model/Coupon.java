package com.lin.sleeve.model;

import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/1 19:01
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
//对所有查询都加上一个条件，应用场景：逻辑删除的数据不应该被查询出来。
@Where(clause = "delete_time is null")
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long activityId;

    /*优惠券的开始时间和结束时间*/
    private Date startTime;
    private Date endTime;
    /*优惠券的有效期：当固定的 startTime 和 endTime 不满足需求时，比如新人券，其开始时间是不固定的。*/
    private Integer valitiy;

    private String title;
    private String description;

    private BigDecimal fullMoney;//满多少减
    private BigDecimal minus;//满减多少
    private BigDecimal rate;//打折
    private Boolean wholeStore;//是否全场券
    /*1. 满减券 2.折扣券 3.无门槛券 4.满金额折扣券*/
    private Integer type;//类型

    private String remark;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "couponList")
    private List<Category> categoryList;

}
