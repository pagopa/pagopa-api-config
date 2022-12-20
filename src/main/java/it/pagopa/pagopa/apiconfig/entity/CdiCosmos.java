package it.pagopa.pagopa.apiconfig.entity;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Container(containerName = "cdis")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CdiCosmos {

    @Id
    private String id;
    @PartitionKey
    private String idPsp;
    private String idCdi;
    private String cdiStatus;
    private Boolean digitalStamp;
    private LocalDateTime validityDateFrom;
    private List<CdiDetailCosmos> details;
}
