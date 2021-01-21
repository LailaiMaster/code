package com.lin.sleeve.model;


import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/21 23:36
 */
@Entity
@Getter
@Setter
@ToString
public class BannerItem extends BaseEntity{

    @Id
    private Long id;

    private String img;
    private String keyword;
    private short type;
    private Long bannerId;
    private String name;

}
