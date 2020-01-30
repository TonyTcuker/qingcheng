package com.qingcheng.test;

import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AdTest extends CommonTest {

    @Autowired
    private AdService adService ;

    @Test
    public void Ad_findByPosition(){
        List<Ad> adList = this.adService.findByPosition("web_index_lb");
        System.out.println(adList);
    }
}
