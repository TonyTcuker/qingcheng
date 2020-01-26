package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.controller.util.TrafficStatistics;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.order.OrderReport;
import com.qingcheng.service.order.OrderReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orderReport")
public class OrderReportController {

    @Reference
    private OrderReportService orderReportService ;

    @GetMapping("/findCount")
    public OrderReport findCount(String date){
        OrderReport orderReport = this.orderReportService.createReport(date);
        return orderReport;
    }

    @GetMapping("/Report") //手动统计
    public Result ReportOrder(){
        Integer uv = TrafficStatistics.getTrafficStatistics().getCountUv();
        this.orderReportService.insertOrderReport(uv);
        return new Result();
    }

    /**
     * 实现指定时间范围对订单数据对统计聚合
     * @param date1 起始日期
     * @param date2 结束日期
     * @return 返回Map集合
     */
    @GetMapping("/orderReportCount")
    public Map orderReportCount(String date1,String date2){
        Map map = this.orderReportService.orderReportCount(date1, date2);
        return map ;
    }

    /**
     * 查询指定日期到今天到数据
     * @param date 起始日期
     * @return 返回List集合
     */
    @GetMapping("/findDateToday")
    public List<OrderReport> findDateToday(String date){
        List<OrderReport> dateList = this.orderReportService.findDateList(date, LocalDate.now().toString());// 传递起始日期，和当前时间
        return dateList ;
    }
}
