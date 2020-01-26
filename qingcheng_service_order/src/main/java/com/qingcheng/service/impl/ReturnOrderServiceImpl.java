package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.OrderItemMapper;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.dao.ReturnOrderItemMapper;
import com.qingcheng.dao.ReturnOrderMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.order.Order;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.pojo.order.ReturnOrder;
import com.qingcheng.pojo.order.ReturnOrderItem;
import com.qingcheng.service.order.ReturnOrderService;
import com.sun.istack.internal.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = ReturnOrderService.class)
@Transactional
public class ReturnOrderServiceImpl implements ReturnOrderService {

    @Autowired
    private ReturnOrderMapper returnOrderMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ReturnOrderItemMapper returnOrderItemMapper;
    /**
     * 返回全部记录
     * @return
     */
    public List<ReturnOrder> findAll() {
        return returnOrderMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<ReturnOrder> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<ReturnOrder> returnOrders = (Page<ReturnOrder>) returnOrderMapper.selectAll();
        return new PageResult<ReturnOrder>(returnOrders.getTotal(),returnOrders.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<ReturnOrder> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return returnOrderMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<ReturnOrder> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<ReturnOrder> returnOrders = (Page<ReturnOrder>) returnOrderMapper.selectByExample(example);
        return new PageResult<ReturnOrder>(returnOrders.getTotal(),returnOrders.getResult());
    }



    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public ReturnOrder findById(Long id) {
        return returnOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param returnOrder
     */
    public void add(ReturnOrder returnOrder) {
        returnOrderMapper.insert(returnOrder);
    }

    /**
     * 修改
     * @param returnOrder
     */
    public void update(ReturnOrder returnOrder) {
        returnOrderMapper.updateByPrimaryKeySelective(returnOrder);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(Long id) {
        returnOrderMapper.deleteByPrimaryKey(id);
    }


    /**
     * 实现退款
     * @param id  退款申请的ID
     * @param adminId 管理员ID
     * @param money 退款金额
     */
    @Override
    public void refund(String id, Integer adminId, Integer money) {
        ReturnOrder returnOrder = this.returnOrderMapper.selectByPrimaryKey(id); //查询申请记录
        Order order = this.orderMapper.selectByPrimaryKey(returnOrder.getOrderId());// 查询对应的订单记录
        if (returnOrder==null){ // 表示没有退款订单
            throw new RuntimeException("不存在此退款订单");
        }
        if (order.getPayMoney()<adminId || money<0){ // 退款金额大于付款金额，不给予退款
            throw new RuntimeException("退款金额大于付款金额，不能进行退款");
        }
        if (!"2".equals(returnOrder.getType())){ // 判断退款订单状态是否为退款流程
            throw new RuntimeException("此订单不是退款申请，不能实现退款流程");
        }
        returnOrder.setAdminId(adminId); // 设置管理员ID
        returnOrder.setDisposeTime(new Date()); // 设置处理时间
        returnOrder.setStatus("1"); // 设置退款状态 1-同意退款 2-驳回退款
        returnOrder.setRemark("同意退款"); // 设置处理备注
        this.returnOrderMapper.updateByPrimaryKeySelective(returnOrder);


        // 调用退款接口（暂时未未实现）

    }

    /**
     * 实现驳回退款请求
     * @param id 退款申请ID
     * @param adminId 管理员ID
     * @param remark 处理备注
     */
    @Override
    public void reject(String id, Integer adminId, String remark) {
        if (remark.length()<5){ // 处理备注字数不能少于5个子
            throw new RuntimeException("备注信息不能少于5个字");
        }
        ReturnOrder returnOrder = this.returnOrderMapper.selectByPrimaryKey(id); // 查询申请的订单ID
        if (returnOrder==null){ // 没有此退款订单
            throw new RuntimeException("不存在此退款订单");
        }
        if (!"2".equals(returnOrder.getType())){ // 不是退款类型的退款订单
            throw new RuntimeException("此订单不是退款申请，不能实现退款流程");
        }
        returnOrder.setStatus("2"); // 设置状态为驳回
//        returnOrder.setRemark(remark);// 设置备注信息
        returnOrder.setRemark(remark);
        returnOrder.setAdminId(adminId); // 设置操作员ID
        returnOrder.setDisposeTime(new Date()); // 设置操作时间
        this.returnOrderMapper.updateByPrimaryKeySelective(returnOrder);// 更新数据

        //修改订单中的商品（order_item）提出退货申请的商品状态(is_return)
        Example example = new Example(ReturnOrderItem.class); // 设置查询条件
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("returnOrderId",returnOrder.getId()); // 查询退款申请中的申请详细商品列表
        List<ReturnOrderItem> returnOrderItemList = this.returnOrderItemMapper.selectByExample(example); // 查询有哪些商品提出了退款申请

        for(ReturnOrderItem returnOrderItem : returnOrderItemList){ // 每一个退款订单明细中都会有订单明细ID，这些订单就是需要驳回的订单
            OrderItem orderItem = new OrderItem();
            orderItem.setId(returnOrderItem.getOrderItemId()+""); // 设置ID
            orderItem.setIsReturn("0"); // 0-未申请退款 1-已申请退款 2-已经退款 将订单的退款状态改为未申请，以便用户二次申请退款
            this.orderItemMapper.updateByPrimaryKeySelective(orderItem); // 重新更新数据
        }
    }

    /**
     * 实现退货请求
     * @param id ReturnOrderId
     * @param adminId 操作管理员ID
     * @param money 退款金额
     * @param isReturnFreight 是否退运费
     */
    @Override
    public void refundGoods(String id, Integer adminId, Integer money,String isReturnFreight) {
        ReturnOrder returnOrder = this.returnOrderMapper.selectByPrimaryKey(id);
        Order order = this.orderMapper.selectByPrimaryKey(returnOrder.getOrderId());
        if (returnOrder == null){ // 不存在这个退货请求
             throw new RuntimeException("不存在这个退货请求");
        }
        if (!"1".equals(returnOrder.getType())){ // 不是退货请求
            throw new RuntimeException("不是退货请求");
        }
        if (order.getPayMoney()<=money||money<0){ // 退款金额不合理
            throw new RuntimeException("退款金额不合理");
        }

        returnOrder.setStatus("1"); // 同意退款
        returnOrder.setDisposeTime(new Date()); // 设置处理时间
        returnOrder.setAdminId(adminId); // 设置管理员iD
        returnOrder.setReturnMoney(money); // 设置退款金额
        returnOrder.setIsReturnFreight(isReturnFreight); // 设置是否退运费
        returnOrder.setRemark("同意退货"); // 同意退货
        this.returnOrderMapper.updateByPrimaryKeySelective(returnOrder) ; //更新数据
    }

    /**
     * 实现拒绝退货流程
     * @param id returnOrder Id
     * @param adminId 操作管理员ID
     * @param remark 拒绝退款原因
     */
    @Override
    public void rejectGoods(String id, @NotNull Integer adminId, String remark) {
        if (remark.length()<=5){
            throw new RuntimeException("退款备注不能少于5个字");
        }
        ReturnOrder returnOrder = this.returnOrderMapper.selectByPrimaryKey(id);
        if (returnOrder== null){ // 不存在此退货申请
            throw new RuntimeException("不存在此退货申请");
        }
        if (!"1".equals(returnOrder.getType())){ // 不是退款请求
            throw new RuntimeException("不是退款请求");
        }
        returnOrder.setStatus("2"); // 设置订单状态为驳回
        returnOrder.setRemark(remark); // 设置退款原因5
        returnOrder.setAdminId(adminId); // 设置管理员ID
        returnOrder.setDisposeTime(new Date()); // 设置操作时间
        this.returnOrderMapper.updateByPrimaryKeySelective(returnOrder); // 更新数据

        Example example = new Example(ReturnOrderItem.class); // 查询退款详情表
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("returnOrderId",returnOrder.getId()); // 找出对应的退款货详情

        List<ReturnOrderItem> returnOrderItemList = this.returnOrderItemMapper.selectByExample(example);
        for (ReturnOrderItem data:returnOrderItemList){
            OrderItem orderItem = new OrderItem();
            orderItem.setId(data.getOrderItemId()+""); //设置id
            orderItem.setIsReturn("0"); // 设置申请状态为未申请
            this.orderItemMapper.updateByPrimaryKeySelective(orderItem); // 更新数据
        }
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(ReturnOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 用户账号
            if(searchMap.get("userAccount")!=null && !"".equals(searchMap.get("userAccount"))){
                criteria.andLike("userAccount","%"+searchMap.get("userAccount")+"%");
            }
            // 联系人
            if(searchMap.get("linkman")!=null && !"".equals(searchMap.get("linkman"))){
                criteria.andLike("linkman","%"+searchMap.get("linkman")+"%");
            }
            // 联系人手机
            if(searchMap.get("linkmanMobile")!=null && !"".equals(searchMap.get("linkmanMobile"))){
                criteria.andLike("linkmanMobile","%"+searchMap.get("linkmanMobile")+"%");
            }
            // 类型
            if(searchMap.get("type")!=null && !"".equals(searchMap.get("type"))){
                criteria.andLike("type","%"+searchMap.get("type")+"%");
            }
            // 是否退运费
            if(searchMap.get("isReturnFreight")!=null && !"".equals(searchMap.get("isReturnFreight"))){
                criteria.andLike("isReturnFreight","%"+searchMap.get("isReturnFreight")+"%");
            }
            // 申请状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
            }
            // 凭证图片
            if(searchMap.get("evidence")!=null && !"".equals(searchMap.get("evidence"))){
                criteria.andLike("evidence","%"+searchMap.get("evidence")+"%");
            }
            // 问题描述
            if(searchMap.get("description")!=null && !"".equals(searchMap.get("description"))){
                criteria.andLike("description","%"+searchMap.get("description")+"%");
            }
            // 处理备注
            if(searchMap.get("remark")!=null && !"".equals(searchMap.get("remark"))){
                criteria.andLike("remark","%"+searchMap.get("remark")+"%");
            }

            // 退款金额
            if(searchMap.get("returnMoney")!=null ){
                criteria.andEqualTo("returnMoney",searchMap.get("returnMoney"));
            }
            // 退货退款原因
            if(searchMap.get("returnCause")!=null ){
                criteria.andEqualTo("returnCause",searchMap.get("returnCause"));
            }
            // 管理员id
            if(searchMap.get("adminId")!=null ){
                criteria.andEqualTo("adminId",searchMap.get("adminId"));
            }

        }
        return example;
    }

}
