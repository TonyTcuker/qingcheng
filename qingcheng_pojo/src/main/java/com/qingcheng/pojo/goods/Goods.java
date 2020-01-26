package com.qingcheng.pojo.goods;

import java.io.Serializable;
import java.util.List;

/**
 * 用于保存sku与spu的类
 */
public class Goods implements Serializable {

    private Spu spu ; // 一个商品的spu
    private List<Sku> skuList; // 一个商品的对应的sku;

    public Spu getSpu() {
        return spu;
    }

    public void setSpu(Spu spu) {
        this.spu = spu;
    }

    public List<Sku> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<Sku> skuList) {
        this.skuList = skuList;
    }
}
