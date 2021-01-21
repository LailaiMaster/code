package com.lin.sleeve.model.test;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/20 18:21
 */
@Entity
@Getter
@Setter
@ToString
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自增，不建议使用 AUTO
    private long id;

    @Column(length = 16)
    private String name;

    private String description;

    private String img;

    private String title;

    @OneToMany
    @JoinColumn(name = "bannerId"/*BannerItem 的 bannerId 字段*/)
    private List<BannerItem> items;

}
