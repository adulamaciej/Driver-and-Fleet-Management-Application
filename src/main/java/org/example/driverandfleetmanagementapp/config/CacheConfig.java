package org.example.driverandfleetmanagementapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        log.debug("CacheManager configuration");
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCacheSpecification(
                "maximumSize=500,expireAfterWrite=15m,recordStats"
        );

        cacheManager.setCacheNames(Arrays.asList("vehicles", "drivers"));
        return cacheManager;
    }
}