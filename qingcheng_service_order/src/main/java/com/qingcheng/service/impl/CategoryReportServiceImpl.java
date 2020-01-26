package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.CategoryReportMapper;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = CategoryReportService.class)
@Transactional
public class CategoryReportServiceImpl implements CategoryReportService {

    @Autowired
    private CategoryReportMapper categoryReportMapper ;

    /**
     * 生成统计数据
     * @param localDate
     * @return
     */
    @Override
    public List<CategoryReport> categoryReport(LocalDate localDate) {
        List<CategoryReport> categoryReportList = this.categoryReportMapper.categoryReport(localDate);
        return categoryReportList;
    }

    /**
     * 进行统计数据插入
     */
    @Override
    public void createDate() {
        //由于所有的订单支付时间都在 2019-04-15 那天，所以暂时只对那天进行数据统计
//        LocalDate localDate = LocalDate.now(); //获取当前时间
//        localDate.minusDays(1) ;// 获取前一天的时间点
        LocalDate localDate = LocalDate.parse("2019-04-15", DateTimeFormatter.ofPattern("yyyy-MM-dd")); // 将字符转换成localDate

        List<CategoryReport> categoryReportList = this.categoryReportMapper.categoryReport(localDate); // 查询统计数据

        //测试数据，所以修改统计日期
        for (CategoryReport categoryReport:categoryReportList){ // 时间操作注解即可
            categoryReport.setCountDate(new Date()); // 把统计日期改为当天
        }

        if (categoryReportList.size()!=0){ // 判断是否有统计数据

            for (CategoryReport categoryReport :categoryReportList){ // 循环集合
                this.categoryReportMapper.insert(categoryReport); // 插入数据
            }

        } else {
            Log log = LogFactory.getLog(this.getClass());
            log.warn("没有统计数据");
        }
    }

    /**
     * 实现对某一个时间段对一级分类聚合统计
     * @param date1 起始时间
     * @param date2 结束时间
     * @return 返回统计结果，使用Map封装
     */
    @Override
    public List<Map> category1Count(String date1, String date2) {
        List<Map> mapList = this.categoryReportMapper.category1Count(date1, date2); // 调用Mapper
        return mapList;
    }
}
