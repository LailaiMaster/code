package com.lin.sleeve.vo;

import com.lin.sleeve.model.Category;

import org.springframework.beans.BeanUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/25 15:19
 */
@Setter
@Getter
@ToString
public class CategoryPureVO {

    private Long id;

    private String name;
    private String description;

    private Boolean isRoot;

    private String img;

    private Long parentId;
    private Integer level;

    private Long index;

    private Integer online;

    public CategoryPureVO(Category category) {
        BeanUtils.copyProperties(category, this);
    }

}
