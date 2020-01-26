package com.qingcheng.dao;

import com.qingcheng.pojo.system.Admin;
import com.qingcheng.pojo.system.AdminCombination;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface AdminMapper extends Mapper<Admin> {


    /**
     * 可以直接查询用户最后登录日期以及用户名称
     * @param LoginName
     * @return
     */
    @Select("select ta.id adminId, ta.login_name loginName,ta.status status,ta.create_time createTime,tll.login_time lastTime " +
            "FROM tb_admin ta,tb_login_log tll " +
            "WHERE ta.login_name = tll.login_name AND ta.login_name='${loginName}' " +
            "order by tll.login_time desc limit 1;")
    public AdminCombination selectLastDate(@Param("loginName") String LoginName);

    /**
     * 根据用角色查询用户
     * @param roleName 角色Id
     * @return 返回Admin对象
     */
    @Select("SELECT ta.id id,ta.login_name loginName,ta.status status,ta.create_time createTime " +
            "FROM tb_role tr ,tb_admin_role tar,tb_admin ta " +
            "WHERE ta.id=tar.admin_id and tr.id = tar.role_id AND tr.name = '${roleName}' ")
    public List<Admin> selectByAdmin(@Param("roleName") String roleName);
}
