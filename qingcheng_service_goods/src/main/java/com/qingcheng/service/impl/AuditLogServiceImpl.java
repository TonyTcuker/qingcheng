package com.qingcheng.service.impl;

import com.alibaba.druid.support.spring.stat.SpringStatUtils;
import com.mysql.cj.util.StringUtils;
import com.qingcheng.dao.AuditLogMapper;
import com.qingcheng.pojo.goods.AuditLog;
import com.qingcheng.service.goods.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;


public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogMapper auditLogMapper ;

    @Override
    public AuditLog findById(String id) {
        AuditLog auditLog = this.auditLogMapper.selectByPrimaryKey(id);
        return auditLog ;
    }

    @Override
    public List<AuditLog> search(Map<String, String> searchMap) {
        Example example = createExample(searchMap);
        List<AuditLog> auditLogList = this.auditLogMapper.selectByExample(example);
        return auditLogList;
    }

    @Override
    public void update(AuditLog auditLog) {
        this.auditLogMapper.updateByPrimaryKeySelective(auditLog);
    }

    @Override
    public void delete(String id) {
        this.auditLogMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void insert(AuditLog auditLog) {
        this.auditLogMapper.insertSelective(auditLog);
    }

    private Example createExample(Map<String,String> searchMap){
        Example example = new Example(AuditLog.class);
        Example.Criteria criteria = example.createCriteria();

        String id = searchMap.get("id");
        if (!StringUtils.isNullOrEmpty(id)){
            criteria.andEqualTo("id",id);
        }

        String spuId = searchMap.get("spuId");
        if (!StringUtils.isNullOrEmpty(spuId)){ // 判断是否为空值或者空字符串，是返回true
            criteria.andEqualTo("spuId",spuId);
        }

        String auditTime = searchMap.get("auditTime");
        if (!StringUtils.isNullOrEmpty(auditTime)){
            criteria.andEqualTo("auditTime",auditTime);
        }

        String message = searchMap.get("message");
        if (!StringUtils.isNullOrEmpty(message)){
            criteria.andEqualTo("message",message);
        }

        String status = searchMap.get("status");
        if (!StringUtils.isNullOrEmpty(status)){
            criteria.andEqualTo("status",status);
        }

        return example ;
    }
}
