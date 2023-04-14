package it.gov.pagopa.apiconfig.core.specification;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamento;

class PspCanaleTipoVersamentoSpecificationTest {
  
  @Test
  void createQuerySpecification() {
    Specification<PspCanaleTipoVersamento> specification =
        PspCanaleTipoVersamentoSpecification.filterViewPspChannelBroker(null, null, null, null, null);
    assertNotNull(specification);
    
    specification =
        PspCanaleTipoVersamentoSpecification.filterViewPspChannelBroker("123", "123", "123", "123", "123");
    assertNotNull(specification);
    
  }

}
