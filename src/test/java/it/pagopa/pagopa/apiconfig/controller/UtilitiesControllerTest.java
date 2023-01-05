package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.ApiConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
class UtilitiesControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void findIban() throws Exception {
        String url = "/ibans/IT01A1234567890123456789012";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
