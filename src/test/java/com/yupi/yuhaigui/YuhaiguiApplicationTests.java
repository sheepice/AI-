package com.yupi.yuhaigui;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YuhaiguiApplicationTests {
    // 从环境变量中获取您的 API Key。此为默认方式，您可根据需要进行修改
    @Value("${ai.apiKey}")
    private String apiKey;
    @Test
    void contextLoads() {
        System.out.println(apiKey);
    }

}
