package it.pagopa.pagopa.apiconfig.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "PA", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Pa {
    @Id
    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "ID_DOMINIO")
    private String idDominio;

    @Column(name = "ENABLED")
    private Boolean enabled;

    @Column(name = "DESCRIZIONE")
    private String descrizione;

    @Column(name = "RAGIONE_SOCIALE")
    private String ragioneSociale;

    @Column(name = "FK_INT_QUADRATURE")
    private Long fkIntQuadrature;

    /**
     * @deprecated not used
     */
    @Deprecated(forRemoval = true)
    @Column(name = "FLAG_REPO_COMMISSIONE_CARICO_PA")
    private Boolean flagRepoCommissioneCaricoPa;

    /**
     * @deprecated not used
     */
    @Deprecated(forRemoval = true)
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

    @Column(name = "PAGAMENTO_PRESSO_PSP")
    private Boolean pagamentoPressoPsp;

    @Column(name = "RENDICONTAZIONE_FTP")
    private Boolean rendicontazioneFtp;

    @Column(name = "RENDICONTAZIONE_ZIP")
    private Boolean rendicontazioneZip;

    /**
     * @deprecated not used
     */
    @Deprecated(forRemoval = true)
    @Column(name = "REVOCA_PAGAMENTO")
    private Long revocaPagamento;


}
