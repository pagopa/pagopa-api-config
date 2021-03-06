package it.pagopa.pagopa.apiconfig.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

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
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "INFORMATIVE_PA_DETAIL", schema = "NODO4_CFG")
public class InformativePaDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "FLAG_DISPONIBILITA", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean flagDisponibilita = false;

    @Column(name = "GIORNO", length = 35)
    private String giorno;

    @Column(name = "TIPO", length = 35)
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_INFORMATIVA_PA_MASTER", nullable = false)
    @ToString.Exclude
    private InformativePaMaster fkInformativaPaMaster;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fkInformativaPaDetail", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<InformativePaFasce> fasce;

}
