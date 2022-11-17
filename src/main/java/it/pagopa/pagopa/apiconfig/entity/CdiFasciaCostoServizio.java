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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "CDI_FASCIA_COSTO_SERVIZIO")
public class CdiFasciaCostoServizio {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "IMPORTO_MINIMO", nullable = false)
    private Double importoMinimo;

    @Column(name = "IMPORTO_MASSIMO", nullable = false)
    private Double importoMassimo;

    @Column(name = "COSTO_FISSO", nullable = false)
    private Double costoFisso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_CDI_DETAIL", nullable = false)
    @ToString.Exclude
    private CdiDetail fkCdiDetail;

    @Column(name = "VALORE_COMMISSIONE")
    private Double valoreCommissione;

    @Column(name = "CODICE_CONVENZIONE", length = 35)
    private String codiceConvenzione;

}
