package it.pagopa.pagopa.apiconfig.entity;

import it.pagopa.pagopa.apiconfig.util.YesNoConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "PA", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Pa {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
  @SequenceGenerator(
      name = "hibernate_sequence",
      sequenceName = "hibernate_sequence",
      allocationSize = 1)
  @Column(name = "OBJ_ID", nullable = false)
  private Long objId;

  @Column(name = "ID_DOMINIO", unique = true)
  private String idDominio;

  @Convert(converter = YesNoConverter.class)
  @Column(name = "ENABLED")
  private Boolean enabled;

  @Column(name = "RAGIONE_SOCIALE")
  private String ragioneSociale;

  @Column(name = "FK_INT_QUADRATURE")
  private Long fkIntQuadrature;

  @Column(name = "INDIRIZZO_DOMICILIO_FISCALE")
  private String indirizzoDomicilioFiscale;

  @Column(name = "CAP_DOMICILIO_FISCALE")
  @Size(max = 5)
  private String capDomicilioFiscale;

  @Column(name = "SIGLA_PROVINCIA_DOMICILIO_FISCALE")
  private String siglaProvinciaDomicilioFiscale;

  @Column(name = "COMUNE_DOMICILIO_FISCALE")
  private String comuneDomicilioFiscale;

  @Column(name = "DENOMINAZIONE_DOMICILIO_FISCALE")
  private String denominazioneDomicilioFiscale;

  @Convert(converter = YesNoConverter.class)
  @Column(name = "PAGAMENTO_PRESSO_PSP")
  private Boolean pagamentoPressoPsp;

  @Convert(converter = YesNoConverter.class)
  @Column(name = "RENDICONTAZIONE_FTP")
  private Boolean rendicontazioneFtp;

  @Convert(converter = YesNoConverter.class)
  @Column(name = "RENDICONTAZIONE_ZIP")
  private Boolean rendicontazioneZip;

  @Convert(converter = YesNoConverter.class)
  @Column(name = "FLAG_REPO_COMMISSIONE_CARICO_PA")
  private Boolean flagRepoCommissioneCaricoPa;
}
