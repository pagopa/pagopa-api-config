package it.pagopa.pagopa.apiconfig.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "CONFIGURATION_KEYS", schema = "NODO4_CFG")
@Builder(toBuilder = true)
public class ConfigurationKeys implements Serializable {

    @EmbeddedId
    private ConfigurationKeyPK id;

    @Column(name = "CONFIG_CATEGORY", nullable = false, length = 255)
    private String configCategory;

    @Column(name = "CONFIG_KEY", nullable = false, length = 255)
    private String configKey;

    @Column(name = "CONFIG_VALUE", nullable = false, length = 255)
    private String configValue;

    @Column(name = "CONFIG_DESCRIPTION", length = 255)
    private String configDescription;

    public ConfigurationKeys(String configCategory, String configDescription, String configKey, String configValue) {
        super();
        this.configCategory = configCategory;
        this.configDescription = configDescription;
        this.configKey = configKey;
        this.configValue = configValue;
    }
}
