package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.ConfigMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.config.Config;
import com.qingcheng.service.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private ConfigMapper configMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Config> findAll() {
        return configMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Config> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Config> configs = (Page<Config>) configMapper.selectAll();
        return new PageResult<Config>(configs.getTotal(),configs.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Config> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return configMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Config> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Config> configs = (Page<Config>) configMapper.selectByExample(example);
        return new PageResult<Config>(configs.getTotal(),configs.getResult());
    }

    /**
     * 根据Id查询
     * @param variable
     * @return
     */
    public Config findById(String variable) {
        return configMapper.selectByPrimaryKey(variable);
    }

    /**
     * 新增
     * @param config
     */
    public void add(Config config) {
        configMapper.insert(config);
    }

    /**
     * 修改
     * @param config
     */
    public void update(Config config) {
        configMapper.updateByPrimaryKeySelective(config);
    }

    /**
     *  删除
     * @param variable
     */
    public void delete(String variable) {
        configMapper.deleteByPrimaryKey(variable);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Config.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // variable
            if(searchMap.get("variable")!=null && !"".equals(searchMap.get("variable"))){
                criteria.andLike("variable","%"+searchMap.get("variable")+"%");
            }
            // value
            if(searchMap.get("value")!=null && !"".equals(searchMap.get("value"))){
                criteria.andLike("value","%"+searchMap.get("value")+"%");
            }
            // set_by
            if(searchMap.get("setBy")!=null && !"".equals(searchMap.get("setBy"))){
                criteria.andLike("setBy","%"+searchMap.get("setBy")+"%");
            }


        }
        return example;
    }

}
