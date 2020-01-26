package com.qingcheng.service.impl;

import com.mysql.cj.util.StringUtils;
import com.qingcheng.dao.SpuMarketableLogMapper;
import com.qingcheng.pojo.goods.SpuMarketableLog;
import com.qingcheng.service.goods.SpuMarketableLogService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import xyz.downgoon.snowflake.Snowflake;

import java.util.List;
import java.util.Map;

public class SpuMarketableLogServiceImpl implements SpuMarketableLogService {

    @Autowired
    private SpuMarketableLogMapper spuMarketableLogMapper;

    @Autowired
    private Snowflake snowflake ;

    @Override
    public SpuMarketableLog findById(String id) {
        SpuMarketableLog spuMarketableLog = this.spuMarketableLogMapper.selectByPrimaryKey(id);
        return spuMarketableLog;
    }

    @Override
    public List<SpuMarketableLog> search(Map<String, Object> map) {
        Example example = this.createExample(map);
        List<SpuMarketableLog> spuMarketableLogList = this.spuMarketableLogMapper.selectByExample(example);

        return spuMarketableLogList;
    }

    @Override
    public void insert(SpuMarketableLog spuMarketableLog) {
        spuMarketableLog.setId(snowflake.nextId()+""); // 设置ID
        this.spuMarketableLogMapper.insert(spuMarketableLog);
    }

    @Override
    public void update(SpuMarketableLog spuMarketableLog) {
        this.spuMarketableLogMapper.updateByPrimaryKeySelective(spuMarketableLog);
    }

    @Override
    public void delete(String id) {
        this.spuMarketableLogMapper.deleteByPrimaryKey(id);
    }

    private Example createExample(Map<String,Object> searchMap){
        Example example = new Example(SpuMarketableLog.class);
        Example.Criteria criteria = example.createCriteria();

        String id = (String) searchMap.get("id");
        if (!StringUtils.isNullOrEmpty(id)){
            criteria.andEqualTo("id",id);
        }

        String spuId = (String) searchMap.get("SpuId");
        if (!StringUtils.isNullOrEmpty(spuId)){
            criteria.andEqualTo("spuId",spuId);
        }

        String rowIsmaketable = (String) searchMap.get("rowIsmaketable");
        if (!StringUtils.isNullOrEmpty(rowIsmaketable)){
            criteria.andEqualTo("rowIsmaketable",rowIsmaketable);
        }

        String updateIsmaketable = (String) searchMap.get("updateIsmaketable");
        if (!StringUtils.isNullOrEmpty(updateIsmaketable)){
            criteria.andEqualTo("updateIsmaketable",updateIsmaketable);
        }
        return example ;
    }
}
