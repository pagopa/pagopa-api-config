package it.gov.pagopa.apiconfig.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppError {
  INTERNAL_SERVER_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Something was wrong"),

  CREDITOR_INSTITUTION_NOT_FOUND(
      HttpStatus.NOT_FOUND,
      "Creditor Institution not found",
      "No Creditor Institution found with code: %s"),

  CREDITOR_INSTITUTIONS_NOT_FOUND(
      HttpStatus.NOT_FOUND,
      "Creditor Institution not found",
      "No Creditor Institution found inside list"),

  CREDITOR_INSTITUTION_CONFLICT(
      HttpStatus.CONFLICT,
      "Creditor Institution conflict",
      "Creditor Institution code %s already exists"),

  RELATION_STATION_BAD_REQUEST(
      HttpStatus.BAD_REQUEST,
      "Relation Station-CI bad request",
      "Relation Station-CI between Creditor Institution with code %s and Station with code %s is"
          + " not valid. %s"),
  RELATION_STATION_NOT_FOUND(
      HttpStatus.NOT_FOUND,
      "Relation Station-CI not found",
      "No Relation Station-CI found between Creditor Institution with code %s and Station with code"
          + " %s"),
  RELATION_STATION_CONFLICT(
      HttpStatus.CONFLICT,
      "Relation Station-CI conflict",
      "Relation Station-CI between Creditor Institution with code %s and Station with code %s"
          + " already exists"),

  ENCODING_CREDITOR_INSTITUTION_BAD_REQUEST(
      HttpStatus.BAD_REQUEST,
      "Creditor Institution Encoding bad request",
      "Encoding %s=%s is not compliant to requirements."),
  ENCODING_CREDITOR_INSTITUTION_DEPRECATED(
      HttpStatus.BAD_REQUEST,
      "Creditor Institution Encoding bad request",
      "Encoding %s=%s is deprecated."),
  ENCODING_CREDITOR_INSTITUTION_NOT_FOUND(
      HttpStatus.NOT_FOUND,
      "Creditor Institution Encoding not found",
      "No Encoding found with code %s associated with Creditor Institution code %s"),
  ENCODING_NOT_FOUND(
      HttpStatus.NOT_FOUND,
      "Creditor Institution Encoding not found",
      "No Encoding found with code %s"),
  ENCODING_CREDITOR_INSTITUTION_CONFLICT(
      HttpStatus.CONFLICT,
      "Creditor Institution Encoding conflict",
      "Encoding code %s associated with Creditor Institution code %s already exists"),

  BROKER_NOT_FOUND(HttpStatus.NOT_FOUND, "Broker not found", "No Broker found with code: %s"),
  BROKER_CONFLICT(HttpStatus.CONFLICT, "Broker conflict", "Broker code %s already exists"),

  STATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Station not found", "No Station found with code: %s"),
  STATION_CONFLICT(HttpStatus.CONFLICT, "Station conflict", "Station code %s already exists"),

  ICA_NOT_FOUND(HttpStatus.NOT_FOUND, "ICA not found", "No ICA found with id: %s"),
  ICA_CONFLICT(HttpStatus.CONFLICT, "ICA conflict", "ICA with code %s already exists"),
  ICA_BAD_REQUEST(HttpStatus.BAD_REQUEST, "ICA bad request", "XML ICA file is not valid: %s"),

  COUNTERPART_NOT_FOUND(
      HttpStatus.NOT_FOUND,
      "Counterpart not found",
      "No Counterpart found with id %s associated with Creditor Institution code %s"),
  COUNTERPART_CONFLICT(
      HttpStatus.CONFLICT, "Counterpart conflict", "Counterpart with code %s already exists"),
  COUNTERPART_BAD_REQUEST(
      HttpStatus.BAD_REQUEST, "Counterpart bad request", "XML Counterpart file is not valid: %s"),

  PSP_NOT_FOUND(
      HttpStatus.NOT_FOUND,
      "Payment Service Provider not found",
      "No Payment Service Provider found with code: %s"),
  PSP_CONFLICT(
      HttpStatus.CONFLICT,
      "Payment Service Provider conflict",
      "Payment Service Provider code %s already exists"),

  BROKER_PSP_NOT_FOUND(
      HttpStatus.NOT_FOUND, "Broker PSP not found", "No Broker PSP found with code: %s"),
  BROKER_PSP_CONFLICT(
      HttpStatus.CONFLICT, "Broker PSP conflict", "Broker PSP code %s already exists"),

  SERV_PLUGIN_NOT_FOUND(HttpStatus.NOT_FOUND, "Plugin not found", "No Plugin found with code: %s"),

  CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Channel not found", "No Channel found with code: %s"),
  CHANNEL_CONFLICT(HttpStatus.CONFLICT, "Channel conflict", "Channel code %s already exists"),

  RELATION_CHANNEL_BAD_REQUEST(
      HttpStatus.BAD_REQUEST,
      "Relation PSP-Channel bad request",
      "Relation PSP-Channel between PSP with code %s and Channel with code %s has no payment types"
          + " specified"),
  RELATION_CHANNEL_NOT_FOUND(
      HttpStatus.NOT_FOUND,
      "Relation PSP-Channel not found",
      "No Relation PSP-Channel found between PSP with code %s and Channel with code %s and with"
          + " payment type %s"),
  RELATION_CHANNEL_CONFLICT(
      HttpStatus.CONFLICT,
      "Relation PSP-Channel conflict",
      "Relation PSP-Channel between PSP with code %s and Channel with code %s and with payment type"
          + " %s already exists"),

  PAYMENT_TYPE_BAD_REQUEST(
      HttpStatus.BAD_REQUEST,
      "Payment type bad request",
      "No payment type specified in the request"),
  PAYMENT_TYPE_NON_DELETABLE(
      HttpStatus.BAD_REQUEST,
      "Payment type not deletable",
      "Payment type is used in AFM Marketplace bundles"),
  PAYMENT_TYPE_NOT_FOUND(
      HttpStatus.NOT_FOUND, "Payment type not found", "No payment type found with code: %s"),
  PAYMENT_TYPE_CONFLICT(
      HttpStatus.CONFLICT, "Payment type conflict", "Payment type with code %s already exists"),
  PAYMENT_TYPE_NO_AFM_MARKETPLACE(
      HttpStatus.BAD_REQUEST,
      "AFM Marketplace not reachable",
      "AFM Marketplace is not reachable to check if payment type is already used"),
  PAYMENT_TYPE_AFM_MARKETPLACE_ERROR(
      HttpStatus.BAD_REQUEST,
      "Payment type bad request",
      "Problem with status check on AFM Marketplace: %s"),

  CHANNEL_PAYMENT_TYPE_FOUND(
      HttpStatus.BAD_REQUEST,
      "Relation Channel-PaymentType found",
      "Channel %s has at least one PaymentType"),
  CHANNEL_PAYMENT_TYPE_NOT_FOUND(
      HttpStatus.NOT_FOUND,
      "Relation Channel-PaymentType not found",
      "Channel %s has not PaymentType %s"),
  CHANNEL_PAYMENT_TYPE_CONFLICT(
      HttpStatus.CONFLICT,
      "Relation Channel-PaymentType conflict",
      "Relation Channel-PaymentType between Channel with code %s and PaymentType with code %s"
          + " already exists"),

  CDI_NOT_FOUND(HttpStatus.NOT_FOUND, "CDI not found", "No CDI found with id: %s"),
  CDI_CONFLICT(HttpStatus.CONFLICT, "CDI conflict", "CDI with code %s already exists"),
  CDI_BAD_REQUEST(HttpStatus.BAD_REQUEST, "CDI bad request", "XML CDI file is not valid: %s"),
  CDI_DETAILS_NOT_FOUND(
      HttpStatus.NOT_FOUND, "CDI details not found", "No CDI Details found refers to Master %s"),

  CDI_SYNC_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, "Problem to sync CDIs", "Problem triggering AFM Utils"),

  CONFIGURATION_KEY_NOT_FOUND(
      HttpStatus.NOT_FOUND,
      "Configuration key not found",
      "No Configuration key found with category: %s key: %s"),
  CONFIGURATION_KEY_CONFLICT(
      HttpStatus.CONFLICT,
      "Configuration key conflict",
      "Configuration key with category %s and key %s already exists"),

  CONFIGURATION_WFESP_PLUGIN_NOT_FOUND(
      HttpStatus.NOT_FOUND,
      "Configuration wfesp plugin not found",
      "No Configuration wfesp plugin found with idServPlugin: %s"),
  CONFIGURATION_WFESP_PLUGIN_CONFLICT(
      HttpStatus.CONFLICT,
      "Configuration wfesp plugin conflict",
      "Configuration key with idServPlugin %s already exists"),

  PDD_NOT_FOUND(HttpStatus.NOT_FOUND, "Pdd not found", "No pdd found with id_pdd %s"),
  PDD_CONFLICT(HttpStatus.CONFLICT, "Pdd conflict", "Pdd with id_pdd %s already exists"),

  FTP_SERVER_NOT_FOUND(
      HttpStatus.NOT_FOUND,
      "Ftp server not found",
      "No ftp server found with host: %s port: %s service: %s"),
  FTP_SERVER_CONFLICT(
      HttpStatus.CONFLICT,
      "Ftp server conflict",
      "Ftp server with host %s, port %s and service %s already exists"),

  MASSIVELOADING_BAD_REQUEST(HttpStatus.BAD_REQUEST, "CSV file is not valid", "%s"),

  CACHE_NOT_FOUND(HttpStatus.NOT_FOUND, "Cache not found", "No version found with code: %s"),

  REFRESH_CONFIG_EXCEPTION(
      HttpStatus.INTERNAL_SERVER_ERROR, "Node Monitoring Error", "Refresh configuration failure"),
  REFRESH_CONFIG_TIMEOUT(
      HttpStatus.GATEWAY_TIMEOUT, "Node Monitoring Timeout", "Refresh configuration timeout"),

  IBAN_ALREADY_ASSOCIATED(
      HttpStatus.CONFLICT,
      "IBAN already associated",
      "The IBAN with code %s was already associated to the creditor institution %s"),

  IBAN_LABEL_NOT_VALID(
      HttpStatus.UNPROCESSABLE_ENTITY,
      "IBAN label not valid",
      "The label %s is not valid and cannot be associated to IBAN"),
  IBAN_NOT_FOUND(
      HttpStatus.NOT_FOUND, "IBAN value not found", "The IBAN with value %s is not present"),

  IBAN_NOT_ASSOCIATED(
      HttpStatus.NOT_FOUND,
      "IBAN not associated",
      "The IBAN with code %s is not associated to the creditor institution %s"),

  POSTAL_IBAN_ALREADY_ASSOCIATED(
      HttpStatus.CONFLICT,
      "Postal IBAN already associated with one CI",
      "The postal IBAN with code %s was already associated to one CI, this type of IBAN cannot be"
          + " associated to an additional creditor institution %s"),

  AZURE_STORAGE_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "Error with the azure table storage",
      "Error when interacting with the azure table storage"),

  ICA_XML_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR,
      "Error when writing the ica binary file",
      "Error when writing the ica binary xml file"),

  CHARITY_ERROR(
      HttpStatus.UNPROCESSABLE_ENTITY,
      "CHARITY prefix found in the PSP ID field",
      "Requests prefixed with CHARITY in the PSP ID field are rejected [IdentificativoPSP=%s]"),

  UNKNOWN(null, null, null);

  public final HttpStatus httpStatus;
  public final String title;
  public final String details;

  AppError(HttpStatus httpStatus, String title, String details) {
    this.httpStatus = httpStatus;
    this.title = title;
    this.details = details;
  }
}
