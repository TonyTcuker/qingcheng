package com.qingcheng.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.system.Role;
import com.qingcheng.pojo.system.RoleCombination;
import com.qingcheng.service.system.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Reference
    private RoleService roleService;

    @GetMapping("/findAll")
    public List<Role> findAll(){
        return roleService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Role> findPage(int page, int size){
        return roleService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Role> findList(@RequestBody Map<String,Object> searchMap){
        return roleService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Role> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  roleService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Role findById(Integer id){
        return roleService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Role role){
        roleService.add(role);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Role role){
        roleService.update(role);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        roleService.delete(id);
        return new Result();
    }

    /**
     * 实现角色权限对查询，需要配合页面
     * @param roleId 角色Id
     * @return
     */
    @GetMapping("/findRoleResource")
    public RoleCombination findRoleResource(Integer roleId){
        RoleCombination roleCombination = this.roleService.findRoleResource(roleId);
        return roleCombination;
    }

    /**
     * 实现保存用户所以选择的权限
     * @param roleId
     * @param resourceIds
     * @return
     */
    @PostMapping("/saveRoleAndResource")
    public Result saveRoleAndResource(Integer roleId,@RequestBody List<Integer> resourceIds){
        this.roleService.saveRoleAndResource(roleId,resourceIds);
        return new Result();
    }

}
