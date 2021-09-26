package it.pagopa.pagopa.apiconfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.docstring.DocString;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources")
@CucumberContextConfiguration
@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
public class CucumberIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @PersistenceContext
    private EntityManager em;

    private MvcResult lastCall;
    private HttpHeaders headers;
    private String body;

    @Given("^(\\w+) in database:$")
    @Transactional
    public void init_db(String entity, DataTable table) throws Throwable {
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);
        for (Map<String, String> columns : rows) {
            String columnsNames = String.join(", ", columns.keySet());
            String values = String.join(", ", columns.values());
            em.createNativeQuery(
                    "INSERT INTO " + entity + "( " + columnsNames + ") " +
                            " SELECT  " + values + "  FROM dual " +
                            " WHERE NOT EXISTS (SELECT * " +
                            "                     FROM " + entity +
                            "                    WHERE OBJ_ID = " + columns.get("OBJ_ID") +
                            "                  )")
                    .executeUpdate();
        }
    }

    @Given("^headers:$")
    public void headers(DocString docString) throws Throwable {
        headers = new HttpHeaders();
        JsonNode json = new ObjectMapper().readValue(docString.getContent(), JsonNode.class);
        for (Iterator<String> it = json.fieldNames(); it.hasNext(); ) {
            String field = it.next();
            headers.add(field, json.get(field).asText());
        }

    }

    @Given("^body:$")
    public void body(DocString docString) {
        body = docString.getContent();
    }

    @When("^calls (GET|POST|PUT|DELETE) (.+)$")
    public void perform_call(String method, String url) throws Throwable {
        MockHttpServletRequestBuilder request = request(HttpMethod.valueOf(method), url);
        if (headers != null) {
            request.headers(headers);
        }
        if (body != null) {
            request.content(body);
        }
        lastCall = mvc.perform(request)
                .andReturn();
    }

    @Then("^receives status code of (\\d+)$")
    public void check_response_code(int statusCode) throws Throwable {
        assertEquals(statusCode, lastCall.getResponse().getStatus());
    }


}
