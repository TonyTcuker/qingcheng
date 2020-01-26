package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.AdminAndRoleMapper;
import com.qingcheng.dao.AdminMapper;
import com.qingcheng.dao.LoginLogMapper;
import com.qingcheng.dao.RoleMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.*;
import com.qingcheng.service.system.AdminService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service(interfaceClass = AdminService.class)
@Transactional
@Component
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RoleMapper roleMapper ;

    @Autowired
    private AdminAndRoleMapper adminAndRoleMapper ;

    @Autowired
    private LoginLogMapper loginLogMapper ;

    /**
     * 返回全部记录
     * @return
     */
    public List<Admin> findAll() {
        return adminMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Admin> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Admin> admins = (Page<Admin>) adminMapper.selectAll();
        return new PageResult<Admin>(admins.getTotal(),admins.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Admin> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return adminMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Admin> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Admin> admins = (Page<Admin>) adminMapper.selectByExample(example);
        return new PageResult<Admin>(admins.getTotal(),admins.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Admin findById(Integer id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param admin
     */
    public void add(Admin admin) {
        adminMapper.insert(admin);
    }

    /**
     * 修改
     * @param adminCombination
     */
    @Override
    public void update(AdminCombination adminCombination){


        Admin admin = new Admin();
        admin.setId(adminCombination.getAdminId()); // 设置ID
        admin.setLoginName(adminCombination.getLoginName()); // 设置登录名称
        admin.setStatus(adminCombination.getStatus()); // 设置用户状态

        this.adminMapper.updateByPrimaryKeySelective(admin); // 更新admin数据

        // 设置用户角色，将原有的角色删除，重新添加
        Example adminRoleExample = new Example(AdminAndRole.class);
        Example.Criteria exampleCriteria = adminRoleExample.createCriteria();
        exampleCriteria.andEqualTo("adminId",adminCombination.getAdminId()); //
        this.adminAndRoleMapper.deleteByExample(adminRoleExample); // 删除数据

        List<Role> roleList = adminCombination.getRoleList(); // 取出所有角色
        for (Role role:roleList){ // 迭代角色
            AdminAndRole adminAndRole = new AdminAndRole();
            adminAndRole.setRoleId(role.getId()); // 设置roleId
            adminAndRole.setAdminId(admin.getId()); // 设置admin的Id
            this.adminAndRoleMapper.insert(adminAndRole); // 插入数据
        }


    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        // 创建AdminAndRole的删除条件
        Example roleExample = new Example(AdminAndRole.class);
        Example.Criteria roleCriteria = roleExample.createCriteria();
        roleCriteria.andEqualTo("adminId",id); //删除所有的这个adminId的数据

        this.adminAndRoleMapper.deleteByExample(roleExample); // 删除admin_role表的数据

        this.adminMapper.deleteByPrimaryKey(id); // 删除管理员数据

    }


    /**
     * 实现用户查询
     * @param loginName 用户名
     * @return
     */
    public Admin findByUserName(String loginName) {
        Example example = new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("loginName",loginName); // 查询指定用户名
        criteria.andEqualTo("status","1");// 用户状态必须为启用状态

        List<Admin> adminList = this.adminMapper.selectByExample(example);
        if (adminList.size()==1){ // 如果有结果
            return adminList.get(0); // 返回数组第一位对象
        }

        return null; // 没有查询结果
    }


    /**
     * 实现密码的修改
     * @param loginName 登录名
     * @param rowPassword 原密码
     * @param password 新密码
     */
    @Override
    public void updatePassword(String loginName, String rowPassword, String password) {
        Example example = new Example(Admin.class); // 创建查询条件
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("loginName",loginName); // 传递登录用户名为查询条件

        List<Admin> adminList = this.adminMapper.selectByExample(example); // 查询数据
        if (adminList.size()==0){ // 没有用户
            throw new RuntimeException("没有此用户");
        }
        if (adminList.size()!=1){ // 有重名用户
            throw new RuntimeException("存在同名用户异常");
        }

        Admin admin = adminList.get(0); //取出用户

        if (!BCrypt.checkpw(rowPassword,admin.getPassword())){ // 校验密码
            throw new RuntimeException("错误：原密码不正确");
        }

        if (BCrypt.checkpw(password,admin.getPassword())){ // 修改的密码与原密码相同
            throw new RuntimeException("错误：密码相同");
        }

        String salt = BCrypt.gensalt(); // 获取盐
        String bcryptPassword = BCrypt.hashpw(password, salt); //加密密码

        admin.setPassword(bcryptPassword); // 设置密码

        this.adminMapper.updateByPrimaryKeySelective(admin); //更新数据
    }


    /**
     * 实现管理员创建
     * @param adminCombination
     */
    @Override
    public void createAdmin(AdminCombination adminCombination) {


        List<Role> roleList = adminCombination.getRoleList(); //  取出所有的选择的角色


        String bcryptPassword = BCrypt.hashpw(adminCombination.getPassword(),BCrypt.gensalt()); // 加密密码

        // 查询是否有重名用户
        Example example = new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("loginName",adminCombination.getLoginName());
        if (this.adminMapper.selectByExample(example).size()>0){ // 用户名存在
            throw new RuntimeException("用户名存在");

        }

        //保存Admin信息
        Admin admin = new Admin();
        admin.setLoginName(adminCombination.getLoginName()); // 设置管理员名称
        admin.setStatus(adminCombination.getStatus()); // 设置状态
        admin.setPassword(bcryptPassword); // 设置密码
        admin.setCreateTime(new Date()); // 设置创建时间
        this.adminMapper.insertSelective(admin); // 保存用户


        // 保存用户角色表
        for (Role role:roleList){ // 取出角色ID
            AdminAndRole adminAndRole = new AdminAndRole();
            adminAndRole.setAdminId(admin.getId()); // Admin实体对象添加了@GeneratedValue注解，保存数据后id会自动封装回原对象
            adminAndRole.setRoleId(role.getId()); //保存角色用户
            this.adminAndRoleMapper.insert(adminAndRole); // 保存到用户角色表
        }

    }


    /**
     * 实现数据的查询
     * @return
     */
    @Override
    public List<AdminCombination> findCombinationAll() {
        List<AdminCombination> adminCombinationList = new ArrayList<AdminCombination>(); // 创建集合


        List<Admin> adminList = this.adminMapper.selectAll(); // 查询全部用户
        for (Admin admin:adminList){ // 迭代所有用户

            List<Role> roleList = this.roleMapper.selectByAdminRole(admin.getId()); // 查询用户的拥有的角色


            AdminCombination adminCombination = this.adminMapper.selectLastDate(admin.getLoginName());

            if (adminCombination==null){ // 没有这个用户没有登录过
                adminCombination = new AdminCombination(); // 创建用户
                adminCombination.setLoginName(admin.getLoginName()); // 设置管理员名称
                adminCombination.setCreateTime(admin.getCreateTime()); // 设置创建时间
                adminCombination.setStatus(admin.getStatus()); // 设置管理员状态
            }

            adminCombination.setRoleList(roleList);

            adminCombinationList.add(adminCombination); // 添加到集合中国呢


        }

        return adminCombinationList;
    }


    /**
     * 实现分页查询
     * @param searchMap 查询条件
     * @param page 当前页
     * @param size 页面大小
     * @return
     */
    @Override
    public PageResult<AdminCombination> findPageCombination(Map<String, Object> searchMap, Integer page, Integer size) {

        PageResult<AdminCombination> pageResult = new PageResult(); // 保存返回数据

        List<AdminCombination> adminCombinationList = new ArrayList<AdminCombination>(); // 保存数据集合

        //分页查询
        PageHelper.startPage(page,size); //设置当前页

        Page<Admin> adminPage;//查询结果并返回查询结果集合
        if (searchMap.get("loginName")!=null && !"".equals(searchMap.get("loginName"))){ // 如果查询条件有登录名称，则不查询角色

            Example example = createExample(searchMap);// 创建查询条件
            adminPage = (Page<Admin>) this.adminMapper.selectByExample(example);

        } else if (searchMap.get("roleName")!=null && !"".equals(searchMap.get("roleName"))){ // 没有登录名称，查询角色
            adminPage = (Page<Admin>) this.adminMapper.selectByAdmin(searchMap.get("roleName").toString()); // 根据roleName查询
        } else { // 没有设置查询条件
            adminPage = (Page<Admin>) this.adminMapper.selectAll(); // 查询全部
        }

        List<Admin> adminList = adminPage.getResult(); // 取出集合

        for (Admin admin:adminList){ // 循环查询
            AdminCombination adminCombination = this.adminMapper.selectLastDate(admin.getLoginName());
            if (adminCombination==null){ // 没有这个用户没有登录过
                adminCombination = new AdminCombination(); // 创建用户
                adminCombination.setAdminId(admin.getId()); // 设置用户Id
                adminCombination.setLoginName(admin.getLoginName()); // 设置管理员名称
                adminCombination.setCreateTime(admin.getCreateTime()); // 设置创建时间
                adminCombination.setStatus(admin.getStatus()); // 设置管理员状态
            }

            // 查询角色
            List<Role> roleList = this.roleMapper.selectByAdminRole(admin.getId());
            adminCombination.setRoleList(roleList); // 设置角色


            adminCombinationList.add(adminCombination); // 添加到集合中

        }

        pageResult.setRows(adminCombinationList); // 设置分页查询数据
        pageResult.setTotal(adminPage.getTotal()); // 设置总数量

        return pageResult;
    }

    /**
     * 海鲜管理员
     * @param id
     * @param status
     */
    @Override
    public void updateStatus(Integer id, String status) {
        Admin admin = new Admin();
        admin.setId(id); // 设置管理员Id
        admin.setStatus(status); // 设置状态
        this.adminMapper.updateByPrimaryKeySelective(admin); // 更新数据
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 用户名
            if(searchMap.get("loginName")!=null && !"".equals(searchMap.get("loginName"))){
                criteria.andEqualTo("loginName",searchMap.get("loginName"));
            }
            // 密码
            if(searchMap.get("password")!=null && !"".equals(searchMap.get("password"))){
                criteria.andLike("password","%"+searchMap.get("password")+"%");
            }
            // 状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
            }

            // id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }

        }
        return example;
    }

}
