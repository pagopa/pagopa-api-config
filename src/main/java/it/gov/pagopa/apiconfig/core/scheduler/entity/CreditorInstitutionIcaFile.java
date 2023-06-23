package it.gov.pagopa.apiconfig.core.scheduler.entity;

import com.microsoft.azure.storage.table.TableServiceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreditorInstitutionIcaFile extends TableServiceEntity {
  private String publicationDate;
  public static final String ORGANIZATION_KEY = "organization";

  public CreditorInstitutionIcaFile(String organizationFiscalCode, String publicationDate) {
    this.partitionKey = ORGANIZATION_KEY;
    this.rowKey = organizationFiscalCode;
    this.publicationDate = publicationDate;
  }
}
