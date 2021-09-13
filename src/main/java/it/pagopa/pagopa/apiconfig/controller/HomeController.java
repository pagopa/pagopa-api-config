package it.pagopa.pagopa.apiconfig.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class HomeController {


    /**
     * @return redirect to Swagger page documentation
     */
    @ApiIgnore
    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("/apiconfig/swagger-ui.html");
    }
}
