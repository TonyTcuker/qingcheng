package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.LoginLog;
import com.qingcheng.service.system.LoginLogService;
import com.qingcheng.util.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.jws.WebService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * security登录成功处理器
 */
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Reference
    private LoginLogService loginLogService ;

    /**
     * 当用户登录成功将会被调用
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {

        String ip = httpServletRequest.getRemoteAddr(); // 获取Ip地址
        String city = WebUtil.getCity(ip);// 根据获取地区信息
        String browserName = WebUtil.getBrowserName(httpServletRequest);// 获取浏览信息

        LoginLog loginLog = new LoginLog();
        loginLog.setIp(ip); //设置IP地址
        loginLog.setLoginName(authentication.getName()); // 获取用户当登录
        loginLog.setLoginTime(new Date()); // 设置登录时间
        loginLog.setBrowserName(browserName); // 设置浏览器名称
        loginLog.setLocation(city); // 设置登录地区


        this.loginLogService.add(loginLog); // 添加日志
        httpServletRequest.getRequestDispatcher("/main.html").forward(httpServletRequest,httpServletResponse); // 跳著到主页面
    }
}
