package com.winesasfood.admin.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonBloomFilterConfig {

    private static final String BLOOM_FILTER_KEY = "short-link:user-register:bloom-filter";

    @Bean
    public RBloomFilter<String> userRegisterBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(BLOOM_FILTER_KEY);
        // 预计插入数量100万，误判率0.01%
        if (!bloomFilter.isExists()) {
            bloomFilter.tryInit(1000000, 0.0001);
        }
        return bloomFilter;
    }
}
