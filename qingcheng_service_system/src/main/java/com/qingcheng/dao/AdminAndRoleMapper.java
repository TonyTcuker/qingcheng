package com.qingcheng.dao;

import com.qingcheng.pojo.system.AdminAndRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;


/**
 * 管理与角色表实体类
 */
public interface AdminAndRoleMapper extends Mapper<AdminAndRole> {

    /**
     * 统计指定角色被多少管理员使用
     * @return
     */
    @Select("select count(admin_id)  " +
            "FROM tb_admin_role " +
            "WHERE role_id = ${roleId} ")
    public Integer countAdmin(@Param("roleId") Integer roleId);

}
