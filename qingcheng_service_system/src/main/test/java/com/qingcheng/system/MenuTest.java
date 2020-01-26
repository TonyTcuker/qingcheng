package com.qingcheng.system;

import com.qingcheng.dao.MenuMapper;
import com.qingcheng.pojo.system.Menu;
import com.qingcheng.service.impl.MenuServiceImpl;
import com.qingcheng.service.system.MenuService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class MenuTest extends CommonTest{

    @Autowired
    private MenuMapper menuMapper ;

    @Autowired
    private MenuServiceImpl menuService ;

    @Test
    public void Menu_selectMenuListByLoginName(){
        List<Menu> menuList = this.menuMapper.selectMenuListByLoginName("admin");
        System.out.println(menuList);
    }

    @Test
    public void Menu_findMenuListByLoginName(){
        List<Map> menuListMap = this.menuService.findMenuListByLoginName("admin");
        System.out.println(menuListMap);
    }
}
