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
import javax.persistence.Table;

@Table(name = "INTERMEDIARI_PSP", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class IntermediariPsp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "ID_INTERMEDIARIO_PSP", nullable = false, length = 35)
    private String idIntermediarioPsp;

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @Column(name = "CODICE_INTERMEDIARIO")
    private String codiceIntermediario;

    @Column(name = "INTERMEDIARIO_AVV", nullable = false)
    private Boolean intermediarioAvv;

    @Column(name = "INTERMEDIARIO_NODO", nullable = false)
    private Boolean intermediarioNodo;

    @Column(name = "FAULT_BEAN_ESTESO", nullable = false)
    private Boolean faultBeanEsteso;

}
