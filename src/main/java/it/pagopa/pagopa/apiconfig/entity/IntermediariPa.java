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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "INTERMEDIARI_PA", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IntermediariPa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "ID_INTERMEDIARIO_PA", nullable = false, length = 35)
    private String idIntermediarioPa;

    @Type(type = "yes_no")
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = false;

    @Column(name = "CODICE_INTERMEDIARIO")
    private String codiceIntermediario;

    @Type(type = "yes_no")
    @Column(name = "FAULT_BEAN_ESTESO", nullable = false)
    private Boolean faultBeanEsteso = false;

}
