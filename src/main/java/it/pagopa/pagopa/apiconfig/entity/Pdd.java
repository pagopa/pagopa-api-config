package it.pagopa.pagopa.apiconfig.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import it.pagopa.pagopa.apiconfig.util.YesNoConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "PDD", schema = "NODO4_CFG")
@Builder(toBuilder = true)
public class Pdd implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")

    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "ID_PDD", nullable = false, length = 35)
    private String idPdd;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @Column(name = "descrizione", nullable = false, length = 35)
    private String descrizione;

    @Column(name = "IP", nullable = false, length = 15)
    private String ip;

    @Column(name = "porta")
    private Integer porta;

}
