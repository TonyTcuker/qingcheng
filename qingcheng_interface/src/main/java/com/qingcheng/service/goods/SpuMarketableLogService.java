package com.qingcheng.service.goods;

import com.qingcheng.pojo.goods.SpuMarketableLog;

import java.util.List;
import java.util.Map;

/**
 * 商品日志接口
 */
public interface SpuMarketableLogService {

    public SpuMarketableLog findById(String id);

    public List<SpuMarketableLog> search(Map<String,Object> map);

    public void insert(SpuMarketableLog spuMarketableLog);

    public void update(SpuMarketableLog spuMarketableLog);

    public void delete(String id);
}
