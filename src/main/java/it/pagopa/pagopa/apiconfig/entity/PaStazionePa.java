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

@Table(name = "PA_STAZIONE_PA", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaStazionePa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "PROGRESSIVO")
    private Long progressivo;

    @Column(name = "FK_PA", nullable = false)
    private Long fkPa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_STAZIONE", nullable = false)
    private Stazioni fkStazione;

    @Column(name = "AUX_DIGIT")
    private Long auxDigit;

    @Column(name = "SEGREGAZIONE")
    private Long segregazione;

    @Column(name = "QUARTO_MODELLO", nullable = false)
    private Boolean quartoModello = false;

    /**
     * @deprecated not used
     */
    @Deprecated(forRemoval = true)
    @Column(name = "STAZIONE_NODO", nullable = false)
    private Boolean stazioneNodo = false;

    @Column(name = "STAZIONE_AVV", nullable = false)
    private Boolean stazioneAvv = false;

    @Column(name = "BROADCAST", nullable = false)
    private Boolean broadcast = false;

}
