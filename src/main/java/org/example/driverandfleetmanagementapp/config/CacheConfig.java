package org.example.driverandfleetmanagementapp.config;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;


@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCacheSpecification(
                "maximumSize=500,expireAfterWrite=15m,recordStats"
        );

        cacheManager.setCacheNames(Arrays.asList("vehicles", "drivers"));
        return cacheManager;
    }
}