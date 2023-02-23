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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "CDI_INFORMAZIONI_SERVIZIO", schema = "NODO4_CFG")
public class CdiInformazioniServizio {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
  @SequenceGenerator(
      name = "hibernate_sequence",
      sequenceName = "hibernate_sequence",
      allocationSize = 1)
  @Column(name = "OBJ_ID", nullable = false)
  private Long id;

  @Column(name = "CODICE_LINGUA", nullable = false, length = 2)
  private String codiceLingua;

  @Column(name = "DESCRIZIONE_SERVIZIO", nullable = false, length = 140)
  private String descrizioneServizio;

  @Column(name = "DISPONIBILITA_SERVIZIO", nullable = false, length = 140)
  private String disponibilitaServizio;

  @Column(name = "URL_INFORMAZIONI_CANALE")
  private String urlInformazioniCanale;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "FK_CDI_DETAIL", nullable = false)
  @ToString.Exclude
  private CdiDetail fkCdiDetail;

  @Column(name = "LIMITAZIONI_SERVIZIO", length = 140)
  private String limitazioniServizio;
}
