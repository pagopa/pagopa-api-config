package it.pagopa.pagopa.apiconfig.model.creditorinstitution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Time;

@XmlRootElement(name = "informativaContoAccredito")
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
        private Erogazione disponibilita;
        private Erogazione indisponibilita;
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
        private Time fasciaOrariaDa;
        private Time fasciaOrariaA;
    }

}
