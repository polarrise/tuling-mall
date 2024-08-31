package com.tuling.tulingmall.rediscomm.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuling.tulingmall.rediscomm.util.RedisOpsExtUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

@Configuration
public class RedisExtConifg {

    // @Value("${spring.redis.cluster.nodes}")
    // String redisNodes;
    //
    @Value("${spring.redis.password}")
    String redisPass;


    @Autowired
    private RedisConnectionFactory connectionFactory;

    // @Bean("redisCluster")
    // @Primary
    // public RedisTemplate<String,Object> redisTemplate(){
    //     RedisTemplate<String,Object> template = new RedisTemplate();
    //     template.setConnectionFactory(connectionFactory);
    //     // 序列化工具
    //     Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer
    //             = new Jackson2JsonRedisSerializer<>(Object.class);
    //     ObjectMapper om = new ObjectMapper();
    //     om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    //     om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    //     jackson2JsonRedisSerializer.setObjectMapper(om);
    //
    //     StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    //     template.setKeySerializer(stringRedisSerializer);
    //     template.setValueSerializer(jackson2JsonRedisSerializer);
    //
    //     template.setHashKeySerializer(jackson2JsonRedisSerializer);
    //     template.setHashValueSerializer(jackson2JsonRedisSerializer);
    //
    //     template.afterPropertiesSet();
    //     return template;
    // }

    @Bean
    public RedisOpsExtUtil redisOpsUtil(){
        return new RedisOpsExtUtil();
    }

    // @Bean
    // public RedissonClient redissonClient(){
    //     Config config = new Config();
    //     ClusterServersConfig clusterServersConfig = config.useClusterServers();
    //      1.集群节点
    //         for (String node: redisNodes.split(",")){
    //             clusterServersConfig.addNodeAddress("redis://"+node);
    //         }
    //         clusterServersConfig.setPassword(redisPass);
    //     return Redisson.create(config);
    // }

    // ---  以上是redis集群 配置 ， 以下是本地redsi单机配置
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + "127.0.0.1" + ":" + 6379).setDatabase(0);
        if (!StringUtils.isEmpty(redisPass)) {
           config.useSingleServer().setPassword(redisPass);
        }
        return Redisson.create(config);
    }

    @Bean("redisSingle")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisSerializer<Object> serializer = redisSerializer();
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisSerializer<Object> redisSerializer() {
        //创建JSON序列化器
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }


}
