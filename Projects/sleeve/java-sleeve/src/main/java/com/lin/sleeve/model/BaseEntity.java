package com.lin.sleeve.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/21 23:43
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @JsonIgnore//不序列化到 json 中
    /*
    默认情况下只有数据库不去插入任何值的时候才会让数据库的 CURRENT_TIMESTAMP 生效。
    但其实对于数据库来说， null 值也是值，所以加上下面的注解，在插入和更新的时候，不让模型的值生效。
    */
    @Column(insertable = false, updatable = false)
    private Date createTime;

    @JsonIgnore
    private Date updateTime;

    @JsonIgnore
    private Date deleteTime;

}
