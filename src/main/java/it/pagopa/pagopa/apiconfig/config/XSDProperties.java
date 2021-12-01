package it.pagopa.pagopa.apiconfig.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("xsd")
public class XSDProperties {

    private String ica;

    public String getIca() {
        return ica;
    }

    public void setIca(String ica) {
        this.ica = ica;
    }
}
