package com.qingcheng.dao;


import com.qingcheng.pojo.order.OrderReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.Map;

public interface OrderReportMapper extends Mapper<OrderReport> {

    /**
     * 生成统计数据
     * @param date 指定日期数据
     * @return 返沪实体类
     */
    @Select("SELECT '${date}' countDate, row_1.order_num orderNum , row_2.item_num itemNum , row_3.valid_order validOrder , row_4.total_money totalMoney , row_5.return_money returnMoney , row_6.people_order peopleOrder, row_7.people_pay peoplePay, row_8.order_pay orderPay , row_9.item_pay itemPay, row_10.money_pay moneyPay " +
            "FROM (SELECT COUNT(o.id) order_num FROM tb_order o WHERE o.is_delete='0' AND DATE_FORMAT(o.create_time,'%Y-%m-%d')='${date}') row_1, " +
            " (SELECT COUNT(toi.id) item_num FROM tb_order o,tb_order_item toi WHERE o.is_delete='0' AND DATE_FORMAT(o.create_time,'%Y-%m-%d')='${date}' AND o.id = toi.order_id AND toi.is_return != '2') row_2, " +
            " (SELECT COUNT(o.id) valid_order FROM tb_order o WHERE o.is_delete='0' AND DATE_FORMAT(o.create_time,'%Y-%m-%d')='${date}' AND o.order_status in('0','1','2','3')) row_3, " +
            " (SELECT SUM(o.total_money) total_money FROM tb_order o WHERE o.is_delete='0' AND DATE_FORMAT(o.create_time,'%Y-%m-%d')='${date}' AND o.order_status in('0','1','2','3')) row_4, " +
            " (SELECT SUM(tro.return_money) return_money FROM tb_return_order tro WHERE tro.status = '1' AND DATE_FORMAT(tro.apply_time,'%Y-%m-%d')='${date}') row_5, " +
            " (SELECT COUNT(u.username) people_order FROM (SELECT o.username username FROM tb_order o WHERE o.is_delete='0' AND DATE_FORMAT(o.create_time,'%Y-%m-%d')='${date}' GROUP BY o.username ) u ) row_6, " +
            " (SELECT COUNT(u.username) people_pay FROM (SELECT o.username username FROM tb_order o WHERE o.is_delete='0' AND DATE_FORMAT(o.create_time,'%Y-%m-%d')='${date}' AND o.order_status in ('1','2','3') GROUP BY o.username ) u ) row_7, " +
            " (SELECT count(o.id) order_pay FROM tb_order o WHERE o.is_delete='0' AND DATE_FORMAT(o.create_time,'%Y-%m-%d')='${date}' AND o.order_status in ('1','2','3')) row_8, " +
            " (SELECT COUNT(toi.id) item_pay FROM tb_order o , tb_order_item toi WHERE toi.order_id = o.id AND o.is_delete='0' AND DATE_FORMAT(o.create_time,'%Y-%m-%d')='${date}' AND o.order_status!='0' AND toi.is_return != '2') row_9, " +
            " (SELECT SUM(o.pay_money) money_pay FROM tb_order o WHERE o.is_delete='0' AND DATE_FORMAT(o.create_time,'%Y-%m-%d')='${date}' AND o.order_status!='0' ) row_10")
    public OrderReport createReport(@Param("date") String date);

    /**
     * 对数据进行集合统计，需要指定指定时间范围
     * @return 返回一Map集合
     */
    @Select("SELECT SUM(tor.order_num) orderNum , SUM(tor.item_num) itemNUm , SUM(tor.valid_order) validOrder , SUM(tor.total_money) totalMoney , SUM(tor.return_money) returnMoney , SUM(tor.people) people , SUM(tor.people_order) peopleOrder, SUM(tor.people_pay) peoplePay ,SUM(tor.order_pay) orderPay,SUM(tor.item_pay) itemPay,SUM(tor.money_pay) moneyPay " +
            "FROM tb_order_report tor " +
            "WHERE DATE_FORMAT(tor.count_date,'%Y-%m-%d') >= '${date1}' AND DATE_FORMAT(tor.count_date,'%Y-%m-%d') <= '${date2}' ")
    public Map orderReportCount(@Param("date1") String date1,@Param("date2") String date2);
}
