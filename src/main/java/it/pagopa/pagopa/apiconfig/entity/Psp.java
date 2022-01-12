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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "PSP", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(onlyExplicitlyIncluded = true)
public class Psp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "ID_PSP", nullable = false, length = 35)
    private String idPsp;

    @Type(type = "yes_no")
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
    private Boolean stornoPagamento;

    @Type(type = "yes_no")
    @Column(name = "FLAG_REPO_COMMISSIONE_CARICO_PA")
    private Boolean flagRepoCommissioneCaricoPa;

    @Column(name = "EMAIL_REPO_COMMISSIONE_CARICO_PA")
    private String emailRepoCommissioneCaricoPa;

    @Column(name = "CODICE_MYBANK", length = 35)
    private String codiceMybank;

    @Column(name = "MARCA_BOLLO_DIGITALE", nullable = false)
    private Boolean marcaBolloDigitale;

    @Type(type = "yes_no")
    @Column(name = "AGID_PSP", nullable = false)
    private Boolean agidPsp;

    @Type(type = "yes_no")
    @Column(name = "PSP_NODO", nullable = false)
    private Boolean pspNodo;

    @Type(type = "yes_no")
    @Column(name = "PSP_AVV", nullable = false)
    private Boolean pspAvv;

    @Column(name = "CODICE_FISCALE", length = 16)
    private String codiceFiscale;

    @Column(name = "VAT_NUMBER", length = 20)
    private String vatNumber;

}
