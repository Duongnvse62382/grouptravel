package com.fpt.gta.rest.setupdata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fpt.gta.model.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setupData")
public class SetupDataController {

    CurrencyService currencyService;

    @Autowired
    public SetupDataController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping
    public void setupCurrency() throws JsonProcessingException {
        currencyService.crawlCurrencyCode();
    }

}
