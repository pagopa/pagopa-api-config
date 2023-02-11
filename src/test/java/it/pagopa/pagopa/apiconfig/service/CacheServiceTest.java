package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.entity.Cache;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.repository.CacheRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCacheByVersion;
import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCacheVersionsE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ApiConfig.class)
class CacheServiceTest {

    @MockBean
    private CacheRepository cacheRepository;

    @Autowired
    @InjectMocks
    private CacheService cacheService;

    @Test
    void getCache() {
        when(cacheRepository.findFirstByVersion(anyString(), any(Sort.class))).thenReturn(getMockCacheByVersion());

        Cache result = cacheService.getCache("3.10.0");
        assertNotNull(result);
    }

    @Test
    void getCache_ko() {
        when(cacheRepository.findFirstByVersion(anyString(), any(Sort.class))).thenReturn(Optional.empty());

        try {
            cacheService.getCache("3.10.0");
            fail();
        } catch (AppException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getHttpStatus());
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    void getCacheVersions() {
        when(cacheRepository.findAll(any(Sort.class))).thenReturn(getMockCacheVersionsE());

        List<it.pagopa.pagopa.apiconfig.model.configuration.Cache> result = cacheService.getCacheVersions();
        assertNotNull(result);
    }

}
