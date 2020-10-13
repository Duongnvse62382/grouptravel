package com.fpt.gta.data.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubsetSum {
    private BigDecimal sum;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubsetSum subsetSum = (SubsetSum) o;
        return getSum().compareTo(subsetSum.getSum())==0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSum());
    }
}
