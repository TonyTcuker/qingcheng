package com.qingcheng.service.order;

import com.qingcheng.pojo.order.CategoryReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * CategoryReport 业务层
 */
public interface CategoryReportService {

    public List<CategoryReport> categoryReport(LocalDate localDate);

    public void createDate();

    public List<Map> category1Count(String date1,String date2);
}
