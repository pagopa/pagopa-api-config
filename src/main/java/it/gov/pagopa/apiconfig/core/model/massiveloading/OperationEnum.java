package it.gov.pagopa.apiconfig.core.model.massiveloading;

import java.util.Set;

public enum OperationEnum {
    I, D, C, U, M;

    public static final Set<OperationEnum> UPDATE_OP = Set.of(U, M);
    public static final Set<OperationEnum> DELETE_OP = Set.of(D, C);
}
