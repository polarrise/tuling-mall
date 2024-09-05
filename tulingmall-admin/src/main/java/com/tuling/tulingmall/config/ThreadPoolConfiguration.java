package com.tuling.tulingmall.config;

import com.tuling.tulingmall.common.threadPool.TulingMallThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

@Configuration
public class ThreadPoolConfiguration {

    @Bean("commonPool")
    public ExecutorService commonThreadPoolExecutor(){
        return new TulingMallThreadPoolExecutor("图灵商场公共线程池",16,50).getLhrmsThreadPoolExecutor();
    }
}
