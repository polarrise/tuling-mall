package com.tuling.tulingmall.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis配置类
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.tuling.tulingmall.mapper","com.tuling.tulingmall.dao"})
public class MyBatisConfig {
}
