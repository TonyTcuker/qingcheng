package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.MenuMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.Menu;
import com.qingcheng.service.system.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Menu> findAll() {
        return menuMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Menu> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Menu> menus = (Page<Menu>) menuMapper.selectAll();
        return new PageResult<Menu>(menus.getTotal(),menus.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Menu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return menuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Menu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Menu> menus = (Page<Menu>) menuMapper.selectByExample(example);
        return new PageResult<Menu>(menus.getTotal(),menus.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Menu findById(String id) {
        return menuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param menu
     */
    public void add(Menu menu) {
        menuMapper.insert(menu);
    }

    /**
     * 修改
     * @param menu
     */
    public void update(Menu menu) {
        menuMapper.updateByPrimaryKeySelective(menu);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(String id) {
        menuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 查询所有的Menu
     * @return
     */
    public List<Map> findAllMenu() {
        // 先查询全部的表单内，在内容中进行筛选
        List<Menu> menuList = this.menuMapper.selectAll(); // 查询所有所有菜单

        // 在内存中进行按照指定格式进行筛选
        List<Map> menuMap = findMenuListByParentId(menuList, "0");//parentId = 0 表示一级目录，从以一级目录开始递归

        return menuMap;
    }

    /**
     * 对menuList全部数据进行归类
     * @param menuList 菜单集合
     * @param parentId 上级id
     * @return
     */
    private List<Map> findMenuListByParentId(List<Menu> menuList,String parentId){
        List<Map> mapList = new ArrayList<Map>();


        for (Menu menu:menuList){
            if (menu.getParentId().equals(parentId)){ // 如果当前菜单id等于上级Id，对数据进行保存
                Map map = new HashMap();
                map.put("path",menu.getId()); // 保存菜单Id
                map.put("title",menu.getName()) ;// 保存菜单名称
                map.put("icon",menu.getIcon()) ; // 保存菜单图表

                if (menu.getUrl()!=null){
                    map.put("linkUrl",menu.getUrl()); // 设置url地址
                }

                //	递归下一级目录,传递当前菜单id，查询是否有下一级菜单对象中parentId等于当前Id
                List<Map> menuListByParentList = this.findMenuListByParentId(menuList, menu.getId());
                if (menuListByParentList.size()!=0){ // 说明有下级菜单，否则没有下级菜单
                    map.put("children",menuListByParentList); //保存数据
                }

                mapList.add(map);
            }
        }

        return mapList ; //返回集合
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Menu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 菜单ID
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andLike("id","%"+searchMap.get("id")+"%");
            }
            // 菜单名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 图标
            if(searchMap.get("icon")!=null && !"".equals(searchMap.get("icon"))){
                criteria.andLike("icon","%"+searchMap.get("icon")+"%");
            }
            // URL
            if(searchMap.get("url")!=null && !"".equals(searchMap.get("url"))){
                criteria.andLike("url","%"+searchMap.get("url")+"%");
            }
            // 上级菜单ID
            if(searchMap.get("parentId")!=null && !"".equals(searchMap.get("parentId"))){
                criteria.andLike("parentId","%"+searchMap.get("parentId")+"%");
            }


        }
        return example;
    }

}
