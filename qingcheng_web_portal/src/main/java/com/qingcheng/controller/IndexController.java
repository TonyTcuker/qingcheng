package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import com.qingcheng.service.goods.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {


    @Reference
    private AdService adService ;

    @Reference
    private CategoryService categoryService ;
    /**
     * 实现首页的跳转
     * @return
     */
    @GetMapping("/index")
    public String findByPosition(Model model){
        // 查询首页轮播图
        List<Ad> adList = this.adService.findByPosition("web_index_lb");
        List<Map> indexCategory = this.categoryService.findIndexCategory();

        // 添加request
        model.addAttribute("lbt",adList);
        model.addAttribute("indexCategory",indexCategory);

        return "index";
    }
}
