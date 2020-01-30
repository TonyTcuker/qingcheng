package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    /**
     * 实现首页的跳转
     * @return
     */

    @Reference
    private AdService adService ;

    @GetMapping("/index")
    public String findByPosition(Model model){
        List<Ad> adList = this.adService.findByPosition("web_index_lb");
        model.addAttribute("lbt",adList);
        return "index";
    }
    @GetMapping("/index2")
    public String findByPosition2(Model model){
        List<Ad> adList = this.adService.findByPosition("web_index_lb");
        model.addAttribute("lbt",adList);
        return "index2";
    }
}
