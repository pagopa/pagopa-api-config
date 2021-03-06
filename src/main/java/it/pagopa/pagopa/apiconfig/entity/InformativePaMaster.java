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
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

@Table(name = "INFORMATIVE_PA_MASTER", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InformativePaMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "ID_INFORMATIVA_PA", nullable = false, length = 35)
    private String idInformativaPa;

    @Column(name = "DATA_INIZIO_VALIDITA")
    private Timestamp dataInizioValidita;

    @Column(name = "DATA_PUBBLICAZIONE")
    private Timestamp dataPubblicazione;

    @ManyToOne(optional = false)
    @JoinColumn(name = "FK_PA", nullable = false)
    @ToString.Exclude
    private Pa fkPa;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_BINARY_FILE")
    @ToString.Exclude
    private BinaryFile fkBinaryFile;

    @Column(name = "VERSIONE", length = 35)
    private String versione;

    @Column(name = "PAGAMENTI_PRESSO_PSP")
    private Boolean pagamentiPressoPsp;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fkInformativaPaMaster", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<InformativePaDetail> details;
}
