package it.gov.pagopa.apiconfig.core.service;

import static it.gov.pagopa.apiconfig.TestUtil.getMockCacheByVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.service.CacheService;
import it.gov.pagopa.apiconfig.starter.entity.Cache;
import it.gov.pagopa.apiconfig.starter.repository.CacheRepository;

@SpringBootTest(classes = ApiConfig.class)
class CacheServiceTest {

  @MockBean private CacheRepository cacheRepository;

  @Autowired @InjectMocks private CacheService cacheService;

  @Test
  void getCache() {
    when(cacheRepository.findFirstByVersion(anyString(), any(Sort.class)))
        .thenReturn(getMockCacheByVersion());

    Cache result = cacheService.getCache("3.10.0");
    assertNotNull(result);
  }

  @Test
  void getCache_ko() {
    when(cacheRepository.findFirstByVersion(anyString(), any(Sort.class)))
        .thenReturn(Optional.empty());

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
    Page<Cache> page =
        TestUtil.mockPage(
            Lists.newArrayList(
                Cache.builder()
                    .id("2023-02-08 01:00:06")
                    .version("3.10.0")
                    .build()),
            50,
            0);
    when(cacheRepository.findAll(any(Pageable.class))).thenReturn(page);

    var result = cacheService.getCacheVersions(0, 50);
    assertNotNull(result);
  }
}
