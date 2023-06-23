package it.gov.pagopa.apiconfig.core.scheduler.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableServiceEntity;
import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.scheduler.entity.CreditorInstitutionIcaFile;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class AzureStorageInteraction {

  @Value("${creditor.institution.table.connection.string}")
  private String storageConnectionString;

  @Value("${creditor.institution.update.table}")
  private String icaTable;

  private boolean debugAzurite = Boolean.parseBoolean(System.getenv("DEBUG_AZURITE"));

  public AzureStorageInteraction(String storageConnectionString, String icaTable) {
    this.storageConnectionString = storageConnectionString;
    this.icaTable = icaTable;
  }

  public Map<String, String> getUpdatedEC(String lastUpdate) throws AppException {
    Spliterator<CreditorInstitutionIcaFile> resultOrganizationIcaList = null;
    try {
      CloudTable table =
          CloudStorageAccount.parse(storageConnectionString)
              .createCloudTableClient()
              .getTableReference(this.icaTable);
      resultOrganizationIcaList =
          table
              .execute(
                  TableQuery.from(CreditorInstitutionIcaFile.class)
                      .where(
                          TableQuery.generateFilterCondition(
                              "PublicationDate",
                              TableQuery.QueryComparisons.GREATER_THAN,
                              lastUpdate)))
              .spliterator();
    } catch (InvalidKeyException | URISyntaxException | StorageException e) {
      // unexpected error
      throw new AppException(AppError.AZURE_STORAGE_ERROR);
    }
    return StreamSupport.stream(resultOrganizationIcaList, false)
        .collect(
            Collectors.toMap(
                TableServiceEntity::getRowKey, CreditorInstitutionIcaFile::getPublicationDate));
  }

  public void updateECIcaTable(String idDominio){
    try {
      CloudTable table = CloudStorageAccount.parse(storageConnectionString).createCloudTableClient()
          .getTableReference(this.icaTable);
      table.execute(TableOperation
          .insertOrReplace(
              new CreditorInstitutionIcaFile(
                  idDominio,
                  LocalDateTime.now().toString()
              )
          ));
    } catch (InvalidKeyException | URISyntaxException | StorageException e) {
      throw new AppException(AppError.AZURE_STORAGE_ERROR);
    }
  }
}
