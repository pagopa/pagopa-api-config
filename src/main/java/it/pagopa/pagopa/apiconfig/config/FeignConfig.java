//package it.pagopa.pagopa.apiconfig.config;
//
//import feign.RequestInterceptor;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//
//import static it.pagopa.pagopa.apiconfig.util.Constants.HEADER_REQUEST_ID;
//import static it.pagopa.pagopa.apiconfig.util.Constants.HEADER_SUBSCRIPTION_KEY;
//
//
//public class FeignConfig {
//
//  @Value("${service.utils.subscriptionKey}")
//  private String subscriptionKey;
//
//  @Bean
//  public RequestInterceptor requestIdInterceptor() {
//    return requestTemplate -> requestTemplate.header(HEADER_REQUEST_ID, MDC.get("requestId"));
//  }
//
//  @Bean
//  public RequestInterceptor subscriptionKeyInterceptor() {
//    return requestTemplate -> requestTemplate.header(HEADER_SUBSCRIPTION_KEY, subscriptionKey);
//  }
//}
