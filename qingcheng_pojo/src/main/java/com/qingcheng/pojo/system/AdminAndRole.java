package com.qingcheng.pojo.system;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * tb_admin_role 表实体类
 */
@Table(name = "tb_admin_role")
public class AdminAndRole  {

    @Id
    private Integer adminId;

    @Id
    private Integer roleId;

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
