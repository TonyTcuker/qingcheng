package com.tony.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)

// IMac路径地址
@ContextConfiguration(locations = "file:/Users/tony/IdeaProjects/qingcheng_parent2/qingcheng_service_goods/src/main/test/resource/applicationContext-dao-test.xml")
public class CommonTest {
}
