package com.qingcheng.pojo.order;

import java.io.Serializable;
import java.util.List;

/**
 * 订单(tb_order)与订单详情表(tb_order_item)的组合实体类
 *
 */
public class OrderAndOrderItem implements Serializable {

    private Order order ; // 订单数据
    private List<OrderItem> orderItemList ; // 订单详情数据

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
