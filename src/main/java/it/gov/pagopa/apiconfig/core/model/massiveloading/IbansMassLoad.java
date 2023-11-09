package it.gov.pagopa.apiconfig.core.model.massiveloading;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IbansMassLoad {
	@JsonProperty("creditor_institution_code")
	private String creditorInstitutionCode;
	private String description;
	private List<Iban> ibans;

}
