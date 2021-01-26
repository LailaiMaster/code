package com.lin.sleeve.model;

import com.lin.sleeve.util.SpecListAndJson;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Convert;
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
public class Sku extends BaseEntity {

    @Id
    private Long id;

    private BigDecimal price;
    private BigDecimal discountPrice;

    private Boolean online;

    private String img;
    private String title;

    private Long spuId;

    private Long categoryId;
    private Long rootCategoryId;

    private Long stock;

    private String code;

    @Convert(converter = SpecListAndJson.class)
    private List<Spec> specs;

}
