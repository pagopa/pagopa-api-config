package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import static org.springframework.http.HttpMethod.*;
import java.util.List;

@Service
@Validated
public class RefreshService {
    private static final List<String> VALID_JOB_LIST = List.of("refreshConfiguration", "paInviaRt",
        "paRetryPaInviaRtNegative", "paInviaRtRecovery", "paSendRt");

    public static final List<String> VALID_CONFIG_LIST = List.of("FTP_SERVER", "INFORMATIVA_CDI",
        "INFORMATIVA_PA", "PA", "PDD", "PSP");

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.trigger.job.host}")
    private String jobTriggerUrl;

    @Value("${service.trigger.job.subscriptionKey}")
    private String jobServiceSubscriptionKey;

    @Value("${service.refresh-config.domains.host}")
    private String refreshConfigUrl;

    @Value("${service.refresh-config.domains.subscriptionKey}")
    private String refreshConfigSubscriptionKey;

    public String jobTrigger(String jobType) {
        if(VALID_JOB_LIST.contains(jobType))
          return get(jobTriggerUrl + "/" + jobType, jobServiceSubscriptionKey).getBody();
        else throw new AppException(AppError.INTERNAL_SERVER_ERROR);
    }

    public String refreshConfig(String configType) {
      if(VALID_CONFIG_LIST.contains(configType))
        return get(refreshConfigUrl + "/" + configType, refreshConfigSubscriptionKey).getBody();
      else throw new AppException(AppError.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<String> get(String url, String subscriptionKey) {
        try {
          HttpHeaders headers = new HttpHeaders();
          headers.set("Ocp-Apim-Subscription-Key", subscriptionKey);
          HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
          ResponseEntity<String> responseE = restTemplate.exchange(url, GET, requestEntity, String.class);

          if (!responseE.getStatusCode().is2xxSuccessful() && !responseE.getStatusCode().equals(HttpStatus.NOT_FOUND))
            throw new AppException(AppError.INTERNAL_SERVER_ERROR);

          return responseE;
        } catch(HttpClientErrorException e){
          throw new AppException(AppError.INTERNAL_SERVER_ERROR);
        }
    }
}
