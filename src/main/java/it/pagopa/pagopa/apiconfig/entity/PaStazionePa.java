package it.pagopa.pagopa.apiconfig.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "PA_STAZIONE_PA", schema = "NODO4_CFG")
@Setter
@Getter
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

    @JoinColumn(name = "FK_PA")
    private Long fkPa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_STAZIONE", referencedColumnName = "OBJ_ID")
    private Stazioni fkStazioni;

    @Column(name = "AUX_DIGIT")
    private Long auxDigit;

    @Column(name = "SEGREGAZIONE")
    private Long segregazione;

    @Column(name = "QUARTO_MODELLO", columnDefinition = "CHAR")
    private String quartoModello;

    @Column(name = "STAZIONE_NODO", columnDefinition = "CHAR")
    private String stazioneNodo;

    @Column(name = "STAZIONE_AVV", columnDefinition = "CHAR")
    private String stazioneAvv;

    @Column(name = "BROADCAST", columnDefinition = "CHAR")
    private String broadcast;

}