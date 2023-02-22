package it.pagopa.pagopa.apiconfig.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "CODIFICHE_PA", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class CodifichePa {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
  @SequenceGenerator(
      name = "hibernate_sequence",
      sequenceName = "hibernate_sequence",
      allocationSize = 1)
  @Column(name = "OBJ_ID", nullable = false)
  private Long id;

  @Column(name = "CODICE_PA", nullable = false, length = 35)
  private String codicePa;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "FK_CODIFICA", nullable = false)
  @ToString.Exclude
  private Codifiche fkCodifica;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "FK_PA", nullable = false)
  @ToString.Exclude
  private Pa fkPa;
}
