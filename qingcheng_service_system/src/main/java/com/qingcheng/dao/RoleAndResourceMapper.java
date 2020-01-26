package com.qingcheng.dao;

import com.qingcheng.pojo.system.RoleAndResource;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * tb_role_resource 数据访问接口
 */
public interface RoleAndResourceMapper extends Mapper<RoleAndResource> {

    /**
     * 查询roleId拥有的resourceId
     * @param roleId
     * @return
     */
    @Select("select resource_id " +
            "FROM tb_role_resource  " +
            "WHERE role_id = ${roleId} ")
    public List<Integer> selectRoleId(@Param("roleId") Integer roleId);

}
