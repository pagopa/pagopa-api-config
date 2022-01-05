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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

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
    private Pa fkPa;

    @ManyToOne
    @JoinColumn(name = "FK_BINARY_FILE")
    private BinaryFile fkBinaryFile;

    @Column(name = "VERSIONE", length = 35)
    private String versione;

    @Column(name = "PAGAMENTI_PRESSO_PSP")
    private Boolean pagamentiPressoPsp;


}
