package com.tuling.tulingmall.config;

import com.tuling.tulingmall.common.threadPool.TulingMallThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;

@Configuration
@EnableAsync
public class ThreadPoolConfiguration {

    @Bean("commonPool")
    public ExecutorService commonThreadPoolExecutor(){
        /**
         * 父子线程传值TransmittableThreadLocal使用踩坑-及相关知识拓展
         *  return new TulingMallThreadPoolExecutor("测试用例专用线程池",2,5).getLhrmsThreadPoolExecutor();
         */
        return new TulingMallThreadPoolExecutor("图灵商场公共线程池",16,50).getLhrmsThreadPoolExecutor();
    }
}
