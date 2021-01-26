package com.lin.sleeve.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/23 15:33
 */
@Getter
@Setter
@ToString
public class SpuSimplifyVO {

    private Long id;

    private String title;
    private String subtitle;

    private Long categoryId;
    private Long rootCategoryId;

    private Boolean online;

    private Long sketchSpecId;
    private Long defaultSkuId;

    private String img;

    private String price;
    private String discountPrice;
    private String description;

    private String tags;

    private Boolean isTest;

    private String forThemeImg;

}
