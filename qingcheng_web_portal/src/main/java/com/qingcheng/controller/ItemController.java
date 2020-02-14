package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.goods.Category;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 测试spu_Id = 103187468200
 *
 * 处理商品详情页的请求
 */
@Controller
@RequestMapping("/item")
public class ItemController {

    @Reference
    private SpuService spuService;

    @Reference
    private CategoryService categoryService;

    @Value("${item.pagePath}")  //此标签可以通过properties中获取数据
    private String pagePath;

    @Autowired
    private TemplateEngine templateEngine ;


    @ResponseBody
    @GetMapping("/createPage")
    public Result createPage(String spuId){

        Result result = new Result(); // 返回操作状态

        // 查询数据
        Goods goods = this.spuService.findGoodsById(spuId); // 查询商品信息
        // 取出数据
        Spu spu = goods.getSpu();
        List<Sku> skuList = goods.getSkuList();


        // 商品分类数据
        List<String> categoryNameList = new ArrayList<String>();
        String categoryName1 = this.categoryService.findById(spu.getCategory1Id()).getName();
        String categoryName2 = this.categoryService.findById(spu.getCategory2Id()).getName();
        String categoryName3 = this.categoryService.findById(spu.getCategory3Id()).getName();
        // 添加顺序不能乱
        categoryNameList.add(categoryName1);
        categoryNameList.add(categoryName2);
        categoryNameList.add(categoryName3);


        // spu商品图片
        List<String> spuImages = Arrays.asList(spu.getImages().split(","));// 拆分images 并且用逗号区分

        // spuParaItem参数,需要加入判断
        String paraItems = spu.getParaItems();
        Map spuParaItems = null;
        if (paraItems!=null&& !"".equals(paraItems)){
            spuParaItems = JSON.parseObject(paraItems, Map.class); // 转换json
        }


        // 构建urlMap，存放所有到sku的跳转路径
        Map<String,Object> urlMap = new HashMap<String, Object>();
        for (Sku sku:skuList){
            // sku必须属于正常状态，否则不构建url
            if ("1".equals(sku.getStatus())){

                // 由于json转换可能顺序不一样导致最后转换到json不一致，所以这里通过两次转换保证转换出来的json保持一致
                Map specMap = JSON.parseObject(sku.getSpec(), Map.class);
                String spec = JSON.toJSONString(specMap, SerializerFeature.MapSortField);

                urlMap.put(spec,sku.getId()+".html"); // 传入到urlMap中
            }
        }


        for (Sku sku:skuList){


            // skuSpec参数,需要加入不为空的判断
            String skuSpec = sku.getSpec();
            Map<String,String > skuSpecs = null;
            if (skuSpec!=null&&!"".equals(skuSpec)){
                skuSpecs = JSON.parseObject(skuSpec, Map.class); // 转换json
            }



            // spuSpec规格，商品拥有到所有规格
            String spuSpecItemsJson = spu.getSpecItems(); // 获取商品spu的所有规格
            Map <String,List<Map>> spuSpecMap = new HashMap<String, List<Map>>() ; // spu规格列表
            if (spuSpecItemsJson!=null && !"".equals(spuSpecItemsJson)){

                Map<String,List> spuSpecs = JSON.parseObject(spuSpecItemsJson,Map.class);// json转map，转换spu到spec列表

                /**
                 * 转换格式格式
                 * 转换前格式：{"颜色":["红色","紫色","蓝色","茶色","灰色"],"尺码":["250度","200度","350度","100度","150度","400度","300度"]}
                 * 转换后格式：{"颜色":[{isSelect:true ,specName:"红色"},{isSelect:true ,specName:"紫色"},...], "尺码":[{isSelect:true,specName:"250度"},..] ,..}
                 */
                for (String spuSpecsKey:spuSpecs.keySet()){ // 循环spec的Map取出value
                    String key = spuSpecsKey ; // 保存key
                    List<Map> value = new ArrayList<Map>(); // 保存value

                    List<String> specsValueList = spuSpecs.get(spuSpecsKey); // 取出spuMap里的Value


                    for (String specValue:specsValueList){
                        Map<String,Object> spec = new HashMap<String, Object>();

                        String skuSpecsValue = skuSpecs.get(spuSpecsKey); // 通过循环的spu的Spec的key，取出sku的spec里的value

                        if (skuSpecsValue.equals(specValue)){
                            spec.put("checked",true); // 选中
                        } else {
                            spec.put("checked",false); // 没有选中
                        }
                        spec.put("option",specValue); // 添加属性

                        // 通过sku取出spec的json数据，转换成map
                        Map map = JSON.parseObject(sku.getSpec(), Map.class);
                        map.put(spuSpecsKey,specValue);// 由于当前正在构建规格选择面板，所以每个规格选项都会被遍历一遍，添加数据其实会将其覆盖
                        String urlKey = JSON.toJSONString(map,SerializerFeature.MapSortField); // 由于json转换可以顺序不一样，转换出来的json也不一样，所以加一个参数用于排序
                        spec.put("url",urlMap.get(urlKey)); //通过前面构建到urlMap，通过key取出url，存放到url属性中，

                        value.add(spec); // 添加到value
                    }
                    spuSpecMap.put(key,value); // 添加到属性
                }
            }






            // sku 图片
            List<String> skuImages = Arrays.asList(sku.getImages().split(",")); // 图片列表

            // 保存图片
            List<String> imagesList = new ArrayList<String>();
            imagesList.addAll(skuImages);
            imagesList.addAll(spuImages);


            // 创建数据模型Map
            Map<String, Object> dataModel = new HashMap();
            dataModel.put("spu",spu); // spu信息
            dataModel.put("sku",sku); // sku信息
            dataModel.put("categoryNameList",categoryNameList); // 分类信息
            dataModel.put("images",imagesList);// 图片列表
            dataModel.put("spuParas",spuParaItems); // spu 参数列表
            dataModel.put("skuSpecs",skuSpecs);  // sku 参数列表
            dataModel.put("spuSpecsItems",spuSpecMap); // spu 规格参数(用于规格选择)

            Context context = new Context(); // 创建上下文
            context.setVariables(dataModel); // 设置数据模型

            // 判断路径文件是否存在
            File pagePathFile = new File(this.pagePath);
            if(!pagePathFile.exists()){
                pagePathFile.mkdirs();
            }

            try {

                // 准备输出文件
                File itemFile = new File(this.pagePath + sku.getId() + ".html");
                PrintWriter printWriter = new PrintWriter(itemFile,"UTF-8");

                // 由于模版引擎是从spring配置文件中注入进来，引擎中已经有了资源解析器，所以默认会从WEB-INF/template 下寻找模版
                this.templateEngine.process("item",context,printWriter);

                // 异常处理
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                result.setCode(1);
                result.setMessage(e.getMessage());
                return result ;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                result.setCode(1);
                result.setMessage(e.getMessage());
                return result;
            }

        }

        return result;
    }
}
