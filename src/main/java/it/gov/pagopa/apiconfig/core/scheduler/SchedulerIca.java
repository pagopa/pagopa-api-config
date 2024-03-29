package it.gov.pagopa.apiconfig.core.scheduler;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.scheduler.storage.AzureStorageInteraction;
import it.gov.pagopa.apiconfig.starter.entity.Iban;
import it.gov.pagopa.apiconfig.starter.entity.IbanMaster;
import it.gov.pagopa.apiconfig.starter.entity.IcaBinaryFile;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.repository.IbanMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanRepository;
import it.gov.pagopa.apiconfig.starter.repository.IcaBinaryFileRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SchedulerIca {

  @Autowired private PaRepository paRepository;

  @Autowired private IbanRepository ibanRepository;

  @Autowired private IbanMasterRepository ibanMasterRepository;

  @Autowired private IcaBinaryFileRepository icaBinaryFileRepository;

  @Autowired private AzureStorageInteraction azureStorageInteraction;

  @Transactional
  public void updateIcaFile() {
    LocalDateTime previousExecution = LocalDateTime.now(ZoneOffset.UTC).minusDays(1L);
    Map<String, String> updatedEcFiscalCodeIcas =
        azureStorageInteraction.getUpdatedEC(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(previousExecution));
    List<Pa> creditorInstitutions =
        paRepository
            .findByIdDominioIn(new ArrayList<>(updatedEcFiscalCodeIcas.keySet()))
            .orElseThrow(() -> new AppException(AppError.CREDITOR_INSTITUTIONS_NOT_FOUND));
    creditorInstitutions.forEach(
        ec -> {
          List<IbanMaster> ibanAttributeMasters = ibanMasterRepository.findByFkPa(ec.getObjId());
          List<Iban> ibans =
              ibanRepository.findByObjIdIn(
                  ibanAttributeMasters.stream()
                      .map(IbanMaster::getObjId)
                      .collect(Collectors.toList()));
          byte[] icaBinaryFileXml = createXml(ec, ibans, updatedEcFiscalCodeIcas);
          Optional<IcaBinaryFile> oldIcaBinaryFile =
              icaBinaryFileRepository.findByIdDominio(ec.getIdDominio());
          if (oldIcaBinaryFile.isEmpty()) {
            IcaBinaryFile newIcaBinaryFile =
                IcaBinaryFile.builder().idDominio(ec.getIdDominio()).build();
            setIcaBinaryFileFields(newIcaBinaryFile, icaBinaryFileXml);
            icaBinaryFileRepository.save(newIcaBinaryFile);
          } else {
            setIcaBinaryFileFields(oldIcaBinaryFile.get(), icaBinaryFileXml);
            icaBinaryFileRepository.save(oldIcaBinaryFile.get());
          }
        });
  }

  @Scheduled(cron = "${cron.job.schedule.expression}")
  @Async
  @Transactional
  public void updateIcaFileAsync() {
    updateIcaFile();
  }

  private void setIcaBinaryFileFields(IcaBinaryFile icaBinaryFile, byte[] icaBinaryFileXml) {
    icaBinaryFile.setFileContent(icaBinaryFileXml);
    icaBinaryFile.setFileSize((long) icaBinaryFileXml.length);
    icaBinaryFile.setFileHash(getHash(icaBinaryFileXml));
  }

  private byte[] createXml(Pa pa, List<Iban> ibans, Map<String, String> publicationEcRelation) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      XMLOutputFactory xof = XMLOutputFactory.newInstance();
      XMLStreamWriter writer = xof.createXMLStreamWriter(outputStream, "UTF-8");
      XMLStreamWriter xtw = new IndentingXMLStreamWriter(writer);
      xtw.writeStartDocument("UTF-8", "1.0");
      xtw.writeStartElement("informativaContoAccredito");
      xtw.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
      xtw.writeAttribute(
          "http://www.w3.org/2001/XMLSchema-instance",
          "noNamespaceSchemaLocation",
          "InformativaContoAccredito _v02.xsd");
      writeElement(
          xtw, "identificativoFlusso", pa.getIdDominio() + "_" + LocalDateTime.now().toString());
      writeElement(xtw, "identificativoDominio", pa.getIdDominio());
      writeElement(xtw, "ragioneSociale", pa.getRagioneSociale());
      writeElement(xtw, "dataPubblicazione", publicationEcRelation.get(pa.getIdDominio()));
      writeElement(xtw, "dataInizioValidita", LocalDateTime.now().toString());
      xtw.writeStartElement("contiDiAccredito");
      ibans.forEach(iban -> writeIbanElement(xtw, iban));
      xtw.writeEndElement();
      xtw.writeEndElement();
      xtw.writeEndDocument();
      xtw.flush();
      xtw.close();
    } catch (XMLStreamException e) {
      throw new AppException(AppError.ICA_XML_ERROR);
    }
    return outputStream.toByteArray();
  }

  private void writeElement(XMLStreamWriter xtw, String elementName, String elementValue) {
    try {
      xtw.writeStartElement(elementName);
      xtw.writeCharacters(elementValue);
      xtw.writeEndElement();
    } catch (XMLStreamException e) {
      throw new AppException(AppError.ICA_XML_ERROR);
    }
  }

  private void writeIbanElement(XMLStreamWriter xtw, Iban iban) {
    try {
      xtw.writeStartElement("infoContoDiAccreditoPair");
      xtw.writeStartElement("ibanAccredito");
      xtw.writeCharacters(iban.getIban());
      xtw.writeEndElement();
      xtw.writeEmptyElement("idBancaSeller");
      xtw.writeEndElement();
    } catch (XMLStreamException e) {
      throw new AppException(AppError.ICA_XML_ERROR);
    }
  }

  private byte[] getHash(byte[] input) {
    byte[] hashedOutput = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      hashedOutput = md.digest(input);
    } catch (NoSuchAlgorithmException e) {
      throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
    }
    return hashedOutput;
  }
}
