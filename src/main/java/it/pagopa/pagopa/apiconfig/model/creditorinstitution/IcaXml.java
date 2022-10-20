package it.pagopa.pagopa.apiconfig.model.creditorinstitution;

import it.pagopa.pagopa.apiconfig.util.XMLDateAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.List;

@XmlRootElement(name = "informativaContoAccredito")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
public class IcaXml {

    private String identificativoFlusso;
    private String identificativoDominio;
    private String ragioneSociale;

    @XmlElement(name = "dataPubblicazione")
    @XmlJavaTypeAdapter(XMLDateAdapter.class)
    private LocalDateTime dataPubblicazione;

    @XmlElement(name = "dataInizioValidita")
    @XmlJavaTypeAdapter(XMLDateAdapter.class)
    private LocalDateTime dataInizioValidita;

    @XmlElementWrapper
    @XmlElements({
            @XmlElement(name = "infoContoDiAccreditoPair", type = InfoContoDiAccreditoPair.class),
            @XmlElement(name = "ibanAccredito", type = String.class)
    })
    private List<Object> contiDiAccredito;

    @XmlRootElement(name = "infoContoDiAccreditoPair")
    @XmlAccessorType(XmlAccessType.FIELD)
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class InfoContoDiAccreditoPair {
        @XmlElement
        private String ibanAccredito;
        @XmlElement
        private String idBancaSeller;
    }

}
