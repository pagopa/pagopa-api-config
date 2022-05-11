package it.pagopa.pagopa.apiconfig.util;

import it.pagopa.pagopa.apiconfig.exception.AppError;
import it.pagopa.pagopa.apiconfig.exception.AppException;
import it.pagopa.pagopa.apiconfig.model.PageInfo;
import it.pagopa.pagopa.apiconfig.model.filterandorder.Filter;
import it.pagopa.pagopa.apiconfig.model.filterandorder.FilterAndOrder;
import it.pagopa.pagopa.apiconfig.model.filterandorder.Order;
import it.pagopa.pagopa.apiconfig.model.filterandorder.OrderType;
import lombok.experimental.UtilityClass;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class CommonUtil {

    /**
     * @param zipcode zip code in long
     * @return zip code in string with the format %05d (add leading zeroes to number)
     * Example 00123
     */
    public static String numberToZipCode(Long zipcode) {
        return zipcode == null ? null : String.format("%05d", zipcode);
    }


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
                .build();
    }

    /**
     * @param timestamp {@link Timestamp} to convert
     * @return convert timestamp to {@link OffsetDateTime}
     */
    public static OffsetDateTime toOffsetDateTime(Timestamp timestamp) {
        return timestamp != null ? OffsetDateTime.of(timestamp.toLocalDateTime(), ZoneOffset.UTC) : null;
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
        return Timestamp.from(offsetDateTime.toInstant());
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
     * Get the field name from enumerations that implements {@link OrderType}.
     * See {@link Order} class.
     * The field name identify the column
     *
     * @param filterAndOrder object with sorting info
     * @return a {@link Sort} object to use with SpringRepository
     */
    public static Sort getSort(FilterAndOrder filterAndOrder) {
        var order = new Sort.Order(filterAndOrder.getOrder().getOrdering(), filterAndOrder.getOrder().getOrderBy().getColumnName());
        return Sort.by(order.ignoreCase());
    }


    /**
     * @param filterByCode filter by code
     * @param filterByName filter by name
     * @param orderBy      order by column
     * @param ordering     direction of ordering
     * @return {@link FilterAndOrder} object
     */
    public static FilterAndOrder getFilterAndOrder(String filterByCode, String filterByName, OrderType orderBy, Sort.Direction ordering) {
        return FilterAndOrder.builder()
                .filter(Filter.builder()
                        .code(filterByCode)
                        .name(filterByName)
                        .build())
                .order(Order.builder()
                        .orderBy(orderBy)
                        .ordering(ordering)
                        .build())
                .build();
    }

    /**
     * @param example filter
     * @return a new Example using the custom ExampleMatcher
     */
    public static <T> Example<T> getFilters(T example) {
        ExampleMatcher matcher = ExampleMatcher.matching()
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
    public static void syntacticValidationXml(MultipartFile xml, String xsdUrl) throws SAXException, IOException, XMLStreamException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        // to be compliant, prohibit the use of all protocols by external entities:
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        javax.xml.validation.Schema schema = factory.newSchema(new URL(xsdUrl));
        Validator validator = schema.newValidator();

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        // to be compliant, completely disable DOCTYPE declaration:
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

        InputStream inputStream = xml.getInputStream();
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(inputStream);
        StAXSource source = new StAXSource(xmlStreamReader);
        validator.validate(source);
    }

    /**
     * @param file  XML to map
     * @param clazz class of model result
     * @return XML mapped in the model
     */
    public static <T> T mapXml(MultipartFile file, Class<T> clazz) {
        T model;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            model = (T) context.createUnmarshaller()
                    .unmarshal(file.getInputStream());
        } catch (IOException | JAXBException e) {
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

}
