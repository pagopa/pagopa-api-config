package it.pagopa.pagopa.apiconfig.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestFilter implements Filter {

    public static final String HEADER_REQUEST_ID = "X-Request-Id";

    /**
     * Get the request ID from the custom header "X-Request-Id" if present, otherwise it generates one.
     * Set the X-Request-Id value in the {@code response} and in the MDC
     *
     * @param request  http request
     * @param response http response
     * @param chain    next filter
     * @throws IOException      if an I/O error occurs during this filter's
     *                          processing of the request
     * @throws ServletException if the processing fails for any other reason
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest httRequest = (HttpServletRequest) request;

            // get requestId from header or generate one
            String requestId = httRequest.getHeader(HEADER_REQUEST_ID);
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            // set requestId in MDC
            MDC.put("requestId", requestId);
            log.debug("{} {}", httRequest.getMethod(), httRequest.getRequestURI());

            // set requestId in the response header
            ((HttpServletResponse) response).setHeader(HEADER_REQUEST_ID, requestId);
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

}
