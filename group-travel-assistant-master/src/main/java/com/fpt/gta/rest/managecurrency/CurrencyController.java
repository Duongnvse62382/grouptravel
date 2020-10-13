package com.fpt.gta.rest.managecurrency;

import com.fpt.gta.model.entity.CurrencyRate;
import com.fpt.gta.model.service.CurrencyService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    CurrencyService currencyService;
    ModelMapper modelMapper;

    @Autowired
    public CurrencyController(CurrencyService currencyService, ModelMapper modelMapper) {
        this.currencyService = currencyService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<CurrencyDTO> findAllCurrency() {
        List<CurrencyDTO> result = Arrays.asList(modelMapper.map(currencyService.findAllCurrency(), CurrencyDTO[].class));
        result.sort(Comparator.comparing(CurrencyDTO::getCode));
        return result;
    }

    @GetMapping("/convert")
    public BigDecimal convertCurrency(@RequestParam String firstCurrencyCode,
                                      @RequestParam String secondCurrencyCode) {
        return currencyService.getCurrencyRate(firstCurrencyCode, secondCurrencyCode).getRate();
    }
}
