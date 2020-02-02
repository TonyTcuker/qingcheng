package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderAndOrderItem;
import com.qingcheng.pojo.order.OrderItemAndSku;
import com.qingcheng.pojo.order.SplitOrder;
import com.qingcheng.service.order.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/order")
@ResponseBody
public class OrderController {

    @Reference
    private OrderService orderService;

    @GetMapping("/findAll")
    public List<Order> findAll(){
        return orderService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Order> findPage(int page, int size){
        return orderService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Order> findList(@RequestBody Map<String,Object> searchMap){
        return orderService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Order> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        PageResult<Order> page1 = orderService.findPage(searchMap, page, size);
        return page1;
    }

    @GetMapping("/findById")
    public Order findById(String id){
        return orderService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Order order){
        orderService.add(order);
        return new Result();
    }


    @PostMapping("/update")
    public Result update(@RequestBody Order order){
        orderService.update(order);
        return new Result();
    }

    @PreAuthorize("orders_delete")
    @GetMapping("/delete")
    public Result delete(String id){
        orderService.delete(id);
        return new Result();
    }

    @GetMapping("/findOrder")
    public OrderAndOrderItem findOrderById(String id){
        OrderAndOrderItem orderAndOrderItem = orderService.findOrderById(id); // 查询数据
        return orderAndOrderItem; //返回查询结果
    }

    @PostMapping("/findSlipByIds")
    public List<Order> findSlipByIds(String[] ids){
        List<Order> orderList = this.orderService.findSlipByIds(ids);
        return orderList;
    }

    @GetMapping("/findOrderItemSku")
    public List<OrderItemAndSku> findOrderItemSku(String id){
        List<OrderItemAndSku> orderItemAndSkuList = this.orderService.findOrderItemSku(id);
        return orderItemAndSkuList;
    }

    @PostMapping("updateSlip")
    public Result updateSlip(@RequestBody List<Order> list){ //保存物流信息
        this.orderService.updateSlip(list);
        return new Result();
    }

    @PreAuthorize("orders_edit")
    @PostMapping("/mergeOrder") // 合并订单
    public Result mergeOrder(String mergerMaster,String mergerSlaver){ // 需要传入 主订单ID 以及从订单ID
        this.orderService.mergeOrder(mergerMaster,mergerSlaver);
        return new Result();
    }

    @PreAuthorize("orders_edit")
    @PostMapping("/splitOrder") // 拆分订单调用
    public Result splitOrder(String id ,@RequestBody List<SplitOrder> splitOrderList) {
        this.orderService.splitOrder(id,splitOrderList);
        return new Result();
    }
}
