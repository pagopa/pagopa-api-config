package it.pagopa.pagopa.apiconfig.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "PA")
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

    @Column(name = "ENABLED", columnDefinition = "CHAR")
    private String enabled;

    @Column(name = "DESCRIZIONE")
    private String descrizione;

    @Column(name = "RAGIONE_SOCIALE")
    private String ragioneSociale;

    @JoinColumn(name = "FK_INT_QUADRATURE")
    private Long fkIntQuadrature;

    /**
     * @deprecated not used
     */
    @Deprecated(forRemoval = true)
    @Column(name = "FLAG_REPO_COMMISSIONE_CARICO_PA", columnDefinition = "CHAR")
    private String flagRepoCommissioneCaricoPa;

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

    @Column(name = "PAGAMENTO_PRESSO_PSP", columnDefinition = "CHAR")
    private String pagamentoPressoPsp;

    @Column(name = "RENDICONTAZIONE_FTP", columnDefinition = "CHAR")
    private String rendicontazioneFtp;

    @Column(name = "RENDICONTAZIONE_ZIP", columnDefinition = "CHAR")
    private String rendicontazioneZip;

    /**
     * @deprecated not used
     */
    @Deprecated(forRemoval = true)
    @Column(name = "REVOCA_PAGAMENTO")
    private Long revocaPagamento;

}
