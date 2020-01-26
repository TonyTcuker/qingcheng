package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.OrderReportMapper;
import com.qingcheng.pojo.order.OrderReport;
import com.qingcheng.service.order.OrderReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderReportService.class)
@Transactional
public class OrderReportServiceImpl implements OrderReportService {

    @Autowired
    private OrderReportMapper orderReportMapper;

    /**
     * 实现统计指定日期记录，查询
     * @param date 日期
     * @return
     */
    @Override
    public OrderReport createReport(String date) {
        OrderReport orderReport = this.orderReportMapper.createReport(date);
        return orderReport;
    }


    /**
     * 实现统计数据的查询与保存,统计日期为昨天
     * @param people 浏览人数 uv
     */
    @Override
    public void insertOrderReport(Integer people) {
        // 测试不用此方法
        //LocalDate localDate = LocalDate.now();
        //localDate = localDate.minusDays(1); // 获取前天的时间点

        // 测试使用，值统计这一天的数据
        String date = "2019-04-15" ;

        OrderReport orderReport = orderReportMapper.createReport(date); // 统计结果
        //orderReport.setCountDate(new SimpleDateFormat("YYYY-MM-dd").parse(date)); // 设置统计日期
        orderReport.setPeople(people); // 设置浏览人数

        this.orderReportMapper.insert(orderReport);
    }

    /**
     * 实现订单数据聚合统计，
     * @param date1 起始日期
     * @param date2 结束日期
     * @return
     */
    @Override
    public Map orderReportCount(String date1, String date2) {
        Map map = this.orderReportMapper.orderReportCount(date1, date2);
        return map;
    }


    /**
     * 查询指定之间的数据
     * @param date1 起始日期
     * @param date2 起始日期
     * @return
     */
    @Override
    public List<OrderReport> findDateList(String date1,String date2) {
        Example example = new Example(OrderReport.class); //  设置查询条件
        Example.Criteria criteria = example.createCriteria();
        criteria.andGreaterThanOrEqualTo("countDate",date1); // 大于等于起始日期
        criteria.andLessThanOrEqualTo("countDate", date2); // 小于等于结束日期

        List<OrderReport> orderReportList = this.orderReportMapper.selectByExample(example);
        return orderReportList ;
    }
}
