package it.pagopa.pagopa.apiconfig.model.creditorinstitution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

@XmlRootElement(name = "informativaPSP")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CdiXml {

    private String identificativoFlusso;
    private String identificativoPSP;
    private String ragioneSociale;
    private InformativaMaster informativaMaster;
    private List<InformativaDetail> listaInformativaDetail;
    @Nullable
    private String mybankIDVS;


    @XmlRootElement(name = "informativaMaster")
    @XmlAccessorType(XmlAccessType.FIELD)
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class InformativaMaster {
        private XMLGregorianCalendar dataPubblicazione;
        private XMLGregorianCalendar dataInizioValidita;
        private String urlInformazioniPSP;
        private Boolean stornoPagamento;
        private Boolean marcaBolloDigitale;
        private String logoPSP;

    }

    @XmlRootElement(name = "informativaDetail")
    @XmlAccessorType(XmlAccessType.FIELD)
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class InformativaDetail {
        private String identificativoIntermediario;
        private String identificativoCanale;
        private String tipoVersamento;
        private Long modelloPagamento;
        private Long priorita;
        private Long canaleApp;
        @Nullable
        private IdentificazioneServizio identificazioneServizio;
        @Nullable
        private List<InformazioniServizio> listaInformazioniServizio;
        @Nullable
        private CostiServizio costiServizio;
        @Nullable
        private List<ParolaChiave> listaParoleChiave;
        @Nullable
        private List<ConvenzioniCosti> listaConvenzioni;
    }

    @XmlRootElement(name = "paroleChiave")
    @XmlAccessorType(XmlAccessType.FIELD)
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class ParolaChiave {
        private String paroleChiave;
    }

    @XmlRootElement(name = "identificazioneServizio")
    @XmlAccessorType(XmlAccessType.FIELD)
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class IdentificazioneServizio {
        private String nomeServizio;
        private String logoServizio;
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
        @Nullable
        private List<FasciaCostoServizio> listaFasceCostoServizio;
        @Nullable
        private Double costoConvenzione;
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
        @Nullable
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
