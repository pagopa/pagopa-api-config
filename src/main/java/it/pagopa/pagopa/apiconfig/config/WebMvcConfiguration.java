package it.pagopa.pagopa.apiconfig.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pagopa.apiconfig.model.AppCorsConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Value("${cors.configuration}")
    private String corsConfiguration;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MDCInterceptor());
    }

    @SneakyThrows
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        AppCorsConfiguration appCorsConfiguration = new ObjectMapper().readValue(corsConfiguration, AppCorsConfiguration.class);
        registry.addMapping("/**")
                .allowedOrigins(appCorsConfiguration.getOrigins())
                .allowedMethods(appCorsConfiguration.getMethods());
    }

    @Slf4j
    public static class MDCInterceptor implements HandlerInterceptor {


        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            MDC.put("requestId", UUID.randomUUID().toString());
            log.debug("{} {}", request.getMethod(), request.getRequestURI());
            return true;
        }

        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
            MDC.clear();
        }


    }
}


