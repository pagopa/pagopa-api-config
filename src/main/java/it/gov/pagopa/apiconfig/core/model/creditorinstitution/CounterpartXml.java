package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "informativaControparte")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CounterpartXml {

  private String identificativoFlusso;
  private String identificativoDominio;
  private String ragioneSociale;
  private XMLGregorianCalendar dataPubblicazione;
  private XMLGregorianCalendar dataInizioValidita;
  private Boolean pagamentiPressoPSP;
  private ErogazioneServizio erogazioneServizio;

  @XmlRootElement(name = "erogazioneServizio")
  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  public static class ErogazioneServizio {
    private List<Erogazione> disponibilita;
    private List<Erogazione> indisponibilita;
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  public static class Erogazione {
    private String tipoPeriodo;
    private String giorno;
    private FasciaOraria fasciaOraria;
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Getter
  public static class FasciaOraria {
    private XMLGregorianCalendar fasciaOrariaDa;
    private XMLGregorianCalendar fasciaOrariaA;
  }
}
