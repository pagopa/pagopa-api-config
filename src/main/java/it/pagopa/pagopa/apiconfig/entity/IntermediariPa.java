package it.pagopa.pagopa.apiconfig.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Table(name = "INTERMEDIARI_PA", indexes = {
        @Index(name = "UQ_ID_INTERMEDIARIO_PA", columnList = "ID_INTERMEDIARIO_PA", unique = true)
})
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class IntermediariPa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "ID_INTERMEDIARIO_PA", nullable = false, length = 35)
    private String idIntermediarioPa;

    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = false;

    @Column(name = "CODICE_INTERMEDIARIO")
    private String codiceIntermediario;

    @Column(name = "FAULT_BEAN_ESTESO", nullable = false)
    private Boolean faultBeanEsteso = false;

}
