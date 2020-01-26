package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mysql.cj.util.StringUtils;
import com.qingcheng.dao.OrderConfigMapper;
import com.qingcheng.dao.OrderItemMapper;
import com.qingcheng.dao.OrderLogMapper;
import com.qingcheng.dao.OrderMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.order.*;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import xyz.downgoon.snowflake.Snowflake;

import java.time.LocalDateTime;
import java.util.*;

@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper ;

    @Autowired
    private OrderLogMapper orderLogMapper ;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private OrderConfigMapper orderConfigMapper ;

    @Reference
    private SkuService skuService ;
    /**
     * 返回全部记录
     * @return
     */
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Order> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Order> orders = (Page<Order>) orderMapper.selectAll();
        return new PageResult<Order>(orders.getTotal(),orders.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        return new PageResult<Order>(orders.getTotal(),orders.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param order
     */
    public void add(Order order) {
        orderMapper.insert(order);
    }

    /**
     * 修改
     * @param order
     */
    public void update(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    /**
     *  删除
     * @param id
     */
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }


    /**
     * 实现订单的查询
     * @param id 传递订单iD
     * @return 返回订单信息以及订单详情信息
     */
    @Override
    public OrderAndOrderItem findOrderById(String id) {
        // 查询订单信息
        Order order = this.orderMapper.selectByPrimaryKey(id); // 查询对应的订单信息
        if (order==null){
            throw new RuntimeException("没有次订单数据");
        }

        Example example = new Example(OrderItem.class); // 创建查询条件
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId",id); // 查询这个订单的详情页
        List<OrderItem> orderItemList = this.orderItemMapper.selectByExample(example);

        OrderAndOrderItem orderAndOrderItem = new OrderAndOrderItem(); // 创建返回的数据类型对象
        orderAndOrderItem.setOrder(order); // 设置订单对象
        orderAndOrderItem.setOrderItemList(orderItemList); //设置订单对象

        return orderAndOrderItem;
    }


    /**
     *  实现多个订单ID的查询
     * @param ids 传递需要查询的订单ID
     * @return
     */
    @Override
    public List<Order> findSlipByIds(String[] ids) {
        if (ids.length == 0 ){ // 如果数组长度为0则直接返回空
            return null;
        }
        Example example = new Example(Order.class); // 构建查询条件
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(ids));

        List<Order> orderList = this.orderMapper.selectByExample(example);

        return orderList ;
    }

    /**
     * 实现 保存物流信息
     * @param orderList 批量发货的ID
     */
    @Override
    public void updateSlip(List<Order> orderList) {
        String userName = "tonyTucker" ; // 操作用户暂时写死


        for (Order order:orderList){
            Date date = new Date(); // 获取当前时间
            if (!"1".equals(order.getPayStatus())){ //没有支付的订单不能发货
                throw new RuntimeException("订单："+order.getId()+" 未支付，不能发货");
            }
            //没有物流名称和单号不能发货
            if (StringUtils.isNullOrEmpty(order.getShippingName()) || StringUtils.isNullOrEmpty(order.getShippingCode()) ){
                throw new RuntimeException("订单："+order.getId()+" 没有物流名称/单号，不能发货");
            }
            order.setOrderStatus("2"); //修改订单状态改为已经发货
            order.setConsignStatus("1"); // 修改订单状态改为已经发货
            order.setConsignTime(date); //设置发货时间
            this.orderMapper.updateByPrimaryKeySelective(order); // 跟新数据

            // 日志信息
            OrderLog orderLog = new OrderLog();
            orderLog.setId(this.snowflake.nextId()+""); // 设置ID
            orderLog.setOperateTime(date); // 记录操作时间
            orderLog.setOrderId(order.getId()); // 记录ID
            orderLog.setConsignStatus(order.getConsignStatus()); //记录发货状态
            orderLog.setPayStatus(order.getPayStatus()); // 记录付款状态
            orderLog.setOrderStatus(order.getOrderStatus()); // 记录订单状态
            orderLog.setOperater(userName); // 记录操作员
            orderLog.setRemarks("暂无流水号"); // 设置备注信息
            this.orderLogMapper.insert(orderLog); // 插入数据
        }
    }

    /**
     * 实现订单的合并
     * @param mergeMaster 主订单id
     * @param mergeSlaver 从订单id
     */
    @Override
    public void mergeOrder(String mergeMaster, String mergeSlaver) {
        Date date = new Date(); // 获取当前时间

        // 查询叮当信息
        Order orderMaster = this.orderMapper.selectByPrimaryKey(mergeMaster);
        Order orderSlaver = this.orderMapper.selectByPrimaryKey(mergeSlaver);

        if (orderMaster==null || orderSlaver==null){
            throw new RuntimeException("主订单/从订单不存在");
        }
        if ("0".equals(orderMaster.getOrderStatus())) { // 主订单未付款不能合并
            throw new RuntimeException("主订单未付款不能合并");
        }
        if ("0".equals(orderSlaver.getOrderStatus())) { // 从订单未付款不能合并
            throw new RuntimeException("从订单未付款不能合并");
        }
        // 数量合计为 主订单数量合计加上从订单数量合计
        orderMaster.setTotalNum(orderMaster.getTotalNum() + orderSlaver.getTotalNum());
        // 金额合计也是主从相加
        orderMaster.setTotalMoney(orderMaster.getTotalMoney() + orderSlaver.getTotalMoney());
        // 实付金额合计也是主从相加
        orderMaster.setPayMoney(orderMaster.getPayMoney()+orderSlaver.getPayMoney());
        orderMaster.setUpdateTime(date);

        orderSlaver.setIsDelete("1"); // 逻辑删除从订单
        orderSlaver.setOrderStatus("4"); // 设置订单状态为关闭


        this.orderMapper.updateByPrimaryKeySelective(orderMaster);// 更新数据
        this.orderMapper.updateByPrimaryKeySelective(orderSlaver);// 更新数据

        // 修改从订单下的订单详情改为主订单
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId",mergeSlaver); // 查询所有的从订单下的商品详情

        List<OrderItem> orderItemSlaverList = this.orderItemMapper.selectByExample(example);
        for(OrderItem orderItemSlaver:orderItemSlaverList){
            orderItemSlaver.setOrderId(mergeMaster); // 将orderID改为主订单id
            this.orderItemMapper.updateByPrimaryKeySelective(orderItemSlaver); // 更新数据
        }


        //日志的记录
        OrderLog orderLogMaster = new OrderLog();
        OrderLog orderLogSlaver = new OrderLog();

        //主订单日志
        orderLogMaster.setId(this.snowflake.nextId()+""); // 设置日志ID
        orderLogMaster.setOperater("system");
        orderLogMaster.setOperateTime(date);
        orderLogMaster.setOrderId(orderMaster.getId()); // 设置主订单ID
        orderLogMaster.setOrderStatus(orderMaster.getOrderStatus()); // 设置主订单状态
        orderLogMaster.setPayStatus(orderMaster.getPayStatus()); // 设置付款状态
        orderLogMaster.setConsignStatus(orderMaster.getConsignStatus()); // 设置发货状态
        orderLogMaster.setRemarks("合并订单(主)");

        //从订单日志
        orderLogSlaver.setId(this.snowflake.nextId()+""); // 设置日志ID
        orderLogSlaver.setOperater("system");
        orderLogSlaver.setOperateTime(date);
        orderLogSlaver.setOrderId(orderSlaver.getId()); // 设置从订单ID
        orderLogSlaver.setOrderStatus(orderSlaver.getOrderStatus()); // 设置主订单状态
        orderLogSlaver.setPayStatus(orderSlaver.getPayStatus()); // 设置付款状态
        orderLogSlaver.setConsignStatus(orderSlaver.getConsignStatus()); // 设置发货状态
        orderLogSlaver.setRemarks("合并订单(从)");

        //插入日志
        this.orderLogMapper.insert(orderLogMaster);
        this.orderLogMapper.insert(orderLogSlaver);
    }

    /**
     * 用于订单的拆分
     * @param id 需要拆分的订单ID
     * @param splitOrderList 保存了需要拆分的商品详情
     */
    @Override
    public void splitOrder(String id, List<SplitOrder> splitOrderList) {
        int isZeroCount = 0 ; // 用于判断是否传递过来拆分商品数量都会0
        int splitNumItem = 0; // 用于保存需要拆分的总数量
        List<OrderItem> splitOrderItemList = new ArrayList<>(); // 用于保存拆分的订单的订单详情
        List<OrderItem> rowOrderItemList = new ArrayList<>(); // 用于保存原来订单的订单详情

        int rowTotalNum = 0 ; // 用于保存原订单的商品总数量

        int rowTotalMoney = 0 ; // 用于保存原订单的总金额
        int splitTotalMoney = 0 ; // 用于保存拆分订单总金额

        for (SplitOrder splitOrder:splitOrderList){
            if (splitOrder.getNum()==0){
                isZeroCount ++ ;
            }
            splitNumItem += splitOrder.getNum(); // 计算拆分总数量
        }
        if (isZeroCount==splitOrderList.size()){
            throw new RuntimeException("没有需要拆分的商品");
        }

        Order order = this.orderMapper.selectByPrimaryKey(id); // 查询订单
        if (order == null){
            throw new RuntimeException("订单不存在");
        }
        if (!"0".equals(order.getOrderStatus())){ // 如果订单已经付款则不能拆分
            throw new RuntimeException("订单已经付款不能拆分");
        }
        Date date = new Date();
        Order splitOrderData = order.clone() ; // 复制主订单
        splitOrderData.setId(this.snowflake.nextId()+""); // 设置新的id
        splitOrderData.setCreateTime(date); // 设置创建订单时间
        splitOrderData.setUpdateTime(date); // 设置更新时间


        Example example = new Example(OrderItem.class);  // 查询订单详情
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId",order.getId());
        List<OrderItem> orderItemList = this.orderItemMapper.selectByExample(example);// 查询订单详情

        for(OrderItem orderItem:orderItemList){
            for(SplitOrder splitOrder : splitOrderList){

                if (orderItem.getId().equals(splitOrder.getId())){ // 判断需要拆分的订单详情与查询的订单详情id是否一样
                    OrderItem orderItemSplit = orderItem.clone() ; // 复制订单详情
                    int i = orderItem.getNum() - splitOrder.getNum(); // 相加数量
                    if (i<0){
                        throw new RuntimeException("需要拆分的商品大于购买数量");
                    }else if (i>0){ // 拆分部分
                        if (splitOrder.getNum()==0){ // 如果为了0，表示不用拆分
                            rowOrderItemList.add(orderItem); //直接添加到愿订单到订单详情中即可
                        } else {
                            // 设置原订单数据
                            orderItem.setNum(i); // 设置数量
                            orderItem.setMoney(orderItem.getNum() * orderItem.getPrice()); // 设置总金额
                            rowOrderItemList.add(orderItem); // 保存到集合中
                            rowTotalNum += orderItem.getNum(); // 保存原订单的商品总数
                            rowTotalMoney += orderItem.getMoney() ; // 保存商品金额到订单金额

                            // 设置拆分订单数据
                            orderItemSplit.setId(this.snowflake.nextId()+""); // 重新设置订单详情ID
                            orderItemSplit.setNum(splitOrder.getNum()); // 设置拆分订单的数量
                            orderItemSplit.setMoney(splitOrder.getNum() * orderItemSplit.getPrice()); // 设置拆分订单金额
                            orderItemSplit.setOrderId(splitOrderData.getId()); // 设置对应的orderId
                            splitOrderItemList.add(orderItemSplit); // 保存到集合中
                            splitTotalMoney += orderItemSplit.getMoney() ;// 保存拆分订单的总金额

                        }
                    } else { // 拆分全部
                        orderItemSplit.setOrderId(splitOrderData.getId()); // 拆分全部直接修改item的orderId即可
                        splitOrderItemList.add(orderItemSplit); // 保存到集合中
                        splitTotalMoney += orderItemSplit.getMoney() ;// 保存拆分订单的总金额
                    }
                    break; // 当匹配到一个就可以到下一个订单详情ID的匹配
                }


            }
        }

        if (rowOrderItemList.size()==0){ // 表示全部订单都被拆分了
            throw new RuntimeException("不能拆分全部订单");
        }

        order.setTotalMoney(rowTotalMoney); // 设置原来订单总金额
        order.setTotalNum(rowTotalNum); // 设置原订单商品数量

        splitOrderData.setTotalMoney(splitTotalMoney); // 设置拆分订单总金额
        splitOrderData.setTotalNum(splitNumItem); // 设置拆分订单商品总数量

        this.orderMapper.updateByPrimaryKeySelective(order);// 更新原来订单数据
        this.orderMapper.insertSelective(splitOrderData);// 插入拆分的订单数据


        for (OrderItem orderItem:orderItemList){ // 删除掉原订单的订单详情数据
            this.orderItemMapper.deleteByPrimaryKey(orderItem);
        }
        for (OrderItem orderItem:rowOrderItemList){ // 重新插入原订单的订单详情
            this.orderItemMapper.insertSelective(orderItem);
        }

        for(OrderItem orderItem : splitOrderItemList){ // 插入拆分出来的订单详情
            this.orderItemMapper.insertSelective(orderItem);// 插入orderItem
        }


        //订单日志
        OrderLog orderLogRow = new OrderLog();
        OrderLog orderLogSplit = new OrderLog();

        orderLogRow.setId(this.snowflake.nextId()+"");// 设置Id
        orderLogRow.setOperater("system"); // 系统设置
        orderLogRow.setOperateTime(date); // 设置操作时间
        orderLogRow.setOrderId(order.getId()); // 设置订单ID
        orderLogRow.setOrderStatus(order.getOrderStatus()); // 设置订单状态
        orderLogRow.setPayStatus(order.getPayStatus()); // 设置付款状态
        orderLogRow.setConsignStatus(order.getConsignStatus()); // 设置发货壮体
        orderLogRow.setRemarks("订单被拆分");

        orderLogSplit.setId(this.snowflake.nextId()+"");// 设置Id
        orderLogSplit.setOperater("system"); // 系统设置
        orderLogSplit.setOperateTime(date); // 设置操作时间
        orderLogSplit.setOrderId(splitOrderData.getId()); // 设置订单ID
        orderLogSplit.setOrderStatus(splitOrderData.getOrderStatus()); // 设置订单状态
        orderLogSplit.setPayStatus(splitOrderData.getPayStatus()); // 设置付款状态
        orderLogSplit.setConsignStatus(splitOrderData.getConsignStatus()); // 设置发货壮体
        orderLogSplit.setRemarks("创建订单");

        this.orderLogMapper.insert(orderLogRow); // 插入日志
        this.orderLogMapper.insert(orderLogSplit); // 插入日志
    }

    /**
     * 验证订单超时
     */
    @Override
    public void TimeOut() {
        OrderConfig orderConfig = this.orderConfigMapper.selectByPrimaryKey(1);//查询订单配置，此表只有一条记录
        Integer orderTimeout = orderConfig.getOrderTimeout();//取出订单超时时间
        LocalDateTime localDateTime = LocalDateTime.now(); // 取得当前时间
        localDateTime = localDateTime.minusMinutes(orderTimeout); // 取得当前时间减去设置的超时时间

        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        //如果创建时间在 当前时间前-减去设定的超时时间前面则表示该订单已经超时，如果没有则订单没有超时
        criteria.andLessThan("createTime",localDateTime);
        criteria.andEqualTo("orderStatus","0"); //并且订单状态为未付款的订单
        criteria.andEqualTo("isDelete","0"); // 且订单没有被删除

        List<Order> orderList = this.orderMapper.selectByExample(example);
        for(Order order:orderList){
            order.setOrderStatus("4"); // 将超时的订单设置为关闭状态
            this.orderMapper.updateByPrimaryKeySelective(order); // 更新数据

            OrderLog orderLog = new OrderLog() ;
            orderLog.setId(this.snowflake.nextId()+"");
            orderLog.setOperateTime(new Date()); // 记录当前时间
            orderLog.setOperater("system"); // 设置操作员为system
            orderLog.setOrderStatus("4"); // 设置订单状态为关闭
            orderLog.setOrderId(order.getId());// 设置订单ID
            orderLog.setPayStatus(order.getPayStatus()); // 记录订单支付状态
            orderLog.setConsignStatus(order.getConsignStatus());//记录发货状态
            orderLog.setRemarks("暂无订单流水号");//记录备注
            this.orderLogMapper.insert(orderLog); // 插入日志
        }
    }


    /**
     * 查询订单下的订单详情以及sku信息
     * @param id 订单ID
     * @return
     */
    @Override
    public List<OrderItemAndSku> findOrderItemSku(String id) {
        ArrayList<OrderItemAndSku> orderItemAndSkuList = new ArrayList<>();

        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId",id); // 查询order订单下的OrderItem
        List<OrderItem> orderItemList = this.orderItemMapper.selectByExample(example);

        for (OrderItem orderItem:orderItemList){
            OrderItemAndSku orderItemAndSku = new OrderItemAndSku();
            Sku sku = this.skuService.findById(orderItem.getSkuId());

            orderItemAndSku.setOrderItem(orderItem);
            orderItemAndSku.setSku(sku);

            orderItemAndSkuList.add(orderItemAndSku); // 保存数据
        }

        return orderItemAndSkuList;
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 订单id
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 支付类型，1、在线支付、0 货到付款
            if(searchMap.get("payType")!=null && !"".equals(searchMap.get("payType"))){
                criteria.andLike("payType","%"+searchMap.get("payType")+"%");
            }
            // 物流名称
            if(searchMap.get("shippingName")!=null && !"".equals(searchMap.get("shippingName"))){
                criteria.andLike("shippingName","%"+searchMap.get("shippingName")+"%");
            }
            // 物流单号
            if(searchMap.get("shippingCode")!=null && !"".equals(searchMap.get("shippingCode"))){
                criteria.andLike("shippingCode","%"+searchMap.get("shippingCode")+"%");
            }
            // 用户名称
            if(searchMap.get("username")!=null && !"".equals(searchMap.get("username"))){
                criteria.andLike("username","%"+searchMap.get("username")+"%");
            }
            // 买家留言
            if(searchMap.get("buyerMessage")!=null && !"".equals(searchMap.get("buyerMessage"))){
                criteria.andLike("buyerMessage","%"+searchMap.get("buyerMessage")+"%");
            }
            // 是否评价
            if(searchMap.get("buyerRate")!=null && !"".equals(searchMap.get("buyerRate"))){
                criteria.andLike("buyerRate","%"+searchMap.get("buyerRate")+"%");
            }
            // 收货人
            if(searchMap.get("receiverContact")!=null && !"".equals(searchMap.get("receiverContact"))){
                criteria.andLike("receiverContact","%"+searchMap.get("receiverContact")+"%");
            }
            // 收货人手机
            if(searchMap.get("receiverMobile")!=null && !"".equals(searchMap.get("receiverMobile"))){
                criteria.andLike("receiverMobile","%"+searchMap.get("receiverMobile")+"%");
            }
            // 收货人地址
            if(searchMap.get("receiverAddress")!=null && !"".equals(searchMap.get("receiverAddress"))){
                criteria.andLike("receiverAddress","%"+searchMap.get("receiverAddress")+"%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if(searchMap.get("sourceType")!=null && !"".equals(searchMap.get("sourceType"))){
                criteria.andLike("sourceType","%"+searchMap.get("sourceType")+"%");
            }
            // 交易流水号
            if(searchMap.get("transactionId")!=null && !"".equals(searchMap.get("transactionId"))){
                criteria.andLike("transactionId","%"+searchMap.get("transactionId")+"%");
            }
            // 订单状态
            if(searchMap.get("orderStatus")!=null && !"".equals(searchMap.get("orderStatus"))){
                criteria.andLike("orderStatus","%"+searchMap.get("orderStatus")+"%");
            }
            // 支付状态
            if(searchMap.get("payStatus")!=null && !"".equals(searchMap.get("payStatus"))){
                criteria.andLike("payStatus","%"+searchMap.get("payStatus")+"%");
            }
            // 发货状态
            if(searchMap.get("consignStatus")!=null && !"".equals(searchMap.get("consignStatus"))){
                criteria.andLike("consignStatus","%"+searchMap.get("consignStatus")+"%");
            }
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andLike("isDelete","%"+searchMap.get("isDelete")+"%");
            }

            // 数量合计
            if(searchMap.get("totalNum")!=null ){
                criteria.andEqualTo("totalNum",searchMap.get("totalNum"));
            }
            // 金额合计
            if(searchMap.get("totalMoney")!=null ){
                criteria.andEqualTo("totalMoney",searchMap.get("totalMoney"));
            }
            // 优惠金额
            if(searchMap.get("preMoney")!=null ){
                criteria.andEqualTo("preMoney",searchMap.get("preMoney"));
            }
            // 邮费
            if(searchMap.get("postFee")!=null ){
                criteria.andEqualTo("postFee",searchMap.get("postFee"));
            }
            // 实付金额
            if(searchMap.get("payMoney")!=null ){
                criteria.andEqualTo("payMoney",searchMap.get("payMoney"));
            }

        }
        return example;
    }

}
