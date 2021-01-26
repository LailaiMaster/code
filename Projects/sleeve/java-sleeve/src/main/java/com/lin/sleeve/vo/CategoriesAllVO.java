package com.lin.sleeve.vo;

import com.lin.sleeve.model.Category;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/25 15:00
 */
@Setter
@Getter
@ToString
public class CategoriesAllVO {

    private List<CategoryPureVO> roots;
    private List<CategoryPureVO> subs;

    public CategoriesAllVO() {
    }

    public CategoriesAllVO(Map<Integer, List<Category>> map) {
        this.roots = map.get(1).stream()
                .map(CategoryPureVO::new)
                .collect(Collectors.toList());
        this.subs = map.get(2).stream()
                .map(CategoryPureVO::new)
                .collect(Collectors.toList());
    }

}
