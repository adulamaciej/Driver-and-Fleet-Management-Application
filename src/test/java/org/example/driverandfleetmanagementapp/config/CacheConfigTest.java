package org.example.driverandfleetmanagementapp.config;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
public class CacheConfigTest {

    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        CacheConfig cacheConfig = new CacheConfig();
        cacheManager = cacheConfig.cacheManager();
    }

    @Test
    void cacheManagerShouldBeCreated() {
        assertThat(cacheManager).isNotNull();
    }

    @Test
    void cacheManagerShouldContainThisCaches() {
        assertThat(cacheManager.getCacheNames())
                .contains("vehicles")
                .contains("drivers");
    }
}

