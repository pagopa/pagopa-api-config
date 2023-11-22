package it.gov.pagopa.apiconfig.core.model.massiveloading;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.gov.pagopa.apiconfig.core.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Iban {
	
	  @JsonProperty("iban")
	  @Size(max = 35)
	  private String ibanValue;
	  
	  private String description;

	  @JsonProperty("validity_date")
	  @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
	  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	  @NotNull
	  private String validityDate;

	  @JsonProperty("due_date")
	  @JsonFormat(pattern = Constants.DateTimeFormat.DATE_TIME_FORMAT)
	  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	  @NotNull
	  private String dueDate;

}
