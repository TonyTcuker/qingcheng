package com.qingcheng.dao;

import com.qingcheng.pojo.system.Menu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MenuMapper extends Mapper<Menu> {

    /**
     * 根据用户名查询对应的菜单
     * @param loginName 用户名
     * @return
     */
    @Select("SELECT id,name,icon,url,parent_id parentId " +
            "FROM tb_menu " +
            "WHERE id IN ( " +
            "    SELECT menu_id " +
            "    FROM tb_resource_menu " +
            "    WHERE resource_id IN ( " +
            "        select resource_id " +
            "        FROM tb_role_resource " +
            "        WHERE tb_role_resource.role_id IN ( " +
            "            select role_id " +
            "            FROM tb_admin_role " +
            "            WHERE tb_admin_role.admin_id IN ( " +
            "                select tb_admin.id " +
            "                FROM tb_admin " +
            "                WHERE tb_admin.login_name = '${loginName}')) " +
            "    ) " +
            ") " +
            "UNION " +
            "SELECT * " +
            "FROM tb_menu " +
            "WHERE id IN( " +
            "    SELECT parent_id " +
            "    FROM tb_menu " +
            "    WHERE id IN ( " +
            "        SELECT menu_id " +
            "        FROM tb_resource_menu " +
            "        WHERE resource_id IN ( " +
            "            select resource_id " +
            "            FROM tb_role_resource " +
            "            WHERE tb_role_resource.role_id IN ( " +
            "                select role_id " +
            "                FROM tb_admin_role " +
            "                WHERE tb_admin_role.admin_id IN ( " +
            "                    select tb_admin.id " +
            "                    FROM tb_admin " +
            "                    WHERE tb_admin.login_name = '${loginName}')) " +
            "        ) " +
            "    ) " +
            ") " +
            "UNION " +
            "select * " +
            "FROM tb_menu " +
            "WHERE id IN ( " +
            "    SELECT parent_id " +
            "    FROM tb_menu " +
            "    WHERE id IN( " +
            "        SELECT parent_id " +
            "        FROM tb_menu " +
            "        WHERE id IN ( " +
            "            SELECT menu_id " +
            "            FROM tb_resource_menu " +
            "            WHERE resource_id IN ( " +
            "                select resource_id " +
            "                FROM tb_role_resource " +
            "                WHERE tb_role_resource.role_id IN ( " +
            "                    select role_id " +
            "                    FROM tb_admin_role " +
            "                    WHERE tb_admin_role.admin_id IN ( " +
            "                        select tb_admin.id " +
            "                        FROM tb_admin " +
            "                        WHERE tb_admin.login_name = '${loginName}')) " +
            "            ) " +
            "        ) " +
            "    ) " +
            ")")
    public List<Menu> selectMenuListByLoginName(@Param("loginName") String loginName);
}
