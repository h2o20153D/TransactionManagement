package com.banking.transactionservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类
 * 使用Caffeine作为缓存实现
 */
@Configuration
public class CachingConfig {

    /**
     * 创建并配置缓存管理器
     * @return 配置好的CacheManager实例
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("transactions", "allTransactions");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)     // 初始容量为100
                .maximumSize(1000)        // 最大容量为1000
                .expireAfterWrite(10, TimeUnit.MINUTES)  // 写入10分钟后过期
                .recordStats());          // 记录缓存状态
        return cacheManager;
    }
} 