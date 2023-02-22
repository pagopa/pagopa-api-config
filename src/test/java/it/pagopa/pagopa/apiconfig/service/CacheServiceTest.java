package it.pagopa.pagopa.apiconfig.service;

import static it.pagopa.pagopa.apiconfig.TestUtil.getMockCacheByVersion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import it.pagopa.pagopa.apiconfig.TestUtil;
import it.pagopa.pagopa.apiconfig.entity.Cache;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.repository.CacheRepository;
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
    Page<it.pagopa.pagopa.apiconfig.model.configuration.Cache> page =
        TestUtil.mockPage(
            Lists.newArrayList(
                it.pagopa.pagopa.apiconfig.model.configuration.Cache.builder()
                    .id("2023-02-08 01:00:06")
                    .version("3.10.0")
                    .build()),
            50,
            0);
    when(cacheRepository.findAllPaged(any(Pageable.class))).thenReturn(page);

    var result = cacheService.getCacheVersions(0, 50);
    assertNotNull(result);
  }
}
