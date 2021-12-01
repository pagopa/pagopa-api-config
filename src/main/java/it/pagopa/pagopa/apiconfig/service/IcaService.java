package it.pagopa.pagopa.apiconfig.service;

import it.pagopa.pagopa.apiconfig.config.XSDProperties;
import it.pagopa.pagopa.apiconfig.entity.InformativeContoAccreditoMaster;
import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Ica;
import it.pagopa.pagopa.apiconfig.model.creditorinstitution.Icas;
import it.pagopa.pagopa.apiconfig.repository.InformativeContoAccreditoMasterRepository;
import it.pagopa.pagopa.apiconfig.util.CommonUtil;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.validation.constraints.NotNull;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class IcaService {

    @Autowired
    InformativeContoAccreditoMasterRepository informativeContoAccreditoMasterRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    XSDProperties xsdProperties;

    public Icas getIcas(@NotNull Integer limit, @NotNull Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit);
        Page<InformativeContoAccreditoMaster> page = informativeContoAccreditoMasterRepository.findAll(pageable);
        return Icas.builder()
                .icaList(getIcaList(page))
                .pageInfo(CommonUtil.buildPageInfo(page))
                .build();
    }


    public byte[] getIca(@NotNull String idIca, String creditorInstitutionCode) {
        Optional<InformativeContoAccreditoMaster> result = informativeContoAccreditoMasterRepository.findByIdInformativaContoAccreditoPaAndFkPa_IdDominio(idIca, creditorInstitutionCode);
        if (result.isEmpty()) {
            throw new AppException(AppError.ICA_NOT_FOUND, idIca);
        }
        return result.get().getFkBinaryFile().getFileContent();
    }


    /**
     * Maps InformativeContoAccreditoMaster objects stored in the DB in a List of Ica
     *
     * @param page page of {@link InformativeContoAccreditoMaster} returned from the database
     * @return a list of {@link Ica}.
     */
    private List<Ica> getIcaList(Page<InformativeContoAccreditoMaster> page) {
        return page.stream()
                .map(elem -> modelMapper.map(elem, Ica.class))
                .collect(Collectors.toList());
    }

    public Map verifyXSD(File xml) {
        boolean xsdEvaluated = false;
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        String lineNumber = "";
        String detail = "";
        String xsdSchema = xsdProperties.getIca();
        try {
            javax.xml.validation.Schema schema = factory.newSchema(new URL(xsdSchema));
            Validator validator = schema.newValidator();
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            FileInputStream inputStream = new FileInputStream(xml);
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
            StAXSource source = new StAXSource(xmlStreamReader);
            validator.validate(source);
            xsdEvaluated = true;
            detail = "XML is valid against the XSD schema.";
        }
        catch (SAXException | IOException | XMLStreamException e) {
            String stringException = e.getMessage();
            Matcher matcher = Pattern.compile("lineNumber: [0-9]*").matcher(stringException);
            if (matcher.find()) {
                lineNumber = matcher.group(0);
            }
            detail = stringException.substring(stringException.lastIndexOf(":")+1).trim();
            e.printStackTrace();
        }


        Map response = new HashMap<>();
        response.put("xsdCompliant", xsdEvaluated);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(detail);
        if (lineNumber.length() > 0) {
            stringBuilder.append(" Error at " + lineNumber);
        }
        response.put("detail", stringBuilder);
        response.put("xsdSchema", xsdSchema);
        System.out.println("DETAIL " + stringBuilder);
        System.out.println("XSD SCHEMA " + xsdSchema);
        System.out.println("XSD COMPLIANT " + xsdEvaluated);
        return response;
    }
}
