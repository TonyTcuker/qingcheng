package com.qingcheng.service.order;

import com.qingcheng.pojo.order.OrderReport;

import java.util.List;
import java.util.Map;

public interface OrderReportService {

    /**
     * 实现统计指定日期记录，查询（测试使用）
     * @param date 日期
     * @return
     */
    public OrderReport createReport(String date);

    /**
     * 实现日期统计，并且保存到数据库中
     * @param people
     */
    public void insertOrderReport(Integer people);

    /**
     * 实现数据表到聚合统计查询
     * @param date1 起始日期
     * @param date2 结束日期
     * @return
     */
    public Map orderReportCount(String date1,String date2);

    /**
     * 查询指定之间的数据
     * @param date1 起始日期
     * @param date2 结束日期
     * @return
     */
    public List<OrderReport> findDateList(String date1,String date2);
}
