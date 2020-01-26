package com.qingcheng.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.pojo.system.AdminCombination;
import com.qingcheng.pojo.system.Resource;
import com.qingcheng.service.system.AdminService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Reference
    private AdminService adminService;


    @GetMapping("/findAll")
    public List<Admin> findAll(){
        return adminService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Admin> findPage(int page, int size){
        return adminService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Admin> findList(@RequestBody Map<String,Object> searchMap){
        return adminService.findList(searchMap);
    }

    /**
     * 实现分页查询，查询结果对象为组合实体类
     * @param searchMap 查询条件
     * @param page 当前页
     * @param size 每页大小
     * @return
     */
    @PostMapping("/findPage")
    public PageResult<AdminCombination> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return this.adminService.findPageCombination(searchMap,page,size);
    }

    @GetMapping("/findById")
    public Admin findById(Integer id){
        return adminService.findById(id);
    }


    /**
     * 实现用户到保存
     * @param admin
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody AdminCombination admin){
        adminService.createAdmin(admin); // 保存用户
        return new Result();
    }

    /**
     * 实现数据的修改
     * @param adminCombination
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody AdminCombination adminCombination){
        adminService.update(adminCombination);
        return new Result();
    }

    /**
     * 实现数据的删除
     * @param id
     * @return
     */
    @GetMapping("/delete")
    public Result delete(Integer id){
        this.adminService.delete(id);
        return new Result();
    }

    /**
     * 实现密码修改
     * @param rowPassword 原密码
     * @param password 新密码
     * @return
     */
    @PostMapping("/updatePassword")
    public Result updatePassword(String rowPassword, String password){
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName(); //获取登录用户名
        this.adminService.updatePassword(loginName,rowPassword,password); // 更新密码
        return new Result();
    }

    /**
     * 更新管理员状态
     * @param id 管理员ID
     * @param status 管理员状态
     * @return
     */
    @PostMapping("/updateStatus")
    public Result updateStatus(Integer id,String status){
        this.adminService.updateStatus(id,status);
        return new Result();
    }
}
