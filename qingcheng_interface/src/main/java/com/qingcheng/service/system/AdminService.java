package com.qingcheng.service.system;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.pojo.system.AdminAndRole;
import com.qingcheng.pojo.system.AdminCombination;

import java.util.*;

/**
 * admin业务逻辑层
 */
public interface AdminService {


    public List<Admin> findAll();


    public PageResult<Admin> findPage(int page, int size);


    public List<Admin> findList(Map<String,Object> searchMap);


    public PageResult<Admin> findPage(Map<String,Object> searchMap,int page, int size);


    public Admin findById(Integer id);

    public void add(Admin admin);


    public void update(AdminCombination adminCombination);


    public void delete(Integer id);

    public Admin findByUserName(String loginName);

    public void updatePassword(String loginName,String rowPassword,String password);

    public void createAdmin(AdminCombination adminCombination);

    public List<AdminCombination> findCombinationAll();

    public PageResult<AdminCombination> findPageCombination(Map<String,Object> searchMap,Integer page,Integer size);

    public void updateStatus(Integer id,String status);


}
