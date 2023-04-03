package it.pagopa.pagopa.apiconfig.service;

import java.lang.reflect.Type;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import it.gov.pagopa.apiconfig.starter.entity.Cache;
import it.gov.pagopa.apiconfig.starter.repository.CacheRepository;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.configuration.CacheVersions;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;

@Service
@Validated
@Transactional
public class CacheService {

  @Autowired CacheRepository cacheRepository;

  @Autowired ModelMapper modelMapper;

  public Cache getCache(String version) {
    return cacheRepository
        .findFirstByVersion(version, Sort.by(Sort.Direction.DESC, "time"))
        .orElseThrow(() -> new AppException(AppError.CACHE_NOT_FOUND, version));
  }

  public CacheVersions getCacheVersions(Integer page, Integer limit) {
    Sort sortOrder = Sort.by(Sort.Direction.DESC, "time").and(Sort.by("version"));
    Pageable pageable = PageRequest.of(page, limit, sortOrder);
    Page<Cache> data =
        cacheRepository.findAll(pageable);
    
    Type listType = new TypeToken<List<it.pagopa.pagopa.apiconfig.model.configuration.Cache>>(){}.getType();
    
    return CacheVersions.builder()
        .versionList(modelMapper.map(data.toList(), listType))
        .pageInfo(CommonUtil.buildPageInfo(data))
        .build();
  }
}
