package it.pagopa.pagopa.apiconfig.entity;

import it.pagopa.pagopa.apiconfig.util.YesNoConverter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Table(name = "PA_STAZIONE_PA", schema = "NODO4_CFG", uniqueConstraints = { @UniqueConstraint(columnNames = { "FK_PA", "FK_STAZIONE" }) })
@Entity
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaStazionePa {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)

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

    @Convert(converter = YesNoConverter.class)
    @Column(name = "QUARTO_MODELLO", nullable = false)
    private Boolean quartoModello = false;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "STAZIONE_AVV", nullable = false)
    private Boolean stazioneAvv = false;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "STAZIONE_NODO", nullable = false)
    private Boolean stazioneNodo = true;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "BROADCAST", nullable = false)
    private Boolean broadcast = false;

}
