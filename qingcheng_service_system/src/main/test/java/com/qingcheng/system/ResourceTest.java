package com.qingcheng.system;


import com.qingcheng.dao.ResourceMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ResourceTest extends CommonTest{

    @Autowired
    private ResourceMapper resourceMapper ;

    @Test
    public void Resource_findResKeyLoginName(){

        List<String> resourceList = this.resourceMapper.selectByResKeyByLoginName("admin");
        System.out.println(resourceList);
    }
}
