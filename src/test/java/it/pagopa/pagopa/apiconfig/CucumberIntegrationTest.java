package it.pagopa.pagopa.apiconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import it.pagopa.pagopa.apiconfig.entity.Pa;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources")
@CucumberContextConfiguration
@SpringBootTest(classes = ApiConfig.class)
@AutoConfigureMockMvc
public class CucumberIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    PaRepository paRepository;

    @Given("^\\w+ in database:$")
    public void init_db(DataTable table) throws Throwable{
        List<Map<String, String>> rows = table.asMaps(String.class, String.class);

        for (Map<String, String> columns : rows) {
            Pa.class.getDeclaredField(columns.get()).set();
        }
        paRepository.save(Pa.builder()
                        .objId(entity.asList().get(0).)
                .build());

    }

    @When("^the client calls .*$")
    public void the_client_issues_GET_version() throws Throwable{
        String url = "/brokers?page=0";
        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Then("^the client receives status code of (\\d+)$")
    public void the_client_receives_status_code_of(int statusCode) throws Throwable {

    }

    @And("^the client receives server version (.+)$")
    public void the_client_receives_server_version_body(String version) throws Throwable {
    }

}
