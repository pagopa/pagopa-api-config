package it.pagopa.pagopa.apiconfig.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Id;

public class ConfigurationKeysView implements Serializable {

  @Id
  @Column(name = "CONFIG_CATEGORY", nullable = false)
  private String configCategory;

  @Id
  @Column(name = "CONFIG_KEY", nullable = false)
  private String configKey;
}
