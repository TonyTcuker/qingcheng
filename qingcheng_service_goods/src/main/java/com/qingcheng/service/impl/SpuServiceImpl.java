package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.mysql.cj.util.StringUtils;
import com.qingcheng.dao.*;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.*;
import com.qingcheng.service.goods.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import xyz.downgoon.snowflake.Snowflake;

import java.lang.reflect.Array;
import java.util.*;

@Service(interfaceClass = SpuService.class)
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuMapper spuMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Spu> findAll() {
        return spuMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Spu> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectAll();
        return new PageResult<Spu>(spus.getTotal(),spus.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Spu> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return spuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Spu> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Spu> spus = (Page<Spu>) spuMapper.selectByExample(example);
        return new PageResult<Spu>(spus.getTotal(),spus.getResult());
    }

    /**
     * 根据Id查询
     * @param id
     * @return
     */
    public Spu findById(String id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     * @param spu
     */
    public void add(Spu spu) {
        spuMapper.insert(spu);
    }

    /**
     * 修改
     * @param spu
     */
    public void update(Spu spu) {
        spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     *  移出数据的删除
     * @param id
     */
    @Transactional
    public void delete(String id) {
        Spu spu = this.spuMapper.selectByPrimaryKey(id); // 查询数据
        if (!"1".equals(spu.getIsDelete())){ // 如果没有逻辑删除数据，则不能移出数据
            throw new RuntimeException("必须要先逻辑删除数据，才能彻底删除数据！");
        }
        spuMapper.deleteByPrimaryKey(id); // 执行删除
        Example example = new Example(Sku.class); // 设置条件
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",id);
        this.skuMapper.deleteByExample(example); // 删除sku中数据
    }


    @Autowired
    private Snowflake snowflake ; // 雪花算法，用于创建ID

    @Autowired
    private CategoryMapper categoryMapper ; // 分类表的数据层，由于查询分类名称

    @Autowired
    private SkuMapper skuMapper ; // sku 数据层代码

    @Autowired
    private CategoryBrandMapper categoryBrandMapper ;//分类品牌表数据层

    /**
     * 用于保存Goods
     * 其中包括spu与sku的保存
     * @param goods
     */
    @Override
    @Transactional //开启事务
    public void saveGoods(Goods goods) {
        boolean isSave = true ; //为true表示新增，为false表示更新

        Spu rowSup = null ; // 保存未修改的数据

        Spu spu = goods.getSpu(); // 获取前端传递过来的spu

        if (spu.getId()==null){ // 判断ID是否为空，如果是空则就是新增
            spu .setId(this.snowflake.nextId()+""); // 为spu设置ID,需要转换成字符串
            spu.setSaleNum(0); // 设置销量初始值0
            spu.setCommentNum(0); // 设置评论数
            spu.setStatus("0"); // 设置未审核状态
            spu.setIsMarketable("0"); // 设置为下架状态
            spu.setIsDelete("0");// 设置默认是为删除状态
        } else { // 如果是修改
            rowSup = this.spuMapper.selectByPrimaryKey(spu.getId()); // 保存原有数据

            isSave = false;
            //需要清空sku列表数据
            Example example = new Example(Sku.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("spuId",spu.getId()); // 设置条件

            this.skuMapper.deleteByExample(example);// 清空sku列表，后面重新添加
            this.spuMapper.updateByPrimaryKeySelective(spu); // 跟新spu数据
        }

        Date date = new Date(); // 获取当前日期


        Category category = this.categoryMapper.selectByPrimaryKey(spu.getCategory3Id()); // 查询商品的三级分类iD，为sku准备

        List<Sku> skuList = goods.getSkuList();// 获取前端传递过来的sku
        for (Sku sku:skuList){ // 选好sku

            if (sku.getId() == null){ // 是新增加sku
                sku.setId(this.snowflake.nextId()+""); // 为每一个sku设置id
                sku.setCreateTime(date); // 创建日期 如果是修改则保存原有的创建日期
            }

            //sku名称 =spu名称+规格值列表
            String skuName = spu.getName();
            String spec = sku.getSpec();// 获取规格列表，此时为字符串 例子：{"颜色":"红","机身内存":"64G"}

            //如果商品没有spec参数，那么spec数据可以是为空或者空字符，所以在此处需要稍做判断
            if (spec == null && "".equals(spec)){ // 判断字符串是否为空，或者空字符串
                spec = "{}" ;
            }
            Map<String,String> map = JSON.parseObject(spec, Map.class);// 将字符传递spec转化为json，并在转换为map
            for (String specValue:map.values()){ // map.values() 方法可以获取所有的value对象，例子获取的对象就是红，64G
                skuName += " " + specValue ; // 每个规格之间都有空格隔开
            }
            sku.setName(skuName); // 保存sku名称
            sku.setUpdateTime(date); // 更新日期
            sku.setSpuId(spu.getId()); // 设置spuID
            sku.setCategoryId(category.getId()); // 设置分类id，ID为spu的三级分类id
            sku.setCategoryName(category.getName()); // 设置分类名称，名称为三级分类名称
            sku.setSaleNum(0); // 设置销量为0
            sku.setCommentNum(0); // 设置评论为0
            this.skuMapper.insert(sku); // 保存sku对象
        }
        if (isSave){
            this.spuMapper.insert(spu); // 保存spu数据
        }




        // 商品分类表的关联
        if (isSave){ //判断是否是新增
            CategoryBrand categoryBrand = new CategoryBrand(); // 创建一个CategoryBrand实体类
            categoryBrand.setBrandId(spu.getBrandId()); // 设置 品牌ID
            categoryBrand.setCategoryId(spu.getCategory3Id()); // 需要设置三级分类对应的品牌

            int categoryBrand_Count = this.categoryBrandMapper.selectCount(categoryBrand);// 可以查询count，把实体类作为条件查询
            if (categoryBrand_Count==0){ // 说明该品牌与分类还没有关联，如果大于0表示之前的商品已经关联过了，不需要重复插入数据
                this.categoryBrandMapper.insert(categoryBrand); // 添加数据
            }
        } else { //修改

            Example example = new Example(Spu.class);
            Example.Criteria criteria = example.createCriteria();
            Integer id = rowSup.getBrandId();
            criteria.andEqualTo("brandId",rowSup.getBrandId()); // 设置条件
            criteria.andEqualTo("category3Id",rowSup.getCategory3Id()); // 设置条件

            List<Spu> spus = this.spuMapper.selectByExample(example);
            int i = this.spuMapper.selectCountByExample(example);//查询BrandID与category3Id相同的数据
            if (i==0){ // 由于前面的spu更新了表数据，所欲查询不到本身的数据，如果查询到其他的数据，则i会大于0

                Example example1 = new Example(CategoryBrand.class);
                Example.Criteria criteria1 = example1.createCriteria();
                criteria1.andEqualTo("brandId",rowSup.getBrandId());
                criteria1.andEqualTo("categoryId",rowSup.getCategory3Id());
                List<CategoryBrand> categoryBrands = this.categoryBrandMapper.selectByExample(example1);
                this.categoryBrandMapper.deleteByExample(example1); // 就可以删除这个品牌和分类的关联

                // 重新添加数据
                CategoryBrand categoryBrand = new CategoryBrand(); // 创建一个CategoryBrand实体类
                categoryBrand.setBrandId(spu.getBrandId()); // 设置 品牌ID
                categoryBrand.setCategoryId(spu.getCategory3Id()); // 需要设置三级分类对应的品牌

                int categoryBrand_Count = this.categoryBrandMapper.selectCount(categoryBrand);// 可以查询count，把实体类作为条件查询
                if (categoryBrand_Count==0){ // 说明该品牌与分类还没有关联，如果大于0表示之前的商品已经关联过了，不需要重复插入数据
                    this.categoryBrandMapper.insert(categoryBrand); // 添加数据
                }
            }else{
                CategoryBrand categoryBrand = new CategoryBrand(); // 创建一个CategoryBrand实体类
                categoryBrand.setBrandId(spu.getBrandId()); // 设置 品牌ID
                categoryBrand.setCategoryId(spu.getCategory3Id()); // 需要设置三级分类对应的品牌

                int categoryBrand_Count = this.categoryBrandMapper.selectCount(categoryBrand);// 可以查询count，把实体类作为条件查询
                if (categoryBrand_Count==0){ // 说明该品牌与分类还没有关联，如果大于0表示之前的商品已经关联过了，不需要重复插入数据
                    this.categoryBrandMapper.insert(categoryBrand); // 添加数据
                }
            }
        }
    }


    /**
     * 根据ID，查询Spu与Sku
     * @param id spuId
     * @return
     */
    @Override
    public Goods findGoodsById(String id) {
        Spu spu = this.spuMapper.selectByPrimaryKey(id); // 查询spu 对象

        //查询spu对应的sku对象列表
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId",id); // 设置查询条件

        List<Sku> skuList = this.skuMapper.selectByExample(example);//查询结果

        //封装数据
        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);

        return goods;
    }


    @Autowired
    private AuditLogMapper auditLogMapper ;

    @Autowired
    private SpuMarketableLogMapper spuMarketableLogMapper;

    /**
     * 实现商品审核
     * @param spuId 需要审核的商品id
     * @param status 审核状态
     * @param message 审核信息
     */
    @Override
    @Transactional
    public void audit(String spuId, String status, String message) {

        // 实现审核跟新
        Spu spu = new Spu() ;
        spu.setId(spuId);
        spu.setStatus(status);
        if ("1".equals(status)){ //审核通过，自动上架
            spu.setIsMarketable("1");
        }
        //通用map会自动查询通过Id主键查询数据，并修改值，如果是selective方法，空值为自动忽略
        this.spuMapper.updateByPrimaryKeySelective(spu); // 跟新数据


        //实现商品日志
        AuditLog auditLog = new AuditLog();
        auditLog.setId(this.snowflake.nextId()+""); //创建ID
        auditLog.setStatus(status); // 保存商品状态
        if (StringUtils.isNullOrEmpty(message)){ // 如果消息为空或者空字符
            message = "";
        }
        auditLog.setMessage(message); // 保存商品审核消息
        auditLog.setAuditTime(new Date());
        auditLog.setSpuId(spuId); // 设置审核的商品ID
        this.auditLogMapper.insert(auditLog);

        //商品修改日志,
        this.markeTableLog(spuId,"0","1");
    }


    /**
     * 下架商品
     * @param id 商品ID
     */
    @Override
    @Transactional
    public void pull(String id) {
        Spu spu = this.spuMapper.selectByPrimaryKey(id);
        String spuIsMarketable = spu.getIsMarketable(); // 保存为修改的数值
        spu.setIsMarketable("0"); // 下架商品
        this.spuMapper.updateByPrimaryKeySelective(spu);//更新数据
        //商品日志
        this.markeTableLog(id, spuIsMarketable,"0");
    }

    /**
     * 上架商品
     * @param id 商品ID
     */
    @Override
    @Transactional
    public void put(String id) {
        Spu spu = this.spuMapper.selectByPrimaryKey(id); // 查询商品
        if (!"1".equals(spu.getStatus())){ //如果商品没有通过审核，则抛出异常
            throw new RuntimeException("没有通过审核不能上架商品");
        }
        String isMarketable = spu.getIsMarketable();// 保存未修改前的状态
        spu.setIsMarketable("1"); // 上架商品
        this.spuMapper.updateByPrimaryKeySelective(spu);//更新数据
        this.markeTableLog(id,isMarketable,"1");
    }

    /**
     * 实现批量上架商品
     * @param ids
     */
    @Override
    @Transactional
    public int putMany(String[] ids) {
        Spu spu = new Spu();
        spu.setIsMarketable("1"); // 设置上架状态为1
        Example example = new Example(Spu.class);

        Example.Criteria criteria = example.createCriteria();
        List<String> stringList = Arrays.asList(ids);
        criteria.andIn("id", stringList); // 符合id为ids的集合数据
        criteria.andEqualTo("status","1"); // 选出审核通过的商品
        criteria.andEqualTo("isMarketable","0");//选出下架的商品，过滤掉上架商品
        List<Spu> spuListSelect = this.spuMapper.selectByExample(example); // 查数需要记录的数组数据，如果更新之后再查就查不到了
        int count = this.spuMapper.updateByExampleSelective(spu, example);//传递需要修改的数值，以及条件

        //商品上下架日志
        List<Spu> spuList = spuListSelect;
        for (Spu data:spuList){
            this.markeTableLog(data.getId(),"0","1");// 传递原始值与修改值
        }

        return count ; // 返回更新的数量
    }


    /**
     * 实现批量下架
     * @param ids 一组需要下架的商品ID
     * @return 返回更新数量
     */
    @Override
    @Transactional
    public int pullMany(String[] ids) {
        Spu spu = new Spu();
        spu.setIsMarketable("0"); // 设置商品下架

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",Arrays.asList(ids)); // 找出ids数组中的所有数据
        criteria.andEqualTo("isMarketable","1");//选出还在上架的商品，过滤掉已经下架的商品

        List<Spu> spuList = this.spuMapper.selectByExample(example); // 在修改数据前，先查询需要修改的数据
        int count = this.spuMapper.updateByExampleSelective(spu, example);

        // 日志部分
        for(Spu data:spuList){
            this.markeTableLog(data.getId(),"1","0");
        }

        return count;
    }

    /**
     * 实现逻辑删除，修改is_delete字段
     * @param id
     */
    @Override
    public void logicDelete(String id) {
        Spu spu = new Spu();
        spu.setId(id);
        spu.setIsDelete("1");//表示该商品已经删除
        this.spuMapper.updateByPrimaryKeySelective(spu);//更新数据
    }

    /**
     * 还原删除数据
     * @param id 被删除的商品ID
     */
    @Override
    public void restoreSpu(String id) {
        Spu spu = new Spu();
        spu.setId(id);
        spu.setIsDelete("0");//还原删除的商品
        this.spuMapper.updateByPrimaryKeySelective(spu);
    }


    /**
     * 保存商品上下架日志
     * @param spuId 商品ID
     * @param RowIsmaketable 商品修改前数据
     * @param UpdateIsmaketable 商品修改后数据
     */
    private void markeTableLog(String spuId,String RowIsmaketable,String UpdateIsmaketable){
        SpuMarketableLog spuMarketableLog = new SpuMarketableLog();
        spuMarketableLog.setRowIsmaketable(RowIsmaketable); // 设置保存前的上架状态
        spuMarketableLog.setUpdateIsmaketable(UpdateIsmaketable);  // 设置保存的上架状态
        spuMarketableLog.setUpdateIsmaketableTime(new Date()); // 设置保存日期
        spuMarketableLog.setId(this.snowflake.nextId()+""); // 设置ID
        spuMarketableLog.setSpuId(spuId); // 设置spuID
        this.spuMarketableLogMapper.insert(spuMarketableLog); // 保存日志
    }

    /**
     * 构建默认查询条件
     * @return
     */
    private Example createExample(){
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("isDelete","1"); // 过滤掉已经删除掉数据
        return example ;
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 主键
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andLike("id","%"+searchMap.get("id")+"%");
            }
            // 货号
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andLike("sn","%"+searchMap.get("sn")+"%");
            }
            // SPU名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 副标题
            if(searchMap.get("caption")!=null && !"".equals(searchMap.get("caption"))){
                criteria.andLike("caption","%"+searchMap.get("caption")+"%");
            }
            // 图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
            }
            // 售后服务
            if(searchMap.get("saleService")!=null && !"".equals(searchMap.get("saleService"))){
                criteria.andLike("saleService","%"+searchMap.get("saleService")+"%");
            }
            // 介绍
            if(searchMap.get("introduction")!=null && !"".equals(searchMap.get("introduction"))){
                criteria.andLike("introduction","%"+searchMap.get("introduction")+"%");
            }
            // 规格列表
            if(searchMap.get("specItems")!=null && !"".equals(searchMap.get("specItems"))){
                criteria.andLike("specItems","%"+searchMap.get("specItems")+"%");
            }
            // 参数列表
            if(searchMap.get("paraItems")!=null && !"".equals(searchMap.get("paraItems"))){
                criteria.andLike("paraItems","%"+searchMap.get("paraItems")+"%");
            }
            // 是否上架
            if(searchMap.get("isMarketable")!=null && !"".equals(searchMap.get("isMarketable"))){
                criteria.andLike("isMarketable","%"+searchMap.get("isMarketable")+"%");
            }
            // 是否启用规格
            if(searchMap.get("isEnableSpec")!=null && !"".equals(searchMap.get("isEnableSpec"))){
                criteria.andLike("isEnableSpec","%"+searchMap.get("isEnableSpec")+"%");
            }
            // 是否删除
            if(searchMap.get("isDelete")!=null && !"".equals(searchMap.get("isDelete"))){
                criteria.andLike("isDelete","%"+searchMap.get("isDelete")+"%");
            }
            // 审核状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
            }

            // 品牌ID
            if(searchMap.get("brandId")!=null ){
                criteria.andEqualTo("brandId",searchMap.get("brandId"));
            }
            // 一级分类
            if(searchMap.get("category1Id")!=null ){
                criteria.andEqualTo("category1Id",searchMap.get("category1Id"));
            }
            // 二级分类
            if(searchMap.get("category2Id")!=null ){
                criteria.andEqualTo("category2Id",searchMap.get("category2Id"));
            }
            // 三级分类
            if(searchMap.get("category3Id")!=null ){
                criteria.andEqualTo("category3Id",searchMap.get("category3Id"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
            // 运费模板id
            if(searchMap.get("freightId")!=null ){
                criteria.andEqualTo("freightId",searchMap.get("freightId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
