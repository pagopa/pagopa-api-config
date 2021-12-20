package it.pagopa.pagopa.apiconfig.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Formatter;

/**
 * Custom exception.
 * <p> See {@link it.pagopa.pagopa.apiconfig.exception.ErrorHandler}
 */
@EqualsAndHashCode(callSuper = true)
@Value
@Validated
public class AppException extends RuntimeException {

    /**
     * title returned to the response when this exception occurred
     */
    String title;

    /**
     * http status returned to the response when this exception occurred
     */
    HttpStatus httpStatus;

    /**
     * @param httpStatus HTTP status returned to the response
     * @param title      title returned to the response when this exception occurred
     * @param message    the detail message returend to the response
     * @param cause      The cause of this {@link AppException}
     */
    public AppException(@NotNull HttpStatus httpStatus, @NotNull String title, @NotNull String message, Throwable cause) {
        super(message, cause);
        this.title = title;
        this.httpStatus = httpStatus;
    }

    /**
     * @param httpStatus HTTP status returned to the response
     * @param title      title returned to the response when this exception occurred
     * @param message    the detail message returend to the response
     */
    public AppException(@NotNull HttpStatus httpStatus, @NotNull String title, @NotNull String message) {
        super(message);
        this.title = title;
        this.httpStatus = httpStatus;
    }


    /**
     * @param appError Response template returned to the response
     * @param args     {@link Formatter} replaces the placeholders in "details" string of {@link AppError} with the arguments.
     *                 If there are more arguments than format specifiers, the extra arguments are ignored.
     */
    public AppException(@NotNull AppError appError, Object... args) {
        super(formatDetails(appError, args));
        this.httpStatus = appError.httpStatus;
        this.title = appError.title;
    }

    /**
     * @param appError Response template returned to the response
     * @param cause    The cause of this {@link AppException}
     * @param args     Arguments for the details of {@link AppError} replaced by the {@link Formatter}.
     *                 If there are more arguments than format specifiers, the extra arguments are ignored.
     */
    public AppException(@NotNull AppError appError, Throwable cause, Object... args) {
        super(formatDetails(appError, args), cause);
        this.httpStatus = appError.httpStatus;
        this.title = appError.title;
    }

    private static String formatDetails(AppError appError, Object[] args) {
        return String.format(appError.details, args);
    }

    @Override
    public String toString() {
        return "AppException(" + httpStatus + ", " + title + ")" + super.toString();
    }
}
