package it.gov.pagopa.apiconfig.core.service;

import it.gov.pagopa.apiconfig.ApiConfig;
import it.gov.pagopa.apiconfig.TestUtil;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.starter.entity.Cache;
import it.gov.pagopa.apiconfig.starter.repository.CacheRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static it.gov.pagopa.apiconfig.TestUtil.getMockCacheByVersion;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
    Page<Cache> page = new PageImpl<>(List.of(Cache.builder().id("2023-02-08 01:00:06").version("3.10.0").time(ZonedDateTime.now()).build()));
    when(cacheRepository.findByVersionLike(anyString(), any(Pageable.class))).thenReturn(page);

    var result = cacheService.getCacheVersions(0, 50);
    assertNotNull(result);
    assertTrue(!result.getVersionList().isEmpty());
  }
}
