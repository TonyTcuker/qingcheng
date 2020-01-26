package com.qingcheng.service.order;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderAndOrderItem;
import com.qingcheng.pojo.order.OrderItemAndSku;
import com.qingcheng.pojo.order.SplitOrder;

import java.util.*;

/**
 * order业务逻辑层
 */
public interface OrderService {


    public List<Order> findAll();


    public PageResult<Order> findPage(int page, int size);


    public List<Order> findList(Map<String,Object> searchMap);


    public PageResult<Order> findPage(Map<String,Object> searchMap,int page, int size);


    public Order findById(String id);

    public void add(Order order);


    public void update(Order order);


    public void delete(String id);


    public OrderAndOrderItem findOrderById(String id);

    public List<Order> findSlipByIds(String ids[]);

    public void updateSlip(List<Order> orderList);

    public void TimeOut();

    public void mergeOrder(String mergeMaster,String mergeSlaver);

    public void splitOrder(String id, List<SplitOrder> splitOrderList) ;

    public List<OrderItemAndSku> findOrderItemSku(String id);

}
