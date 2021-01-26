package com.lin.sleeve.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/23 0:24
 */
@Entity
@Getter
@Setter
@ToString
public class SpuDetailImg extends BaseEntity {

    @Id
    private Long id;
    private String img;
    private Long spuId;
    private Long index;

}
