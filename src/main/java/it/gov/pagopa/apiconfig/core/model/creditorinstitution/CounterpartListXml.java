package it.gov.pagopa.apiconfig.core.model.creditorinstitution;

import java.util.List;
import javax.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "listaInformativeControparte")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CounterpartListXml {

  @XmlElement(name = "informativaControparte")
  private List<CounterpartXml> list;
}
