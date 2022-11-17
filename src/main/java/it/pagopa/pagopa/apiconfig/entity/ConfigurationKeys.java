package it.pagopa.pagopa.apiconfig.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@IdClass(ConfigurationKeysView.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "CONFIGURATION_KEYS")
@Builder(toBuilder = true)
public class ConfigurationKeys implements Serializable {

    @Id
    @Column(name = "CONFIG_CATEGORY", nullable = false, length = 255)
    private String configCategory;

    @Id
    @Column(name = "CONFIG_KEY", nullable = false, length = 255)
    private String configKey;

    @Column(name = "CONFIG_VALUE", nullable = false, length = 255)
    private String configValue;

    @Column(name = "CONFIG_DESCRIPTION", length = 255)
    private String configDescription;

}
