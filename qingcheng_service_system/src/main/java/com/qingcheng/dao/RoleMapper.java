package com.qingcheng.dao;

import com.qingcheng.pojo.system.AdminCombination;
import com.qingcheng.pojo.system.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RoleMapper extends Mapper<Role> {

    /**
     * 通过adminId查询对于的角色
     * @param adminId
     * @return
     */
    @Select("SELECT tar.role_id id,tr.name name,tr.status status , tr.detail detail, tr.create_time createTime " +
            "FROM tb_admin_role tar,tb_role tr " +
            "WHERE tr.id = tar.role_id and tar.admin_id = '${adminId}' ")
    public List<Role> selectByAdminRole(@Param("adminId") Integer adminId);

    /**
     * 根据用角色名称查询用户
     * @param roleName 角色Id
     * @return 返回用户名称
     */
    @Select("SELECT ta.login_name " +
            "FROM tb_role tr ,tb_admin_role tar,tb_admin ta " +
            "WHERE ta.id=tar.admin_id and tr.id = tar.role_id AND tr.name = ${roleId}")
    public List<String> selectByAdmin(@Param("roleId") String roleName);


    /**
     * --- 不再使用
     * 模糊查询指定名称角色，并统计拥有角色该角色的管理员人数,没有角色名称
     * @param roleName 角色名称
     * @return
     */
    @Deprecated
    @Select("SELECT tr.id,tr.name,tr.create_time createTime,tr.status status,tr.detail detail,tb1.countAdmin countAdmin " +
            "FROM tb_role tr,(SELECT tar.role_id,count(tar.admin_id) countAdmin " +
            "                FROM tb_admin_role tar " +
            "                group by tar.role_id ) tb1 " +
            "WHERE tr.id = tb1.role_id AND tr.name LIKE '%${roleName}%' ")
    public List<Role> selectByRoleCount(@Param("roleName") String roleName);


}
