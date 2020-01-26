package com.qingcheng.system;

import com.qingcheng.dao.AdminMapper;
import com.qingcheng.dao.RoleMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.pojo.system.AdminCombination;
import com.qingcheng.pojo.system.Role;
import com.qingcheng.service.impl.AdminServiceImpl;
import com.qingcheng.service.impl.RoleServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;

public class AdminDaoTest extends CommonTest{

    @Autowired
    private AdminMapper adminMapper ;

    @Autowired
    private AdminServiceImpl adminService ;

    @Autowired
    private RoleMapper roleMapper ;


    @Test
    public void Admin_SelectLastDate(){
        System.out.println(adminMapper);
        AdminCombination jimiMap = this.adminMapper.selectLastDate("吉米");
        System.out.println(jimiMap);
    }

    @Test
    public void Role_SelectByAdminRole(){
        List<Role> role = this.roleMapper.selectByAdminRole(6);
        System.out.println(role);
    }

    @Test
    public void AdminService_selectAll(){
        List<AdminCombination> selectAll = this.adminService.findCombinationAll();
        System.out.println(selectAll);
    }

    @Test
    public void AdminService_findPageCombination(){
        HashMap<String, Object> search = new HashMap<String, Object>();
        search.put("roleName","商品管理员");

        PageResult<AdminCombination> pageCombination = this.adminService.findPageCombination(search, 1, 3);

        System.out.println(pageCombination);
    }

    @Test
    public void AdminService_updateStatus(){
        this.adminService.updateStatus(6,"0");
    }
}
