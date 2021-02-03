package com.lin.sleeve.api.v1;

import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.NotFoundException;
import com.lin.sleeve.model.Category;
import com.lin.sleeve.model.GridCategory;
import com.lin.sleeve.service.CategoryService;
import com.lin.sleeve.service.GridCategoryService;
import com.lin.sleeve.vo.CategoriesAllVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/25 14:51
 */
@RestController
@RequestMapping("/category")
@Validated
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private GridCategoryService gridCategoryService;

    @GetMapping("/all")
    public CategoriesAllVO getAll() {
        Map<Integer, List<Category>> all = categoryService.getAll();
        return new CategoriesAllVO(all);
    }

    @GetMapping("/grid/all")
    public   List<GridCategory> getAllGrid() {
        List<GridCategory> gridCategoryList = gridCategoryService.getAll();
        if (gridCategoryList.isEmpty()) {
            throw new NotFoundException(ExceptionCodes.C_30009);
        }
        return gridCategoryList;
    }

}
