package com.tony.test;

import com.qingcheng.service.goods.CategoryService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class CategoryTest extends CommonTest{

    @Autowired
    private CategoryService categoryService ;

    @Test
    public void category_findIndexCategory(){
        List<Map> indexCategory = this.categoryService.findIndexCategory();
        System.out.println(indexCategory);
    }
}
