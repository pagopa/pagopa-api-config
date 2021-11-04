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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name = "PSP", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(onlyExplicitlyIncluded = true)
public class Psp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "ID_PSP", nullable = false, length = 35)
    private String idPsp;

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @Column(name = "ABI", length = 5)
    private String abi;

    @Column(name = "BIC", length = 11)
    private String bic;

    @Column(name = "DESCRIZIONE", length = 70)
    private String descrizione;

    @Column(name = "RAGIONE_SOCIALE", length = 70)
    private String ragioneSociale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_INT_QUADRATURE")
    private IntermediariPsp fkIntQuadrature;

    @Column(name = "STORNO_PAGAMENTO", nullable = false)
    private Long stornoPagamento;

    @Column(name = "FLAG_REPO_COMMISSIONE_CARICO_PA")
    private Boolean flagRepoCommissioneCaricoPa;

    @Column(name = "EMAIL_REPO_COMMISSIONE_CARICO_PA")
    private String emailRepoCommissioneCaricoPa;

    @Column(name = "CODICE_MYBANK", length = 35)
    private String codiceMybank;

    @Column(name = "MARCA_BOLLO_DIGITALE", nullable = false)
    private Long marcaBolloDigitale;

    @Column(name = "AGID_PSP", nullable = false)
    private Boolean agidPsp;

    @Column(name = "PSP_NODO", nullable = false)
    private Boolean pspNodo;

    @Column(name = "PSP_AVV", nullable = false)
    private Boolean pspAvv;

    @Column(name = "CODICE_FISCALE", length = 16)
    private String codiceFiscale;

    @Column(name = "VAT_NUMBER", length = 20)
    private String vatNumber;

}
