package com.lin.sleeve.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lin.sleeve.util.SpecListAndJson;

import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Convert;
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
 * Date 2021/1/23 0:24
 */
@Entity
@Getter
@Setter
@ToString
//对所有查询都加上一个条件，应用场景：逻辑删除的数据不应该被查询出来。
@Where(clause = "delete_time is null")
public class Sku extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @JsonIgnore
    public BigDecimal getActualPrice() {
        return discountPrice != null ? discountPrice : price;
    }

    @JsonIgnore
    public List<String> getSpecValueList() {
        System.out.println("getSpecs = " + getSpecs().get(0).getClass());
        return getSpecs().stream().map(Spec::getValue).collect(Collectors.toList());
    }

}
