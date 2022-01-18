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

@Table(name = "PA_STAZIONE_PA", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaStazionePa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "PROGRESSIVO")
    private Long progressivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_PA", nullable = false)
    @ToString.Exclude
    private Pa pa;

    @Column(name = "FK_PA", nullable = false, insertable = false, updatable = false)
    private Long fkPa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_STAZIONE", nullable = false)
    @ToString.Exclude
    private Stazioni fkStazione;

    @Column(name = "AUX_DIGIT")
    private Long auxDigit;

    @Column(name = "SEGREGAZIONE")
    private Long segregazione;

    @Type(type = "yes_no")
    @Column(name = "QUARTO_MODELLO", nullable = false)
    private Boolean quartoModello = false;

    @Type(type = "yes_no")
    @Column(name = "STAZIONE_AVV", nullable = false)
    private Boolean stazioneAvv = false;

    @Type(type = "yes_no")
    @Column(name = "STAZIONE_NODO", nullable = false)
    private Boolean stazioneNodo = true;

    @Type(type = "yes_no")
    @Column(name = "BROADCAST", nullable = false)
    private Boolean broadcast = false;

}
