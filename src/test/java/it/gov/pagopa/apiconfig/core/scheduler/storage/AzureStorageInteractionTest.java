package it.gov.pagopa.apiconfig.core.scheduler.storage;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.RetryNoRetry;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableRequestOptions;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.scheduler.entity.CreditorInstitutionIcaFile;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Testcontainers
@ExtendWith(MockitoExtension.class)
@java.lang.SuppressWarnings("secrets:S6338")
public class AzureStorageInteractionTest {

  @ClassRule
  @Container
  public static GenericContainer<?> azurite =
      new GenericContainer<>(
          DockerImageName.parse("mcr.microsoft.com/azure-storage/azurite:latest"))
          .withExposedPorts(10001, 10002, 10000);

  String storageConnectionString =
      String.format(
          "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;TableEndpoint=http://%s:%s/devstoreaccount1;QueueEndpoint=http://%s:%s/devstoreaccount1;BlobEndpoint=http://%s:%s/devstoreaccount1",
          azurite.getContainerIpAddress(),
          azurite.getMappedPort(10002),
          azurite.getContainerIpAddress(),
          azurite.getMappedPort(10001),
          azurite.getContainerIpAddress(),
          azurite.getMappedPort(10000));

  String tableName = "testTable";

  CloudTable table = null;

  @BeforeEach
  void setUp() throws StorageException {
    try {
      CloudStorageAccount cloudStorageAccount = CloudStorageAccount.parse(storageConnectionString);
      CloudTableClient cloudTableClient = cloudStorageAccount.createCloudTableClient();
      TableRequestOptions tableRequestOptions = new TableRequestOptions();
      tableRequestOptions.setRetryPolicyFactory(RetryNoRetry.getInstance());
      cloudTableClient.setDefaultRequestOptions(tableRequestOptions);
      table = cloudTableClient.getTableReference(tableName);
      table.createIfNotExists();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void getCreditorInstitutionIcaFile() throws Exception {
    CreditorInstitutionIcaFile insertedEntity = (CreditorInstitutionIcaFile) table.execute(
        TableOperation.insert(new CreditorInstitutionIcaFile("12345", LocalDateTime.now().plusDays(1).toString()))).getResult();
    AzureStorageInteraction az = new AzureStorageInteraction(storageConnectionString, tableName);
    Map<String, String> result = az.getUpdatedEC(LocalDateTime.now().toString());
    assertEquals(insertedEntity.getPublicationDate(), result.get("12345"));
  }

  @Test
  void getCreditorInstitutionIcaFile_WrongConnString() throws Exception {
    String wrongStorageConnectionString = String.format(
        "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;TableEndpoint=http://%s:%s/devstoreaccount1;QueueEndpoint=http://%s:%s/devstoreaccount1;BlobEndpoint=http://%s:%s/devstoreaccount1",
        azurite.getContainerIpAddress(),
        azurite.getMappedPort(10002),
        azurite.getContainerIpAddress(),
        azurite.getMappedPort(10001),
        azurite.getContainerIpAddress(),
        azurite.getMappedPort(10000));
    AzureStorageInteraction az = new AzureStorageInteraction(wrongStorageConnectionString, tableName);
    try {
      az.getUpdatedEC(LocalDateTime.now().toString());
    } catch (AppException e) {
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getHttpStatus());
    } catch (Exception e) {
      fail();
    }
  }


}
