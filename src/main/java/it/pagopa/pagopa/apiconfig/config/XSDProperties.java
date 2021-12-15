package it.pagopa.pagopa.apiconfig.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("xsd")
@Getter
@Setter
public class XSDProperties {

    private String ica;
}
