package com.qingcheng.pojo.order;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 订单统计实体类
 */
@Table(name = "tb_order_report")
public class OrderReport implements Serializable {
    @Id
    private Date countDate;
    private Integer people;
    private Integer orderNum ;
    private Integer itemNum ;
    private Integer validOrder ;
    private Integer totalMoney ;
    private Integer returnMoney ;
    private Integer peopleOrder ;
    private Integer peoplePay ;
    private Integer orderPay ;
    private Integer itemPay ;
    private Integer moneyPay ;

    public Date getCountDate() {
        return countDate;
    }

    public void setCountDate(Date countDate) {
        this.countDate = countDate;
    }

    public Integer getPeople() {
        return people;
    }

    public void setPeople(Integer people) {
        this.people = people;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getItemNum() {
        return itemNum;
    }

    public void setItemNum(Integer itemNum) {
        this.itemNum = itemNum;
    }

    public Integer getValidOrder() {
        return validOrder;
    }

    public void setValidOrder(Integer validOrder) {
        this.validOrder = validOrder;
    }

    public Integer getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Integer totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Integer getReturnMoney() {
        return returnMoney;
    }

    public void setReturnMoney(Integer returnMoney) {
        this.returnMoney = returnMoney;
    }

    public Integer getPeopleOrder() {
        return peopleOrder;
    }

    public void setPeopleOrder(Integer peopleOrder) {
        this.peopleOrder = peopleOrder;
    }

    public Integer getPeoplePay() {
        return peoplePay;
    }

    public void setPeoplePay(Integer peoplePay) {
        this.peoplePay = peoplePay;
    }

    public Integer getOrderPay() {
        return orderPay;
    }

    public void setOrderPay(Integer orderPay) {
        this.orderPay = orderPay;
    }

    public Integer getItemPay() {
        return itemPay;
    }

    public void setItemPay(Integer itemPay) {
        this.itemPay = itemPay;
    }

    public Integer getMoneyPay() {
        return moneyPay;
    }

    public void setMoneyPay(Integer moneyPay) {
        this.moneyPay = moneyPay;
    }
}
