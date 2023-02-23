package it.pagopa.pagopa.apiconfig.config;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.retry.annotation.Retryable;

@Slf4j
@RequiredArgsConstructor
public class RetryableDataSource extends AbstractDataSource {

  private final DataSource dataSource;

  @Override
  @Retryable(maxAttempts = 1)
  public Connection getConnection() throws SQLException {
    log.trace("Trying to connect to the database...");
    return dataSource.getConnection();
  }

  @Override
  @Retryable(maxAttempts = 1)
  public Connection getConnection(String username, String password) throws SQLException {
    log.trace("Trying to connect to the database by username and password ...");
    return dataSource.getConnection(username, password);
  }
}
