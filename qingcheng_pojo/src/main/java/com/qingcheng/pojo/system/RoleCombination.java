package com.qingcheng.pojo.system;

import java.io.Serializable;
import java.util.List;

/**
 * 用于角色权限的选择
 */
public class RoleCombination implements Serializable {

    private Integer roleId; // 角色id
    private String roleName; // 角色名称

    private List<ResourceCombination> resourceCombinationList; // 角色拥有的子权限


    public List<ResourceCombination> getResourceCombinationList() {
        return resourceCombinationList;
    }

    public void setResourceCombinationList(List<ResourceCombination> resourceCombinationList) {
        this.resourceCombinationList = resourceCombinationList;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}
