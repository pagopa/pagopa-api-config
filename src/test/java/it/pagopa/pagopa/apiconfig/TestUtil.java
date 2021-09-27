package it.pagopa.pagopa.apiconfig;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;

@UtilityClass
public class TestUtil {

    /**
     * @param relativePath Path from source root of the json file
     * @return the Json string read from the file
     * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable byte sequence is read
     */
    public String readJsonFromFile(String relativePath) throws IOException {
        ClassLoader classLoader = TestUtil.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(relativePath)).getPath());
        return Files.readString(file.toPath());
    }

    /**
     * @param object to map into the Json string
     * @return object as Json string
     * @throws JsonProcessingException if there is an error during the parsing of the object
     */
    public String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }


    public <T> Page<T> mockPage(List<T> content, int limit, int pageNumber) {
        Page<T> page = Mockito.mock(Page.class);
        when(page.getTotalPages()).thenReturn((int) Math.ceil((double) content.size()/limit));
        when(page.getNumberOfElements()).thenReturn(content.size());
        when(page.getNumber()).thenReturn(pageNumber);
        when(page.getSize()).thenReturn(limit);
        when(page.getContent()).thenReturn(content);
        when(page.stream()).thenReturn(content.stream());
        return page;
    }

}
