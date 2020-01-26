package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.AdminAndRoleMapper;
import com.qingcheng.dao.ResourceMapper;
import com.qingcheng.dao.RoleAndResourceMapper;
import com.qingcheng.dao.RoleMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.*;
import com.qingcheng.service.system.ResourceService;
import com.qingcheng.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.Format;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = RoleService.class)
@Transactional
@Component
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleAndResourceMapper roleAndResourceMapper;

    @Autowired
    private ResourceService resourceService ;

    @Autowired
    private AdminAndRoleMapper adminAndRoleMapper ;

    /**
     * 返回全部记录
     * @return
     */
    public List<Role> findAll() {
        return roleMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Role> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Role> roles = (Page<Role>) roleMapper.selectAll();
        return new PageResult<Role>(roles.getTotal(),roles.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Role> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return roleMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Role> findPage(Map<String, Object> searchMap, int page, int size) {
        // 分页设置
        PageHelper.startPage(page,size);

        // 查询所有人，再通过循环进行统计
        Example example = createExample(searchMap);
        Page<Role> rolePage = (Page<Role>) roleMapper.selectByExample(example); // 查询用户

        List<Role> roleList = rolePage.getResult(); // 取出集合

        for (Role role:roleList){
            Integer integer = this.adminAndRoleMapper.countAdmin(role.getId()); // 统计角色被多少管理员使用
            role.setCountAdmin(integer); // 设置统计人数
        }

        return new PageResult<Role>(rolePage.getTotal(), rolePage.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Role findById(Integer id) {
        return roleMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param role
     */
    public void add(Role role) {

        role.setCreateTime(new Date()); // 设置创建日期

        roleMapper.insert(role);
    }

    /**
     * 修改
     * @param role
     */
    public void update(Role role) {
        roleMapper.updateByPrimaryKeySelective(role);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {

        // 删除管理员与角色中间表数据
        Example adminAndRoleExample = new Example(AdminAndRole.class);
        Example.Criteria adminAndRoleCriteria = adminAndRoleExample.createCriteria();
        adminAndRoleCriteria.andEqualTo("roleId",id);
        this.adminAndRoleMapper.deleteByExample(adminAndRoleExample);


        // 删除角色与权限中间表
        Example RoleAndResourceExample = new Example(RoleAndResource.class);
        Example.Criteria roleAndResourceCriteria = RoleAndResourceExample.createCriteria();
        roleAndResourceCriteria.andEqualTo("roleId",id);
        this.roleAndResourceMapper.deleteByExample(RoleAndResourceExample);

        // 删除role表中记录
        roleMapper.deleteByPrimaryKey(id);

    }


    /**
     * 通过角色id查询角色拥有的权限
     * @param roleId
     * @return
     */
    @Override
    public RoleCombination findRoleResource(Integer roleId) {

        RoleCombination roleCombination = new RoleCombination(); // 创建roleCombination对象

        // 查询角色信息
        Role role = this.roleMapper.selectByPrimaryKey(roleId);
        if(role==null){
            throw new RuntimeException("没有此角色");
        }

        // 获取所有一级二级分类信息
        List<ResourceCombination> resourceCombinationList = this.resourceService.findAll();

        // 查询角色拥有的权限
        List<Integer> resourceIds = this.roleAndResourceMapper.selectRoleId(roleId);


        // 对权限Id进行并且保存到ResourceCombination对象中
        for(ResourceCombination resourceCombination:resourceCombinationList){
            List<Resource> itemResourceList = resourceCombination.getItemResource(); // 取出itemResourceList，二级权限

            // 存放角色拥有权限Id
            List<Integer> selectResourceIds = new ArrayList<Integer>();

            // 循环二级权限
            for (Resource resource:itemResourceList){
                // 判断角色是否有该权限
                for (Integer resourceId:resourceIds){
                    if (resourceId == resource.getId()){
                        selectResourceIds.add(resourceId); // 添加到权限Id对list中
                        break;
                    }

                }
            }

            resourceCombination.setSelectResourceIds(selectResourceIds); // 设置权限ID集合
        }

        // 设置RoleCombination信息
        roleCombination.setResourceCombinationList(resourceCombinationList); // 设置权限集合
        roleCombination.setRoleId(role.getId());
        roleCombination.setRoleName(role.getName());


        return roleCombination;
    }

    /**
     * 用于保存角色于权限的对于关系保存
     * @param roleId 角色Id
     * @param resourceIds 权限id集合
     */
    @Override
    public void saveRoleAndResource(Integer roleId, List<Integer> resourceIds) {

        // 先删除所有roleId对于的权限，之后重新添加
        Example example = new Example(RoleAndResource.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("roleId",roleId);

        this.roleAndResourceMapper.deleteByExample(example);

        // 封装成实体类让后进行数据的保存
        for (Integer resourceId:resourceIds){

            RoleAndResource roleAndResource = new RoleAndResource();
            roleAndResource.setRoleId(roleId);
            roleAndResource.setResourceId(resourceId);

            this.roleAndResourceMapper.insertSelective(roleAndResource);
        }

    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Role.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 角色名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }

            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }

        }
        return example;
    }

}
