package com.qingcheng.service.goods;

import com.qingcheng.pojo.goods.AuditLog;

import java.util.List;
import java.util.Map;

/**
 * auditLog 业务层接口
 */
public interface AuditLogService {

    public AuditLog findById(String id);

    public List<AuditLog> search(Map<String,String> searchMap);

    public void update(AuditLog auditLog);

    public void delete(String id);

    public void insert(AuditLog auditLog);
}
