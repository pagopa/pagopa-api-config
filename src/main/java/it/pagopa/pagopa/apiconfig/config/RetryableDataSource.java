package it.pagopa.pagopa.apiconfig.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class RetryableDataSource extends AbstractDataSource {

    private final DataSource dataSource;


    @Override
    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 8000, multiplier = 2, maxDelay = 600000))
    public Connection getConnection() throws SQLException {
        log.debug("Trying to connect to the database...");
        return dataSource.getConnection();
    }

    @Override
    @Retryable(maxAttempts = 10, backoff = @Backoff(delay = 8000, multiplier = 2, maxDelay = 600000))
    public Connection getConnection(String username, String password) throws SQLException {
        log.debug("Trying to connect to the database by username and password ...");
        return dataSource.getConnection(username, password);
    }
}
