package it.pagopa.pagopa.apiconfig.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum AppError {
    CREDITOR_INSTITUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Creditor Institution not found", "No Creditor Institution found with code: %s"),
    CREDITOR_INSTITUTION_CONFLICT(HttpStatus.CONFLICT, "Creditor Institution conflict", "Creditor Institution code %s already exists"),

    ENCODING_CREDITOR_INSTITUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Creditor Institution Encoding not found", "No Encoding found with code %s associated with Creditor Institution code %s"),
    ENCODING_CREDITOR_INSTITUTION_CONFLICT(HttpStatus.CONFLICT, "Creditor Institution Encoding conflict", "Encoding code %s associated with Creditor Institution code %s already exists"),

    BROKER_NOT_FOUND(HttpStatus.NOT_FOUND, "Broker not found", "No Broker found with code: %s"),
    BROKER_CONFLICT(HttpStatus.CONFLICT, "Broker conflict", "Broker code %s already exists"),

    STATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Station not found", "No Station found with code: %s"),
    STATION_CONFLICT(HttpStatus.CONFLICT, "Station conflict", "Station code %s already exists"),

    ICA_NOT_FOUND(HttpStatus.NOT_FOUND, "ICA not found", "No ICA found with id: %s"),

    COUNTERPART_NOT_FOUND(HttpStatus.NOT_FOUND, "Counterpart not found", "No Counterpart found with id %s associated with Creditor Institution code %s"),

    PSP_NOT_FOUND(HttpStatus.NOT_FOUND, "Payment Service Provider not found", "No Payment Service Provider found with code: %s"),

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


