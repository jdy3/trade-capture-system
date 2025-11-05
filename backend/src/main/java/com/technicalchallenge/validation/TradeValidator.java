package com.technicalchallenge.validation;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class TradeValidator {

    public ValidationResult validateTradeBusinessRules(TradeDTO tradeDTO) {
        ValidationResult result = ValidationResult.success();


    if (tradeDTO.getTradeDate() != null) {
            if (tradeDTO.getTradeDate().isBefore(LocalDate.now().minusDays(30))) {
                result.addError("Trade date cannot be more than 30 days in the past");
            }
        }

        if (tradeDTO.getTradeStartDate() != null && tradeDTO.getTradeDate() != null) {
            if (tradeDTO.getTradeStartDate().isBefore(tradeDTO.getTradeDate())) {
                result.addError("Start date cannot be before trade date");
            }
        }

        if (tradeDTO.getTradeMaturityDate() != null && tradeDTO.getTradeDate() != null) {
            if (tradeDTO.getTradeMaturityDate().isBefore(tradeDTO.getTradeDate())) {
                result.addError("Maturity date cannot be before trade date");
            }
        }

        if (tradeDTO.getTradeMaturityDate() != null && tradeDTO.getTradeStartDate() != null) {
            if (tradeDTO.getTradeMaturityDate().isBefore(tradeDTO.getTradeStartDate())) {
                result.addError("Maturity date cannot be before start date");
            }
        }

        return result;
    }

}
