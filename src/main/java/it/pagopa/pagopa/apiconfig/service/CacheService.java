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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

        String filePath = "/Users/francesco/Development/PagoPA/pagopa-api-config/src/test/resources/file/massiveIcaValid.zip";
        // file to byte[], Path
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            cacheRepository.save(Cache.builder()
                    .id(LocalDateTime.now().toString().substring(0, 19))
                    .time(Timestamp.valueOf(LocalDateTime.now()))
                    .cache(bytes)
                    .version("3.10.0")
                    .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Sort sortOrder = Sort.by( Sort.Direction.DESC, "time").and(Sort.by("version"));
        var data = cacheRepository.findAll(sortOrder);
        return StreamSupport.stream(data.spliterator(), false).map(c -> modelMapper.map(c, it.pagopa.pagopa.apiconfig.model.configuration.Cache.class)).collect(Collectors.toList());
    }
}
