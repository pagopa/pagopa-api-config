package it.pagopa.pagopa.apiconfig.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class ConfigurationKeysView implements Serializable {

    @Id
    @Column(name = "CONFIG_CATEGORY", nullable = false, length = 255)
    private String configCategory;

    @Id
    @Column(name = "CONFIG_KEY", nullable = false, length = 255)
    private String configKey;

}
