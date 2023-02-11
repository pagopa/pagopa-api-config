package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.entity.Cache;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.repository.CacheRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Validated
public class CacheService {

    @Autowired
    CacheRepository cacheRepository;

    @Autowired
    ModelMapper modelMapper;

    public Cache getCache(String version) {
        return cacheRepository.findFirstByVersion(version, Sort.by(Sort.Direction.DESC, "time"))
                .orElseThrow(() -> new AppException(AppError.CACHE_NOT_FOUND, version));
    }

    public List<it.pagopa.pagopa.apiconfig.model.configuration.Cache> getCacheVersions() {

        Sort sortOrder = Sort.by( Sort.Direction.DESC, "time").and(Sort.by("version"));
        var data = cacheRepository.findAll(sortOrder);
        return StreamSupport.stream(data.spliterator(), true).map(c -> modelMapper.map(c, it.pagopa.pagopa.apiconfig.model.configuration.Cache.class)).collect(Collectors.toList());
    }
}
