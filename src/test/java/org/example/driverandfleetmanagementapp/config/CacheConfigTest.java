package org.example.driverandfleetmanagementapp.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class CacheConfigTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    void cacheManagerShouldBeCreated(){
        assertThat(cacheManager).isNotNull();
    }

    @Test
    void cacheManagerShouldContainThisCaches() {
        assertThat(cacheManager.getCacheNames())
                .contains("vehicles")
                .contains("drivers");
    }
}


