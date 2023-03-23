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

@Table(name = "INFORMATIVE_CONTO_ACCREDITO_DETAIL")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Builder
public class InformativeContoAccreditoDetail {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
  @SequenceGenerator(
      name = "hibernate_sequence",
      sequenceName = "hibernate_sequence",
      allocationSize = 1)
  @Column(name = "OBJ_ID", nullable = false)
  private Long id;

  @Column(name = "IBAN_ACCREDITO", length = 35)
  private String ibanAccredito;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FK_INFORMATIVA_CONTO_ACCREDITO_MASTER")
  @ToString.Exclude
  private InformativeContoAccreditoMaster fkInformativaContoAccreditoMaster;

  @Column(name = "ID_MERCHANT", length = 15)
  private String idMerchant;

  @Column(name = "CHIAVE_AVVIO")
  private String chiaveAvvio;

  @Column(name = "CHIAVE_ESITO")
  private String chiaveEsito;

  @Column(name = "ID_BANCA_SELLER", length = 50)
  private String idBancaSeller;
}
