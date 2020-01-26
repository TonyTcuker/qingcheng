package com.qingcheng.pojo.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 保存权限的分类 保存
 */
public class ResourceCombination implements Serializable {

    private Resource headResource ; // 一级分类 parent_id = 0
    private List<Resource> itemResource; // 二级分类

    private List<Integer> selectResourceIds ;// 保存选择的权限

    public ResourceCombination() {
        this.selectResourceIds = new ArrayList<Integer>(); // 初始化选择权限
    }

    public List<Integer> getSelectResourceIds() {
        return selectResourceIds;
    }

    public void setSelectResourceIds(List<Integer> selectResourceIds) {
        this.selectResourceIds = selectResourceIds;
    }

    public Resource getHeadResource() {
        return headResource;
    }

    public void setHeadResource(Resource headResource) {
        this.headResource = headResource;
    }

    public List<Resource> getItemResource() {
        return itemResource;
    }

    public void setItemResource(List<Resource> itemResource) {
        this.itemResource = itemResource;
    }
}
