package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.ConfigurationDomain;
import it.pagopa.pagopa.apiconfig.model.JobTrigger;
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

@Service
@Validated
public class RefreshService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.trigger.job.host}")
    private String jobTriggerUrl;

    @Value("${service.refresh-config.domains.host}")
    private String refreshConfigUrl;

    public String jobTrigger(JobTrigger jobType) {
        return get(jobTriggerUrl + "/" + jobType).getBody();
    }

    public String refreshConfig(ConfigurationDomain configType) {
        String url;
        if(configType.equals(ConfigurationDomain.GLOBAL))
          url = jobTriggerUrl + "/refreshConfiguration";
        else url = refreshConfigUrl + "/" + configType;

        return get(url).getBody();
    }

    private ResponseEntity<String> get(String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
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
