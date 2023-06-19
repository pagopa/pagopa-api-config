package it.gov.pagopa.apiconfig.core.scheduler.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.TableQuery;
import com.microsoft.azure.storage.table.TableServiceEntity;
import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.scheduler.entity.CreditorInstitutionIcaFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class AzureStorageInteraction {

  private String storageConnectionString = "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;TableEndpoint=http://127.0.0.1:10002/devstoreaccount1;QueueEndpoint=http://127.0.0.1:10001/devstoreaccount1;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1;";

  @Value("${creditor.institution.update.table}")
  private String icaTable;

  public Map<String, String> getUpdatedEC(String lastUpdate) throws AppException {
    Spliterator<CreditorInstitutionIcaFile> resultOrganizationIcaList = null;
    try {
      CloudTable table = CloudStorageAccount.parse(storageConnectionString).createCloudTableClient()
          .getTableReference(this.icaTable);
      resultOrganizationIcaList =
          table.execute(TableQuery.from(CreditorInstitutionIcaFile.class)
                  .where(TableQuery.generateFilterCondition("PublicationDate", TableQuery.QueryComparisons.GREATER_THAN, lastUpdate)))
              .spliterator();
    } catch (InvalidKeyException | URISyntaxException | StorageException e) {
      // unexpected error
      throw new AppException(AppError.AZURE_STORAGE_ERROR);
    }
    return StreamSupport.stream(resultOrganizationIcaList, false).collect(
        Collectors.toMap(
            TableServiceEntity::getRowKey,
            CreditorInstitutionIcaFile::getPublicationDate));
  }

}
