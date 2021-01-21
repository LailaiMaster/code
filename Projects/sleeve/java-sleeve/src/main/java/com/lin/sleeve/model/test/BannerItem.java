package com.lin.sleeve.model.test;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/21 14:49
 */
@Entity
@Getter
@Setter
@ToString
public class BannerItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增，不建议使用 AUTO
    private Long id;

    private Long bannerId;

    private String img;

    /**
     * spu id
     */
    private String keyword;

    /**
     * 跳转类型
     */
    private Short type;

    private String name;

}
