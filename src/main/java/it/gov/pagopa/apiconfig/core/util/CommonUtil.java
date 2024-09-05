package it.gov.pagopa.apiconfig.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.gov.pagopa.apiconfig.core.exception.AppError;
import it.gov.pagopa.apiconfig.core.exception.AppException;
import it.gov.pagopa.apiconfig.core.model.CheckItem;
import it.gov.pagopa.apiconfig.core.model.PageInfo;
import it.gov.pagopa.apiconfig.core.model.filterandorder.Filter;
import it.gov.pagopa.apiconfig.core.model.filterandorder.FilterAndOrder;
import it.gov.pagopa.apiconfig.core.model.filterandorder.Order;
import it.gov.pagopa.apiconfig.core.model.filterandorder.OrderType;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class CommonUtil {

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * @param page Page returned from the database
     * @return return the page info
     */
    public <T> PageInfo buildPageInfo(Page<T> page) {
        return PageInfo.builder()
                .page(page.getNumber())
                .limit(page.getSize())
                .totalPages(page.getTotalPages())
                .itemsFound(page.getNumberOfElements())
                .totalItems(page.getTotalElements())
                .build();
    }

    /**
     * @param timestamp {@link Timestamp} to convert
     * @return convert timestamp to {@link OffsetDateTime}
     */
    public static OffsetDateTime toOffsetDateTime(Timestamp timestamp) {
        return timestamp != null
                ? OffsetDateTime.of(timestamp.toLocalDateTime(), ZoneOffset.UTC)
                : null;
    }

    /**
     * @param calendar to convert
     * @return convert calendar to {@link Timestamp}
     */
    public static Timestamp toTimestamp(XMLGregorianCalendar calendar) {
        return Timestamp.from(calendar.toGregorianCalendar().toZonedDateTime().toInstant());
    }

    /**
     * @param offsetDateTime to convert
     * @return convert offsetDateTime to {@link Timestamp}
     */
    public static Timestamp toTimestamp(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ?
            Timestamp.from(offsetDateTime.toInstant())
            : null;
    }

    /**
     * @param value value to deNullify.
     * @return return empty string if value is null
     */
    public static String deNull(String value) {
        return Optional.ofNullable(value).orElse("");
    }

    /**
     * @param value value to deNullify.
     * @return return empty string if value is null
     */
    public static String deNull(Object value) {
        return Optional.ofNullable(value).orElse("").toString();
    }

    /**
     * @param value value to deNullify.
     * @return return false if value is null
     */
    public static Boolean deNull(Boolean value) {
        return Optional.ofNullable(value).orElse(false);
    }

    /**
     * Get the field name from enumerations that implements {@link OrderType}. See {@link Order}
     * class. The field name identify the column
     *
     * @param filterAndOrder object with sorting info
     * @return a {@link Sort} object to use with SpringRepository
     */
    public static Sort getSort(FilterAndOrder filterAndOrder) {
        var order =
                new Sort.Order(
                        filterAndOrder.getOrder().getOrdering(),
                        filterAndOrder.getOrder().getOrderBy().getColumnName());
        return Sort.by(order.ignoreCase());
    }

    /**
     * @param filterByCode filter by code
     * @param filterByName filter by name
     * @param orderBy      order by column
     * @param ordering     direction of ordering
     * @return {@link FilterAndOrder} object
     */
    public static FilterAndOrder getFilterAndOrder(
            String filterByCode, String filterByName, OrderType orderBy, Sort.Direction ordering) {
        return FilterAndOrder.builder()
                .filter(Filter.builder().code(filterByCode).name(filterByName).build())
                .order(Order.builder().orderBy(orderBy).ordering(ordering).build())
                .build();
    }

    /**
     * @param filterByCode    filter by code
     * @param filterByName    filter by name
     * @param filterByEnabled filter by enabled
     * @param orderBy         order by column
     * @param ordering        direction of ordering
     * @return {@link FilterAndOrder} object
     */
    public static FilterAndOrder getFilterAndOrder(
            String filterByCode, String filterByName, Boolean filterByEnabled, OrderType orderBy, Sort.Direction ordering) {
        return FilterAndOrder.builder()
                .filter(Filter.builder().code(filterByCode).name(filterByName).enabled(filterByEnabled).build())
                .order(Order.builder().orderBy(orderBy).ordering(ordering).build())
                .build();
    }

    /**
     * @param example filter
     * @return a new Example using the custom ExampleMatcher
     */
    public static <T> Example<T> getFilters(T example) {
        ExampleMatcher matcher =
                ExampleMatcher.matching()
                        .withIgnoreNullValues()
                        .withIgnoreCase(true)
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        return Example.of(example, matcher);
    }

    /**
     * @param iban an italian IBAN with length 27
     * @return ABI in the IBAN
     */
    public static String getAbiFromIban(String iban) {
        return iban.substring(5, 10);
    }

    /**
     * @param iban an italian IBAN with length 27
     * @return Conto Corrente in the IBAN
     */
    public static String getCcFromIban(String iban) {
        return iban.substring(15, 27);
    }

    /**
     * @param xml    file XML to validate
     * @param xsdUrl url of XSD
     * @throws SAXException       if XML is not valid
     * @throws IOException        if XSD schema not found
     * @throws XMLStreamException error during read XML
     */
    public static void syntaxValidation(MultipartFile xml, String xsdUrl)
            throws SAXException, IOException, XMLStreamException {
        syntaxValidation(xml.getInputStream(), xsdUrl);
    }

    /**
     * @param inputStream file xml to validate
     * @param xsdUrl      url of XSD
     * @throws SAXException       if XML is not valid
     * @throws IOException        if XSD schema not found
     * @throws XMLStreamException error during read XML
     */
    public static void syntaxValidation(InputStream inputStream, String xsdUrl)
            throws SAXException, IOException, XMLStreamException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // to be compliant, prohibit the use of all protocols by external entities:
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        javax.xml.validation.Schema schema = factory.newSchema(new URL(xsdUrl));
        Validator validator = schema.newValidator();

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        // to be compliant, completely disable DOCTYPE declaration:
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
        StAXSource source = new StAXSource(xmlStreamReader);
        validator.validate(source);
    }

    public static String getExceptionErrors(String stringException) {
        Matcher matcher = Pattern.compile("lineNumber: \\d*").matcher(stringException);
        String lineNumber = "";
        if (matcher.find()) {
            lineNumber = matcher.group(0);
        }
        String detail = stringException.substring(stringException.lastIndexOf(":") + 1).trim();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(detail);
        if (lineNumber.length() > 0) {
            stringBuilder.append(" Error at ").append(lineNumber);
        }
        return stringBuilder.toString();
    }

    public static CheckItem checkData(String title, Object data, Object target, String action) {
        CheckItem.Validity validity =
                (target == null && data == null) || (target != null && target.equals(data))
                        ? CheckItem.Validity.VALID
                        : CheckItem.Validity.NOT_VALID;
        return CheckItem.builder()
                .title(title)
                .value(data != null ? data.toString() : null)
                .valid(validity)
                .note(validity.equals(CheckItem.Validity.VALID) ? "" : action)
                .build();
    }

    /**
     * @param startValidityDate check if the validity is after today
     * @return item with validity info
     */
    public static CheckItem checkValidityDate(LocalDateTime startValidityDate) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        String value;
        String note;
        CheckItem.Validity validity;

        if (startValidityDate == null) {
            validity = CheckItem.Validity.NOT_VALID;
            value = "-";
            note = "Validity Date not parsable";
        } else {
            validity =
                    startValidityDate.toLocalDate().isBefore(tomorrow)
                            ? CheckItem.Validity.NOT_VALID
                            : CheckItem.Validity.VALID;
            value = startValidityDate.toString();
            note =
                    validity.equals(CheckItem.Validity.VALID)
                            ? ""
                            : "Validity start date must be greater than the today's date";
        }

        return CheckItem.builder()
                .title("Check validity date")
                .value(value)
                .valid(validity)
                .note(note)
                .build();
    }

    /**
     * check if the dueDate is after the validityDate
     *
     * @param validityDate
     * @param dueDate
     * @return item with validity info
     */
    public static CheckItem checkDueDate(LocalDateTime validityDate, LocalDateTime dueDate) {
        String value;
        String note;
        CheckItem.Validity validity;

        if (validityDate == null) {
            validity = CheckItem.Validity.NOT_VALID;
            value = "-";
            note = "Validity Date not parsable";
        } else if (dueDate == null) {
            validity = CheckItem.Validity.NOT_VALID;
            value = "-";
            note = "Due Date not parsable";
        } else {
            validity =
                    dueDate.toLocalDate().isAfter(validityDate.toLocalDate())
                            ? CheckItem.Validity.VALID
                            : CheckItem.Validity.NOT_VALID;
            value = validityDate.toString();
            note =
                    validity.equals(CheckItem.Validity.VALID)
                            ? ""
                            : "Due date must be greater than the validity date";
        }

        return CheckItem.builder()
                .title("Check due date")
                .value(value)
                .valid(validity)
                .note(note)
                .build();
    }

    public static boolean checkIfLocalDatesNotEquals(LocalDateTime date1, LocalDateTime date2) {
        return date1 == null || date2 == null || !date1.toLocalDate().equals(date2.toLocalDate());
    }

    /**
     * @param file  XML to map
     * @param clazz class of model result
     * @return XML mapped in the model
     */
    public static <T> T mapXml(MultipartFile file, Class<T> clazz) {
        try {
            return mapXml(file.getInputStream(), clazz);
        } catch (IOException e) {
            throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * @param inputStream XML to map
     * @param clazz       class of model result
     * @return XML mapped in the model
     */
    public static <T> T mapXml(InputStream inputStream, Class<T> clazz) {
        T model;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            model = (T) context.createUnmarshaller().unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
        }
        return model;
    }

    /**
     * @param headers header of the CSV file
     * @param rows    data of the CSV file
     * @return byte array of the CSV using commas (,) as separator
     */
    public static byte[] createCsv(List<String> headers, List<List<String>> rows) {
        var csv = new StringBuilder();
        csv.append(String.join(",", headers));
        rows.forEach(row -> csv.append(System.lineSeparator()).append(String.join(",", row)));
        return csv.toString().getBytes();
    }

    /**
     * @param env environment of the application
     * @return String to help build the url with the correct application environment
     */
    public String getEnvironment(String env) {
        if (env.equals("PROD")) {
            return "";
        }
        return "." + env.toLowerCase();
    }

    /**
     * @param file  Json to map
     * @param clazz class of model result
     * @return JSON mapped in the model
     */
    public static <T> T mapJSON(InputStream inputStream, Class<T> clazz) {
        T model;
        try {
            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            model = mapper.readValue(inputStream, clazz);
        } catch (Exception e) {
            throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
        }
        return model;
    }

    /**
     * Compute the station's connection flag
     *
     * @param station station model
     * @return true if the station is configured to be synchronous, false otherwise
     */
    public static boolean isConnectionSync(Stazioni station) {
        return (org.apache.commons.lang3.StringUtils.isNotBlank(station.getTargetPath()) && org.apache.commons.lang3.StringUtils.isNotBlank(station.getRedirectIp()))
                || StringUtils.isNotBlank(station.getTargetPathPof());
    }
}
