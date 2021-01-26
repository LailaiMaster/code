package com.lin.sleeve.mockdata;

import com.lin.sleeve.model.Category;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CategoriesAll {

    private List<Category> roots;

    private List<Category> subs;

}