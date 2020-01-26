package com.qingcheng.util;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.sun.istack.internal.NotNull;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;


/**
 * WEB层Util工具类
 */
public class WebUtil {

    /**
     * 通过iP获取地域信息
     * @param ip 传递ip地址
     * @return
     */
    public static String getCity(@NotNull String ip){
        String city = "未知";
        if (ip.equals("127.0.0.1")||ip.equals("localhost")||ip.equals("0:0:0:0:0:0:0:1")){
            return "WEB服务器本机ip";
        }
        String encoding = "utf-8" ; // 中文编码格式
        AddressUtils addressUtils = new AddressUtils();
        try {
            city = addressUtils.getAddresses("ip=" + ip, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return city;
    }

    /**
     * 获取浏览器名称
     * @param request
     * @return
     */
    public static String getBrowserName(HttpServletRequest request){
        String header = request.getHeader("User-Agent");
        Browser browser = UserAgent.parseUserAgentString(header).getBrowser(); // 获取浏览器名称
        Version version = browser.getVersion(header); // 获取浏览器版本号

        String browserAndVersion = browser+" / "+version;
        System.out.println(browserAndVersion);

        return browserAndVersion;
    }

    /**
     * 获取用户的真实Ip （端口映射之后不能获取用户ip）
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
