package com.qingcheng.dao;

import com.qingcheng.pojo.system.Resource;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ResourceMapper extends Mapper<Resource> {

    /**
     * 根据登录名称查询拥有的权限key
     * @param loginName 登录名称
     * @return
     */
    @Select("select tb_resource.res_key " +
            "FROM tb_resource " +
            "where  tb_resource.id in ( " +
            "    select resource_id " +
            "    FROM tb_role_resource " +
            "    WHERE tb_role_resource.role_id IN ( " +
            "        select role_id " +
            "        FROM tb_admin_role " +
            "        WHERE tb_admin_role.admin_id IN ( " +
            "            select tb_admin.id " +
            "            FROM tb_admin " +
            "            WHERE tb_admin.login_name = '${loginName}')))")
    public List<String> selectByResKeyByLoginName(@Param("loginName") String loginName);
}
