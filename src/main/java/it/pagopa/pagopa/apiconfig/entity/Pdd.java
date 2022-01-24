package it.pagopa.pagopa.apiconfig.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
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
