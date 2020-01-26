package com.tony.test;

import com.qingcheng.pojo.goods.AuditLog;
import com.qingcheng.service.goods.AuditLogService;
import com.qingcheng.service.impl.AuditLogServiceImpl;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringJUnitConfig(locations = "classpath:applicationContext-dao.xml")
public class Test {

    @Autowired
    private AuditLogService auditLogService ;

    @org.junit.Test
    public void selectAuditLog_01(){
        AuditLog auditLog = new AuditLog() ;
        auditLog.setId("4444");
        auditLog.setSpuId("2");
        auditLog.setMessage("测试数据");
        auditLog.setAuditTime(new Date());
        auditLog.setStatus("1");
        this.auditLogService.insert(auditLog);
    }
}
