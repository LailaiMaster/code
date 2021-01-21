package com.lin.sleeve.model.test;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
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
public class Spu {

    @Id
    private Long id;

    private String title;
    private String name;
    private String subTame;

    @ManyToMany(mappedBy = "spuList"/*关系被维护端，通过 mappedBy 指定关系维护段字段。*/)
    private List<Theme> themeList;

}
