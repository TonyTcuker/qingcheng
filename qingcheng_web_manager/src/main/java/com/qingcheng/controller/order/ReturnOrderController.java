package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.order.ReturnOrder;
import com.qingcheng.service.order.ReturnOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/returnOrder")
public class ReturnOrderController {

    @Reference
    private ReturnOrderService returnOrderService;

    @GetMapping("/findAll")
    public List<ReturnOrder> findAll(){
        return returnOrderService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<ReturnOrder> findPage(int page, int size){
        return returnOrderService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<ReturnOrder> findList(@RequestBody Map<String,Object> searchMap){
        return returnOrderService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<ReturnOrder> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  returnOrderService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public ReturnOrder findById(Long id){
        return returnOrderService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody ReturnOrder returnOrder){
        returnOrderService.add(returnOrder);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody ReturnOrder returnOrder){
        returnOrderService.update(returnOrder);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Long id){
        returnOrderService.delete(id);
        return new Result();
    }

    @PostMapping("/refund") // 同意退款
    public Result refund(String id,Integer adminId,Integer money){
        this.returnOrderService.refund(id,adminId,money); // 执行同意退货业务层
        return new Result();
    }

    @PostMapping("/reject") // 拒绝退款
    public Result reject(String id,Integer adminId,String remark){
        this.returnOrderService.reject(id,adminId,remark);
        return new Result();
    }

    @PostMapping("/refundGoods") // 同意退货请求
    public Result refundGoods(String id,Integer adminId,Integer money,String isReturnFreight){ // isReturnFreight 表示是否退运费
        this.returnOrderService.refundGoods(id,adminId,money,isReturnFreight);
        return new Result();
    }

    @PostMapping("/rejectGoods") // 拒绝退货请求
    public Result rejectGoods(String id,Integer adminId,String remark){
        this.returnOrderService.rejectGoods(id,adminId,remark);
        return new Result();
    }
}
