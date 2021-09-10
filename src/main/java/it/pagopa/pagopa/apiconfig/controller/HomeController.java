package it.pagopa.pagopa.apiconfig.controller;

import it.pagopa.pagopa.apiconfig.models.Pa;
import it.pagopa.pagopa.apiconfig.repository.PaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeController {

    @Autowired
    private PaRepository paRepository;

    @GetMapping("")
    List<Pa> home() {
        return paRepository.findAll();
    }
}
