package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.service.system.AdminService;
import com.qingcheng.service.system.ResourceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder ;

    @Reference
    private AdminService adminService ;

    @Reference
    private ResourceService resourceService;

    private Log log;

    public UserDetailsServiceImpl() {
        this.log = LogFactory.getLog(UserDetailsServiceImpl.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 校验登录用户
        Admin admin = this.adminService.findByUserName(username);
        if (admin==null){ // 如果admin为空，表示不存在此用户
            this.log.error("没有查询到此用户--"+username);
            return null ;
        }

        // 添加角色
        List<GrantedAuthority> grantedAuthorityList = new ArrayList(); // 创建角色集合
        if (username.equals("admin")){
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_ADMIN"); //创建角色
            grantedAuthorityList.add(grantedAuthority);// 向集合添加角色
        }

        List<String> resKeyList = this.resourceService.findResKeyByLoginName(username); // 查询用户拥有的角色

        for (String resKey:resKeyList){
            grantedAuthorityList.add(new SimpleGrantedAuthority(resKey)); //  想角色集合添加角色
        }

        // 可以通过passwordEncoder接口加密明文密码
        User user = new User(admin.getLoginName(),admin.getPassword(),grantedAuthorityList); // 创建User对象
        return user;
    }
}
