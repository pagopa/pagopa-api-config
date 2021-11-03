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
import javax.persistence.Table;

@Table(name = "INTERMEDIARI_PSP", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class IntermediariPsp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;
}
