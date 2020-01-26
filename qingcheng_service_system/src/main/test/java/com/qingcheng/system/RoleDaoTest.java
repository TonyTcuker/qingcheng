package com.qingcheng.system;

import com.qingcheng.dao.RoleAndResourceMapper;
import com.qingcheng.dao.RoleMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.Role;
import com.qingcheng.pojo.system.RoleCombination;
import com.qingcheng.service.impl.RoleServiceImpl;
import com.qingcheng.service.system.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.Table;
import java.util.HashMap;
import java.util.List;

public class RoleDaoTest extends CommonTest{

    @Autowired
    private RoleServiceImpl roleService ;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleAndResourceMapper roleAndResourceMapper;

    @Test
    public void Role_findPage(){
        HashMap<String, Object> search = new HashMap<String, Object>();
        PageResult<Role> page = this.roleService.findPage(search, 1, 20);
        System.out.println(page);
    }

    @Test
    public void RoleAndResource_findRoleId(){
        List<Integer> roleId = this.roleAndResourceMapper.selectRoleId(1);
        System.out.println(roleId);
    }

    @Test
    public void Role_findRoleResource(){
        RoleCombination roleResource = this.roleService.findRoleResource(1);
        System.out.println(roleResource);
    }

}
