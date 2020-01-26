package com.qingcheng.controller.login;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * 返回当前登录用户名
     * @return
     */
    @GetMapping("/showName")
    public Map showName(){
        SecurityContext context = SecurityContextHolder.getContext(); // 获取Security当前应用上下文
        Authentication authentication = context.getAuthentication(); // 获取authentication对象

        String name = authentication.getName(); // 获取用户名

        Map map = new HashMap();
        map.put("name",name);
        return map;
    }

    /**
     * 测试使用
     * @return
     */
    @GetMapping("/system")
    public Authentication findSystem(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
