package com.fpt.gta.data.dto;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class CurrencyDTO  implements Serializable {
    private Integer id;
    private String name;
    private String code;
    private String symbol;

    public CurrencyDTO(@NonNull final String name, @NonNull final String code) {
        this.name = name;
        this.code = code;
    }

    public CurrencyDTO() {
    }
}
