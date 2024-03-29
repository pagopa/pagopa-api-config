package it.gov.pagopa.apiconfig.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobTrigger {
  PA_INVIA_RT("paInviaRt"),
  PA_RETRY_PA_INVIA_RT_NEGATIVE("paRetryPaInviaRtNegative"),
  PA_INVIA_RT_RECOVERY("paInviaRtRecovery"),
  PA_SEND_RT("paSendRt"),
  REFRESH_CONFIGURATION("refreshConfiguration");

  private final String value;
}
