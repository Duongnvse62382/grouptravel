package com.fpt.gta.data.dto;

import java.math.BigDecimal;

public class OcrDTO {
    BigDecimal bigDecimal;
    int count;
    int k;

    public OcrDTO(BigDecimal bigDecimal, int count) {
        this.bigDecimal = bigDecimal;
        this.count = count;
    }

    public OcrDTO(BigDecimal bigDecimal, int count, int k) {
        this.bigDecimal = bigDecimal;
        this.count = count;
        this.k = k;
    }

    public OcrDTO() {
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
