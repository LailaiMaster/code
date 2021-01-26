package com.lin.sleeve.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/25 15:01
 */
@Entity
@Getter
@Setter
@ToString
public class Category extends BaseEntity {

    @Id
    private Long id;

    private String name;

    private String description;

    private Boolean isRoot;

    private Long parentId;

    private String img;

    /* index 是保留字段，参考：https://stackoverflow.com/questions/2224503/how-to-map-an-entity-field-whose-name-is-a-reserved-word-in-jpa*/
    @Column(name = "\"index\"")
    private Long index;

}
