package com.lin.sleeve.model;

import org.hibernate.annotations.Where;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 根据数据表生成实体后，还需要手动做一下微调。
 *
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/21 23:32
 */
@Entity
@Getter
@Setter
@ToString
@Where(clause = "delete_time is null")//对所有查询都加上一个条件，应用场景：逻辑删除的数据不应该被查询出来。
public class Banner extends BaseEntity{

    @Id
    private Long id;

    private String title;
    private String name;
    private String description;

    private String img;

    @OneToMany(fetch = FetchType.LAZY)
    /*BannerItem 的 bannerId 字段*/
    @JoinColumn(name="bannerId")
    private List<BannerItem> items;

}
