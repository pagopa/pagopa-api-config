package it.pagopa.pagopa.apiconfig.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum AppError {
    CREDITOR_INSTITUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Creditor Institution not found", "No Creditor Institution found with code: {}"),
    CREDITOR_INSTITUTION_CONFLICT(HttpStatus.CONFLICT, "Creditor Institution conflict", "Creditor Institution code {} already exists"),

    BROKER_NOT_FOUND(HttpStatus.NOT_FOUND, "Broker not found", "No Broker found with code: {}"),
    BROKER_CONFLICT(HttpStatus.CONFLICT, "Broker conflict", "Broker code {} already exists"),

    STATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Station not found", "No Station found with code: {}"),
    STATION_CONFLICT(HttpStatus.CONFLICT, "Station conflict", "Station code {} already exists"),

    ICA_NOT_FOUND(HttpStatus.NOT_FOUND, "ICA not found", "No ICA found with id: {}"),

    COUNTERPART_NOT_FOUND(HttpStatus.NOT_FOUND, "Counterpart not found", "No Counterpart found with id {} and Creditor Institution code {}"),

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


