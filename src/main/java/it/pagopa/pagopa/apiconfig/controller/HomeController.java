package it.pagopa.pagopa.apiconfig.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class HomeController {


    /**
     * @return redirect to Swagger page documentation
     */
    @Hidden
    @GetMapping("")
    public RedirectView home() {
        return new RedirectView("/apiconfig/api/v1/swagger-ui.html");
    }

    /**
     * Health Check
     *
     * @return ok
     */
    @Hidden
    @GetMapping("/info")
    public ResponseEntity<Void> healthCheck() {
        return ResponseEntity.ok().build();
    }
}
