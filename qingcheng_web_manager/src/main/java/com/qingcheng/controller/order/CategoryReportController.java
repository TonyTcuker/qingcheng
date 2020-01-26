package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.pojo.system.Resource;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categoryReport")
public class CategoryReportController {

    @Reference
    private CategoryReportService categoryReportService ;

    // 针对对昨天到订单进行业务统计
    @GetMapping("/yesterday")
    public List<CategoryReport> yesterday(){
        LocalDate localDate = LocalDate.now(); // 获取当前时间
        localDate.minusDays(1); // 当前时间减去1天
        List<CategoryReport> categoryReportList = this.categoryReportService.categoryReport(localDate);
        return categoryReportList ;
    }

    // 针对指定日期进行订单统计
    @GetMapping("/findDate")
    public List<CategoryReport> findDate(String date){
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")); // 转换成localDate格式
        List<CategoryReport> categoryReportList = this.categoryReportService.categoryReport(localDate); //查询结果
        return categoryReportList;
    }

    // 实现某一个时间段对一级分类对聚合统计
    @GetMapping("/category1Count")
    public List<Map> category1Count(String date1,String date2){
        List<Map> maps = this.categoryReportService.category1Count(date1, date2);
        return maps ;
    }

}
