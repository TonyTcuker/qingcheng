package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Config;

import java.util.*;

/**
 * config业务逻辑层
 */
public interface ConfigService {


    public List<Config> findAll();


    public PageResult<Config> findPage(int page, int size);


    public List<Config> findList(Map<String,Object> searchMap);


    public PageResult<Config> findPage(Map<String,Object> searchMap,int page, int size);


    public Config findById(String variable);

    public void add(Config config);


    public void update(Config config);


    public void delete(String variable);

}
