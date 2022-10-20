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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "ID_PDD", nullable = false, length = 35)
    private String idPdd;

    @Type(type = "yes_no")
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @Column(name = "descrizione", nullable = false, length = 35)
    private String descrizione;

    @Column(name = "IP", nullable = false, length = 15)
    private String ip;

    @Column(name = "porta")
    private Integer porta;

}
