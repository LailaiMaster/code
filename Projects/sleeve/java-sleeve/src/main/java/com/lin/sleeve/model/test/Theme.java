package com.lin.sleeve.model.test;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/21 17:24
 */
@Entity
@Getter
@Setter
@ToString
public class Theme {

    @Id
    private Long id;

    private String title;
    private String name;

    @ManyToMany
    /*JoinTable 用于规范关系表*/
    @JoinTable(
            name = "theme_spu",
            joinColumns = @JoinColumn(name = "theme_id"),
            inverseJoinColumns = @JoinColumn(name = "spu_id")
    )
    private List<Spu> spuList;

}
