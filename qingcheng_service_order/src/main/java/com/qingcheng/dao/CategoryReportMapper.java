package com.qingcheng.dao;

import com.qingcheng.pojo.order.CategoryReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Category_report 表的数据访问接口
 */
public interface CategoryReportMapper extends Mapper<CategoryReport> {

    // 在日期筛选日期那个条件不能写死，需要定义拼接sql
    // 没有驼峰命名，需要给列起别名
    @Select("select oi.category_id1 categoryId1,oi.category_id2 categoryId2,oi.category_id3 categoryId3,DATE_FORMAT(o.pay_time,'%Y-%m-%d') countDate ,sum(oi.num) num ,sum(oi.pay_money) money " +
            "FROM tb_order o,tb_order_item oi  " +
            "WHERE o.id = oi.order_id AND o.is_delete='0' AND o.order_status='1' AND DATE_FORMAT(o.pay_time,'%Y-%m-%d')='${date}' " +
            "group BY oi.category_id1,oi.category_id2,oi.category_id3,DATE_FORMAT(o.pay_time,'%Y-%m-%d') ")
    // @Param 是对sql语句中${} 进行传递，需要与${名称}中对名称对应
    // 需要注意到是 # 与 $ , #会自动转换成字符传，而$需要拼接字符串
    public List<CategoryReport> categoryReport(@Param("date") LocalDate date); // 使用localDate类，不能使用localDateTime类


    /**
     * 实现对一级分类对某一个时间段的聚合统计，
     * @param date1 启始日期  数据类型可以为字符串
     * @param date2 结束日期
     * @return 返回结果可以通过Map封装
     */
    @Select("SELECT tcr.category_id1 category_id1,vc.name categoryName,SUM(tcr.num) num,SUM(tcr.money) money " +
            "FROM tb_category_report tcr,v_category1 vc " +
            "WHERE vc.id=tcr.category_id1 AND tcr.count_date>='${date1}' AND tcr.count_date<='${date2}'  " +
            "GROUP BY tcr.category_id1,vc.name ")
    public List<Map> category1Count(@Param("date1") String date1,@Param("date2") String date2);
}
