package it.gov.pagopa.apiconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiGenerationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @Test
    void swaggerSpringPlugin() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/v3/api-docs").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(
                        (result) -> {
                            assertNotNull(result);
                            assertNotNull(result.getResponse());
                            final String content = result.getResponse().getContentAsString();
                            assertFalse(content.isBlank());
                            assertFalse(content.contains("${"), "Generated swagger contains placeholders");
                            Object swagger =
                                    objectMapper.readValue(result.getResponse().getContentAsString(), Object.class);
                            String formatted =
                                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(swagger);
                            Path basePath = Paths.get("openapi/");
                            Files.createDirectories(basePath);
                            Files.write(basePath.resolve("openapi.json"), formatted.getBytes());
                        });
    }
}
