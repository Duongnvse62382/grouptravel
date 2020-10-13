package com.fpt.gta.rest.managecurrency;

import lombok.Data;

@Data
public class CurrencyDTO {
    private Integer id;
    private String name;
    private String code;
    private String symbol;
}
