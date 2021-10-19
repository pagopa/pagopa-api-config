package it.pagopa.pagopa.apiconfig.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.UUID;


@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private Environment environment;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MDCInterceptor());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // disable CORS only in local environments
        if (!Arrays.asList(environment.getActiveProfiles()).isEmpty()) {
            registry.addMapping("/**").allowedMethods("*");
        }
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


