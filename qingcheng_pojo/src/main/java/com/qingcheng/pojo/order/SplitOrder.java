package com.qingcheng.pojo.order;

import java.io.Serializable;

/**
 * 用于拆分订单保存订单详情数据、以及数量
 */
public class SplitOrder implements Serializable {
    private String id ; // 订单详情Id
    private Integer num ; // 订单详情数量

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
