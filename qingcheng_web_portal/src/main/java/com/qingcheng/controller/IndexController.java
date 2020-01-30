package com.qingcheng.controller;

import com.qingcheng.pojo.business.Ad;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    @GetMapping("/index")
    public String findByPosition(){
        return "index";
    }
}
