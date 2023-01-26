package it.pagopa.pagopa.apiconfig.cosmos.container;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Container(containerName = "paymenttypes", autoCreateContainer = false)
public class PaymentTypesCosmos {

    @Id
    private String id;

    @PartitionKey
    private String name;

    private String description;
}
