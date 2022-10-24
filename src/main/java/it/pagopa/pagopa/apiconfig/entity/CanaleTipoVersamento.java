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
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "CANALE_TIPO_VERSAMENTO", schema = "NODO4_CFG")
@Builder
public class CanaleTipoVersamento implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "FK_CANALE", nullable = false, insertable = false, updatable = false)
    private Long fkCanale;

    @Column(name = "FK_TIPO_VERSAMENTO", nullable = false, insertable = false, updatable = false)
    private Long fkTipoVersamento;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_CANALE", nullable = false)
    private Canali canale;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_TIPO_VERSAMENTO", nullable = false)
    private TipiVersamento tipoVersamento;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fkCanaleTipoVersamento", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<PspCanaleTipoVersamento> pspCanaleTipoVersamentoList;

}
