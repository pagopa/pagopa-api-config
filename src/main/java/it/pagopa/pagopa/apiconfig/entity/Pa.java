package it.pagopa.pagopa.apiconfig.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "ID_DOMINIO", unique = true)
    private String idDominio;

    @Type(type="yes_no")
    @Column(name = "ENABLED")
    private Boolean enabled;

    @Column(name = "RAGIONE_SOCIALE")
    private String ragioneSociale;

    @Column(name = "FK_INT_QUADRATURE")
    private Long fkIntQuadrature;

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

    @Type(type="yes_no")
    @Column(name = "PAGAMENTO_PRESSO_PSP")
    private Boolean pagamentoPressoPsp;

    @Type(type="yes_no")
    @Column(name = "RENDICONTAZIONE_FTP")
    private Boolean rendicontazioneFtp;

    @Type(type="yes_no")
    @Column(name = "RENDICONTAZIONE_ZIP")
    private Boolean rendicontazioneZip;

    // default false because it is deprecated but not_null in the DB
    @Column(name = "FLAG_REPO_COMMISSIONE_CARICO_PA")
    private Boolean flagRepoCommissioneCaricoPa = false;

}
