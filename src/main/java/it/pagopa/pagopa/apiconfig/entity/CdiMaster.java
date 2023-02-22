package it.pagopa.pagopa.apiconfig.entity;

import java.sql.Timestamp;
import java.util.List;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "CDI_MASTER", schema = "NODO4_CFG")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class CdiMaster {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
  @SequenceGenerator(
      name = "hibernate_sequence",
      sequenceName = "hibernate_sequence",
      allocationSize = 1)
  @Column(name = "OBJ_ID", nullable = false)
  private Long id;

  @Column(name = "ID_INFORMATIVA_PSP", nullable = false, length = 35)
  private String idInformativaPsp;

  @Column(name = "DATA_INIZIO_VALIDITA")
  private Timestamp dataInizioValidita;

  @Column(name = "DATA_PUBBLICAZIONE")
  private Timestamp dataPubblicazione;

  @Column(name = "LOGO_PSP")
  @ToString.Exclude
  private byte[] logoPsp;

  @Column(name = "URL_INFORMAZIONI_PSP")
  private String urlInformazioniPsp;

  @Column(name = "MARCA_BOLLO_DIGITALE", nullable = false)
  private Boolean marcaBolloDigitale;

  @Column(name = "STORNO_PAGAMENTO", nullable = false)
  private Boolean stornoPagamento;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FK_PSP", nullable = false)
  private Psp fkPsp;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "FK_BINARY_FILE", nullable = false)
  private BinaryFile fkBinaryFile;

  @Column(name = "VERSIONE", length = 35)
  private String versione;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "fkCdiMaster", cascade = CascadeType.REMOVE)
  @ToString.Exclude
  private List<CdiDetail> cdiDetail;
}
