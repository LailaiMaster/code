package com.lin.sleeve.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/25 17:05
 */
@Getter
@Setter
@ToString
public class ThemePureVO {

    private Long id;

    private String title;
    private String name;
    private String description;

    private String entranceImg;
    private String internalTopImg;

    private String titleImg;

    private String extend;

    private String tplName;

    private Boolean online;

}
