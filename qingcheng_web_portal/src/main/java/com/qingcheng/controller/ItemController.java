package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.goods.Spu;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理商品详情页的请求
 */
@Controller
@RequestMapping("/item")
public class ItemController {

    @Reference
    private SpuService spuService;


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


        for (Sku sku:skuList){

            // 创建数据模型Map
            Map<String, Object> dataModel = new HashMap();
            dataModel.put("spu",spu);
            dataModel.put("sku",sku);

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
