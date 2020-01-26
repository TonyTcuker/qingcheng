package com.qingcheng.pojo.goods;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 这是品牌与分类的关系表 实体类
 */
@Table(name = "tb_category_brand")
public class CategoryBrand implements Serializable {

    @Id
    private Integer brandId ;// 品牌ID
    @Id
    private Integer categoryId ; // 分类Id

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
