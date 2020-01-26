package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.controller.util.TrafficStatistics;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.pojo.order.OrderReport;
import com.qingcheng.service.order.CategoryReportService;
import com.qingcheng.service.order.OrderReportService;
import com.qingcheng.service.order.OrderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 订单定时任务
 */
@Component
public class OrderTask {

    // 流量统计类
    private TrafficStatistics trafficStatistics = TrafficStatistics.getTrafficStatistics() ;

    @Reference
    private OrderService orderService;
    /**
     * 订单超时
     */
    @Scheduled(cron = "0 0/2 * * * ?") //表示每分钟两分钟执行一次
    public void OrderTimeOut(){
        System.out.println("执行时间:"+ new SimpleDateFormat("YYYY-MM-dd hh:mm:ss").format(new Date()));
        //暂时不设置订单超时设置
        //this.orderService.TimeOut();
    }

    @Reference
    private CategoryReportService categoryReportService ;

    /**
     * 定时类目统计数据
     */
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点中统计订单类目数据
    public void createCategoryReportData(){
        System.out.println("统计订单类目时间：" + LocalDateTime.now());
        this.categoryReportService.createDate(); // 执行service
    }


    @Reference
    private OrderReportService orderReportService ;
    /**
     * 定时统计订单统计数据
     */
    @Scheduled(cron = "0 0 1 * * ?") // 凌晨1点统计订单数据
    public void createOrderReportData(){
        System.out.println("订单数据统计时间：" + LocalDate.now());
        this.orderReportService.insertOrderReport(this.trafficStatistics.getCountUv()); // 执行统计
    }

}
