package com.qingcheng.pojo.order;

import com.qingcheng.pojo.goods.Sku;

import java.io.Serializable;

/**
 * 封装OrderItem与sku
 */
public class OrderItemAndSku implements Serializable {
    private OrderItem orderItem; // 保存OrderItem数据
    private Sku sku ; // 保存sku数据

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public Sku getSku() {
        return sku;
    }

    public void setSku(Sku sku) {
        this.sku = sku;
    }
}
