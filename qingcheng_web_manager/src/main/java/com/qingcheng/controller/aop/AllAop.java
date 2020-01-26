package com.qingcheng.controller.aop;

import com.qingcheng.controller.util.TrafficStatistics;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 配置动态代理
 */
@Component
@Aspect
public class AllAop {

    private TrafficStatistics trafficStatistics ;

    public AllAop() {
        this.trafficStatistics = TrafficStatistics.getTrafficStatistics();
    }

    /**
     * 实现流量的统计，当Controller被调用就会被触发
     *
     */
    @Around("execution(* com.qingcheng.controller..*.*(..))")
    public Object recodingUV(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        Object proceed = proceedingJoinPoint.proceed(); // 执行方法，并接受方法返回对象


        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();// 获取ServletRequestAttribute
        if (servletRequestAttributes==null){ // 如果为空，则表示不是controller调用，则可以直接返回
            return proceed ;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest(); // 获取request对象
        HttpServletResponse response = servletRequestAttributes.getResponse(); // 获取response对象

        if (request == null || response == null){
            return proceed ;
        }

        this.trafficStatistics.addPvCount(); // 增加一次pv值
            Cookie[] cookies = request.getCookies(); // 获取传递过来的cookies值
        if (cookies==null||cookies.length==0){ // 如果没有cookie表示新用户
            Cookie cookie = new Cookie("isMark", UUID.randomUUID().toString()); // 创建cookies
            cookie.setMaxAge(3600); // 最大一个消失删除
            response.addCookie(cookie); // 保存到response中
            this.trafficStatistics.addUvMap(cookie.getValue(),cookie.getName()); // 增加一个值
        } else {
            boolean mark = false ;
            for (Cookie cookie:cookies){ // 循环cookie
                if (cookie.getName().equals("isMark")){ // 判断是否有cookies的值为isMark
                    this.trafficStatistics.addUvMap(cookie.getValue(),cookie.getName()); // 如果有增加cookie，如果有存放过的cookie则不增加
                    mark = true;
                }
            }
            if (!mark){ // cookie中没有isMark的值，添加cookie
                Cookie cookie = new Cookie("isMark", UUID.randomUUID().toString()); // 创建cookies
                cookie.setMaxAge(3600); // 最大一个消失删除
                response.addCookie(cookie); // 保存到response中
                this.trafficStatistics.addUvMap(cookie.getValue(),cookie.getName()); // 增加一个值
            }
        }

        System.out.println("uv - > " + this.trafficStatistics.getCountUv());
        System.out.println("pv - > " + this.trafficStatistics.getCountPv());

        return  proceed ;
    }
}
