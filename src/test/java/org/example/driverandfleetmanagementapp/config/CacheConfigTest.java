package org.example.driverandfleetmanagementapp.config;


import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
public class CacheConfigTest {

    @Test
    void cacheManagerShouldBeCreated() {
        CacheConfig cacheConfig = new CacheConfig();
        CacheManager cacheManager = cacheConfig.cacheManager();
        assertThat(cacheManager).isNotNull();
    }

    @Test
    void cacheManagerShouldContainThisCaches() {
        CacheConfig cacheConfig = new CacheConfig();
        CacheManager cacheManager = cacheConfig.cacheManager();
        assertThat(cacheManager.getCacheNames())
                .contains("vehicles")
                .contains("drivers");
    }
}


