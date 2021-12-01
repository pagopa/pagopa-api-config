package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.config.StorageProperties;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

@Service
public class StorageService {

    private final Path rootLocation;

    @Autowired
    public StorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "StorageService", "Could not initialize storage", e);
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public File store(MultipartFile file) {
        File savedFile = null;
        try {
            if (file.isEmpty()) {
                throw new AppException(HttpStatus.BAD_REQUEST, "Storage Service", "Failed to store empty file.", null);
            }
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Storage Service", "Cannot store file outside current directory.", null);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                savedFile = destinationFile.toFile();
            }
        }
        catch (IOException e) {
            throw new AppException(HttpStatus.INTERNAL_SERVER_ERROR, "Storage Service", "Failed to store file.", e);
        }
        return savedFile;
    }

}
