package it.gov.pagopa.apiconfig.core.mapper;

import javax.validation.Valid;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import it.gov.pagopa.apiconfig.core.model.configuration.FtpServer;
import it.gov.pagopa.apiconfig.starter.entity.FtpServers;

public class ConvertFtpServersToFtpServer implements Converter<FtpServers, FtpServer> {
  @Override
  public FtpServer convert(MappingContext<FtpServers, FtpServer> mappingContext) {
    @Valid FtpServers ftpServers = mappingContext.getSource();
    return FtpServer.builder()
        .host(ftpServers.getHost())
        .port(ftpServers.getPort())
        .username(ftpServers.getUsername())
        .password(ftpServers.getPassword())
        .rootPath(ftpServers.getRootPath())
        .service(ftpServers.getService())
        .type(ftpServers.getType())
        .inPath(ftpServers.getInPath())
        .outPath(ftpServers.getOutPath())
        .historyPath(ftpServers.getHistoryPath())
        .enabled(ftpServers.getEnabled())
        .build();
  }
}
