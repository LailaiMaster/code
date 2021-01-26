package com.lin.sleeve.model;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/25 16:28
 */
@Entity
@Getter
@Setter
@ToString
public class GridCategory extends BaseEntity {

    @Id
    private Long id;

    private String title;
    private String img;
    private String name;
    private Integer categoryId;
    private Integer rootCategoryId;

}
