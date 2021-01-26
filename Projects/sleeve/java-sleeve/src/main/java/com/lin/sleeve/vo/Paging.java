package com.lin.sleeve.vo;

import org.springframework.data.domain.Page;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/23 16:02
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Paging<T> {

    private Long total;
    private Integer count;
    private Integer page;
    private Integer totalPage;

    private List<T> items;

    public Paging(Page<T> page) {
        initPageParameters(page);
        items = page.getContent();
    }

    void initPageParameters(Page<T> page) {
        this.total = page.getTotalElements();
        this.count = page.getSize();
        this.page = page.getNumber();
        this.totalPage = page.getTotalPages();
    }

}
