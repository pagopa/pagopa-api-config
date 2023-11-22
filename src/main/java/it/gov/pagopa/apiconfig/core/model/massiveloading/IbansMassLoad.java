package it.gov.pagopa.apiconfig.core.model.massiveloading;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IbansMassLoad {
	@JsonProperty("creditor_institution_code")
	@NotNull
	private String creditorInstitutionCode;
	private String description;
	@Size(min=1)
	private List<Iban> ibans;

}
