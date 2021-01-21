package com.lin.sleeve.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

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
    private Date createTime;

    @JsonIgnore
    private Date updateTime;

    @JsonIgnore
    private Date deleteTime;

}
