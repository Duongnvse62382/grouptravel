package com.fpt.gta.model.repository;

import com.fpt.gta.model.entity.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Integer> {

    @Query("select cr from CurrencyRate cr where cr.firstCurrency.code=?1 and cr.secondCurrency.code=?2")
    Optional<CurrencyRate> findCurrencyRateCurrencyCode(String firstCurrencyCode, String secondCurrencyCode);

}
