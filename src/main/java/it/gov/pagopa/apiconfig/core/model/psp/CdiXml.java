package it.gov.pagopa.apiconfig.core.model.psp;

import it.gov.pagopa.apiconfig.core.util.XMLDateAdapter;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.*;

@XmlRootElement(name = "informativaPSP")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class CdiXml {

  private String identificativoFlusso;
  private String identificativoPSP;
  private String ragioneSociale;
  private String codiceABI;
  private String codiceBIC;
  private InformativaMaster informativaMaster;
  private ListaInformativaDetail listaInformativaDetail;

  private String mybankIDVS;

  @XmlRootElement(name = "informativaMaster")
  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  public static class InformativaMaster {
    @XmlElement(name = "dataPubblicazione")
    @XmlJavaTypeAdapter(XMLDateAdapter.class)
    private LocalDateTime dataPubblicazione;

    @XmlElement(name = "dataInizioValidita")
    @XmlJavaTypeAdapter(XMLDateAdapter.class)
    private LocalDateTime dataInizioValidita;

    private String urlInformazioniPSP;
    private Boolean stornoPagamento;
    private Boolean marcaBolloDigitale;
    @ToString.Exclude private String logoPSP;
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  public static class ListaInformativaDetail {
    private List<InformativaDetail> informativaDetail;
  }

  @XmlRootElement(name = "informativaDetail")
  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  @NotNull
  @ToString
  public static class InformativaDetail {
    private String identificativoIntermediario;
    private String identificativoCanale;
    private String tipoVersamento;
    private Long modelloPagamento;
    private Long priorita;
    private Long canaleApp;

    private IdentificazioneServizio identificazioneServizio;

    private ListaInformazioniServizio listaInformazioniServizio;

    private CostiServizio costiServizio;

    @XmlElementWrapper
    @XmlElements({@XmlElement(name = "paroleChiave", type = String.class)})
    private List<String> listaParoleChiave;

    private List<ConvenzioniCosti> listaConvenzioni;
  }

  @XmlRootElement(name = "identificazioneServizio")
  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  public static class IdentificazioneServizio {
    private String nomeServizio;
    @ToString.Exclude private String logoServizio;
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  public static class ListaInformazioniServizio {
    private List<InformazioniServizio> informazioniServizio;
  }

  @XmlRootElement(name = "informazioniServizio")
  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  public static class InformazioniServizio {
    private String codiceLingua;
    private String descrizioneServizio;
    private String disponibilitaServizio;
    private String urlInformazioniCanale;
    private String limitazioniServizio;
  }

  @XmlRootElement(name = "costiServizio")
  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  public static class CostiServizio {
    private String tipoCostoTransazione;
    private String tipoCommissione;

    private ListaFasceCostoServizio listaFasceCostoServizio;

    private Double costoConvenzione;
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  public static class ListaFasceCostoServizio {
    private List<FasciaCostoServizio> fasciaCostoServizio;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  public static class FasciaCostoServizio {
    private Double importoMassimoFascia;
    private Double costoFisso;
    private Double valoreCommissione;

    private List<ConvenzioniCosti> listaConvenzioniCosti;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  public static class ConvenzioniCosti {
    private String codiceConvenzione;
  }
}
