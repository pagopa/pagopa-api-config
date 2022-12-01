//package it.pagopa.pagopa.apiconfig.entity;
//
//import com.azure.spring.data.cosmos.core.mapping.Container;
//import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//import lombok.experimental.SuperBuilder;
//
//import javax.persistence.Id;
//
//
//@Getter
//@Setter
//@ToString
//@NoArgsConstructor
//@AllArgsConstructor
//@SuperBuilder(toBuilder = true)
//@Container(containerName = "cdi")
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class CDI {
//
//    @Id
//    private String id;
//    @PartitionKey
//    private String idPsp;
//
//    private String name;
//    private String description;
//    private Integer paymentAmount;
//    private Integer minPaymentAmount;
//    private Integer maxPaymentAmount;
//    private String paymentMethod;
//    private String touchpoint;
//
//}
