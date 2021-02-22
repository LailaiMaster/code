package com.lin.sleeve.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/21 13:38
 */
@Entity
@Getter
@Setter
@ToString
public class SaleExplain extends BaseEntity{

    @Id
    private Long id;
    private Boolean fixed;
    private String text;
    private Long spuId;
    private Long index;
    private Long replaceId;

}
