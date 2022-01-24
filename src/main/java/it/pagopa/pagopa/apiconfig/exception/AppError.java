package it.pagopa.pagopa.apiconfig.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum AppError {
    CREDITOR_INSTITUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Creditor Institution not found", "No Creditor Institution found with code: %s"),
    CREDITOR_INSTITUTION_CONFLICT(HttpStatus.CONFLICT, "Creditor Institution conflict", "Creditor Institution code %s already exists"),

    RELATION_STATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Relation Station-CI not found", "No Relation Station-CI found between Creditor Institution with code %s and Station with code %s"),
    RELATION_STATION_CONFLICT(HttpStatus.CONFLICT, "Relation Station-CI conflict", "Relation Station-CI between Creditor Institution with code %s and Station with code %s already exists"),

    ENCODING_CREDITOR_INSTITUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Creditor Institution Encoding not found", "No Encoding found with code %s associated with Creditor Institution code %s"),
    ENCODING_CREDITOR_INSTITUTION_CONFLICT(HttpStatus.CONFLICT, "Creditor Institution Encoding conflict", "Encoding code %s associated with Creditor Institution code %s already exists"),

    BROKER_NOT_FOUND(HttpStatus.NOT_FOUND, "Broker not found", "No Broker found with code: %s"),
    BROKER_CONFLICT(HttpStatus.CONFLICT, "Broker conflict", "Broker code %s already exists"),

    STATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Station not found", "No Station found with code: %s"),
    STATION_CONFLICT(HttpStatus.CONFLICT, "Station conflict", "Station code %s already exists"),

    ICA_NOT_FOUND(HttpStatus.NOT_FOUND, "ICA not found", "No ICA found with id: %s"),

    COUNTERPART_NOT_FOUND(HttpStatus.NOT_FOUND, "Counterpart not found", "No Counterpart found with id %s associated with Creditor Institution code %s"),

    PSP_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment Service Provider not found", "No Payment Service Provider found with code: %s"),
    PSP_CONFLICT(HttpStatus.CONFLICT, "Payment Service Provider conflict", "Payment Service Provider code %s already exists"),

    BROKER_PSP_NOT_FOUND(HttpStatus.NOT_FOUND, "Broker PSP not found", "No Broker PSP found with code: %s"),
    BROKER_PSP_CONFLICT(HttpStatus.CONFLICT, "Broker PSP conflict", "Broker PSP code %s already exists"),

    SERV_PLUGIN_NOT_FOUND(HttpStatus.NOT_FOUND, "Plugin not found", "No Plugin found with code: %s"),

    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Channel not found", "No Channel found with code: %s"),
    CHANNEL_CONFLICT(HttpStatus.CONFLICT, "Channel conflict", "Channel code %s already exists"),

    RELATION_CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Relation PSP-Channel not found", "No Relation PSP-Channel found between PSP with code %s and Channel with code %s and with payment type %s"),
    RELATION_CHANNEL_CONFLICT(HttpStatus.CONFLICT, "Relation PSP-Channel conflict", "Relation PSP-Channel between PSP with code %s and Channel with code %s and with payment type %s already exists"),

    PAYMENT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "PaymentType not found", "No PaymentType found with code: %s"),
    CHANNEL_PAYMENT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "Relation Channel-PaymentType not found", "Channel %s has not PaymentType %s"),

    CDI_NOT_FOUND(HttpStatus.NOT_FOUND, "CDI not found", "No CDI found with id: %s"),

    CONFIGURATION_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "Configuration key not found", "No Configuration key found with category: %s key: %s"),
    CONFIGURATION_KEY_CONFLICT(HttpStatus.CONFLICT, "Configuration key conflict", "Configuration key with category %s and key %s already exists"),

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


