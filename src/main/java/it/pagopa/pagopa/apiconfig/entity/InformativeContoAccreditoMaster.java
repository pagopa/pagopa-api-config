package it.pagopa.pagopa.apiconfig.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Table(name = "INFORMATIVE_CONTO_ACCREDITO_MASTER")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class InformativeContoAccreditoMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "DATA_INIZIO_VALIDITA")
    private Timestamp dataInizioValidita;

    @Column(name = "DATA_PUBBLICAZIONE")
    private Timestamp dataPubblicazione;

    @Column(name = "ID_INFORMATIVA_CONTO_ACCREDITO_PA", nullable = false, length = 35)
    private String idInformativaContoAccreditoPa;

    @Column(name = "RAGIONE_SOCIALE", length = 70)
    private String ragioneSociale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_PA")
    @ToString.Exclude
    private Pa fkPa;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_BINARY_FILE")
    @ToString.Exclude
    private BinaryFile fkBinaryFile;

    @Column(name = "VERSIONE", length = 35)
    private String versione;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fkInformativaContoAccreditoMaster", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<InformativeContoAccreditoDetail> icaDetail;

}
