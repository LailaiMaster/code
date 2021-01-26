package com.lin.sleeve.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
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
 * Date 2021/1/22 20:10
 */
@Entity
@Getter
@Setter
@ToString
public class Theme extends BaseEntity {

    @Id
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "theme_spu",
            joinColumns = @JoinColumn(name = "theme_id"),
            inverseJoinColumns = @JoinColumn(name = "spu_id")
    )
    private List<Spu> spuList;

}
