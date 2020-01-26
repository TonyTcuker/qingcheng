package com.qingcheng.pojo.goods;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


@Table(name = "tb_spu_marketable_log")
public class SpuMarketableLog implements Serializable {

    @Id
    private String id ;
    private String spuId;
    private String rowIsmaketable;//修改前上架状态
    private String updateIsmaketable;//修改后上架状态
    private Date updateIsmaketableTime;//记录修改上架状态时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpuId() {
        return spuId;
    }

    public void setSpuId(String spuId) {
        this.spuId = spuId;
    }

    public String getRowIsmaketable() {
        return rowIsmaketable;
    }

    public void setRowIsmaketable(String rowIsmaketable) {
        this.rowIsmaketable = rowIsmaketable;
    }

    public String getUpdateIsmaketable() {
        return updateIsmaketable;
    }

    public void setUpdateIsmaketable(String updateIsmaketable) {
        this.updateIsmaketable = updateIsmaketable;
    }

    public Date getUpdateIsmaketableTime() {
        return updateIsmaketableTime;
    }

    public void setUpdateIsmaketableTime(Date updateIsmaketableTime) {
        this.updateIsmaketableTime = updateIsmaketableTime;
    }
}
