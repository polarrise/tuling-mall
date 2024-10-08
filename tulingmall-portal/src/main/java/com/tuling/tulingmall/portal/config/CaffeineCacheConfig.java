package com.tuling.tulingmall.portal.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tuling.tulingmall.portal.domain.HomeContentResult;
import com.tuling.tulingmall.promotion.domain.FlashPromotionProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class CaffeineCacheConfig {

    @Bean(name = "promotion")
    public Cache<String, HomeContentResult> promotionCache() {
        int rnd = ThreadLocalRandom.current().nextInt(10);
        return Caffeine.newBuilder()
                // 设置最后一次写入经过固定时间过期
                .expireAfterWrite(30 + rnd, TimeUnit.MINUTES)
                // 初始的缓存空间大小
                .initialCapacity(20)
                // 缓存的最大条数
                .maximumSize(100)
                .removalListener(((key,value,cause)->{
                    log.info("缓存失效通知,key：{},原因：{}",key,cause);
                }))
                .build();
    }

    /*以双缓存的形式提升首页的访问性能，这个备份缓存其实运行过程中会永不过期
    * 可以作为首页的降级和兜底方案 * */
    @Bean(name = "promotionBak")
    public Cache<String, HomeContentResult> promotionCacheBak() {
        int rnd = ThreadLocalRandom.current().nextInt(10);
        return Caffeine.newBuilder()
                // 设置最后一次访问经过固定时间过期
                .expireAfterAccess(41 + rnd, TimeUnit.MINUTES)
                // 初始的缓存空间大小
                .initialCapacity(20)
                // 缓存的最大条数
                .maximumSize(100)
                .build();
    }

    /*秒杀信息在首页的缓存*/
    @Bean(name = "secKill")
    public Cache<String, List<FlashPromotionProduct>> secKillCache() {
        int rnd = ThreadLocalRandom.current().nextInt(400);
        return Caffeine.newBuilder()
                // 设置最后一次写入经过固定时间过期
                .expireAfterWrite(5000 + rnd, TimeUnit.MILLISECONDS)
                // 初始的缓存空间大小
                .initialCapacity(20)
                // 缓存的最大条数
                .maximumSize(100)
                .build();
    }

    /*秒杀信息在首页的缓存备份，提升首页的访问性能*/
    @Bean(name = "secKillBak")
    public Cache<String, List<FlashPromotionProduct>> secKillCacheBak() {
        int rnd = ThreadLocalRandom.current().nextInt(400);
        return Caffeine.newBuilder()
                // 设置最后一次写入经过固定时间过期
                .expireAfterWrite(1000 + rnd, TimeUnit.MILLISECONDS)
                // 初始的缓存空间大小
                .initialCapacity(20)
                // 缓存的最大条数
                .maximumSize(100)
                .build();
    }
}
