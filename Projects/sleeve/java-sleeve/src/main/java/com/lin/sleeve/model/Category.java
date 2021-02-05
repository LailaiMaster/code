package com.lin.sleeve.model;

import org.hibernate.annotations.Where;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/25 15:01
 */
@Entity
@Getter
@Setter
@ToString
//对所有查询都加上一个条件，应用场景：逻辑删除的数据不应该被查询出来。
@Where(clause = "delete_time is null")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private Boolean isRoot;

    private Long parentId;

    private String img;

    /* index 是保留字段，参考：https://stackoverflow.com/questions/2224503/how-to-map-an-entity-field-whose-name-is-a-reserved-word-in-jpa*/
    @Column(name = "\"index\"")
    private Long index;

    /**优惠券可以只属于某个分类，表示该优惠券只用于该分类下的商品。*/
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "coupon_category",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "coupon_id")
    )
    private List<Coupon> couponList;

}
