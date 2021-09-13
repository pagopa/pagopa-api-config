package it.pagopa.pagopa.apiconfig.models.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PA")
@Getter
@Setter
@ToString
public class Pa {
    @Id
    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "ID_DOMINIO")
    private String idDominio;

    @Column(name = "ENABLED", columnDefinition = "CHAR")
    private String enabled;

    @Column(name = "DESCRIZIONE")
    private String descrizione;

    @Column(name = "RAGIONE_SOCIALE")
    private String ragioneSociale;

    @Column(name = "FK_INT_QUADRATURE")
    private Long fkIntQuadrature;

    @Column(name = "FLAG_REPO_COMMISSIONE_CARICO_PA", columnDefinition = "CHAR")
    private String flagRepoCommissioneCaricoPa;

    @Column(name = "EMAIL_REPO_COMMISSIONE_CARICO_PA")
    private String emailRepoCommissioneCaricoPa;

    @Column(name = "INDIRIZZO_DOMICILIO_FISCALE")
    private String indirizzoDomicilioFiscale;

    @Column(name = "CAP_DOMICILIO_FISCALE")
    private Long capDomicilioFiscale;

    @Column(name = "SIGLA_PROVINCIA_DOMICILIO_FISCALE")
    private String siglaProvinciaDomicilioFiscale;

    @Column(name = "COMUNE_DOMICILIO_FISCALE")
    private String comuneDomicilioFiscale;

    @Column(name = "DENOMINAZIONE_DOMICILIO_FISCALE")
    private String denominazioneDomicilioFiscale;

    @Column(name = "PAGAMENTO_PRESSO_PSP", columnDefinition = "CHAR")
    private String pagamentoPressoPsp;

    @Column(name = "RENDICONTAZIONE_FTP", columnDefinition = "CHAR")
    private String rendicontazioneFtp;

    @Column(name = "RENDICONTAZIONE_ZIP", columnDefinition = "CHAR")
    private String rendicontazioneZip;

    @Column(name = "REVOCA_PAGAMENTO")
    private Long revocaPagamento;

}
