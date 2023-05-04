package com.tuling.tulingmall.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
* @vlog: 高于生活，源于生活
* @desc: 类的描述:ribbon客户端配置
* @author: smlz
* @version: 1.0
*/
@Configuration
public class RibbonConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
