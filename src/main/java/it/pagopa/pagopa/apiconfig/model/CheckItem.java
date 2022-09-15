package it.pagopa.pagopa.apiconfig.model;


import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CheckItem {

    String value;
    Validity valid;
    String note;
    String action;

    @Getter
    @AllArgsConstructor
    public enum Validity {
        VALID("valid"),
        NOT_VALID("not valid");

        private final String value;
    }
}
