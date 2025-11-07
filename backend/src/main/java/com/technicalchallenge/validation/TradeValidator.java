package com.technicalchallenge.validation;

import com.technicalchallenge.dto.CashflowDTO;
import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.mapper.CashflowMapper;
import com.technicalchallenge.model.Cashflow;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TradeValidator {

    @Autowired
    private CashflowMapper cashflowMapper;

    public ValidationResult validateTradeBusinessRules(TradeDTO tradeDTO) {
        ValidationResult result = ValidationResult.success();


    if (tradeDTO.getTradeDate() != null) {
        // Trade date cannot be more than 30 days in the past
            if (tradeDTO.getTradeDate().isBefore(LocalDate.now().minusDays(30))) {
                result.addError("Trade date cannot be more than 30 days in the past");
            }
        }

        // Start date cannot be before trade date
        if (tradeDTO.getTradeStartDate() != null && tradeDTO.getTradeDate() != null) {
            if (tradeDTO.getTradeStartDate().isBefore(tradeDTO.getTradeDate())) {
                result.addError("Start date cannot be before trade date");
            }
        }

        // Maturity date cannot be before trade date
        if (tradeDTO.getTradeMaturityDate() != null && tradeDTO.getTradeDate() != null) {
            if (tradeDTO.getTradeMaturityDate().isBefore(tradeDTO.getTradeDate())) {
                result.addError("Maturity date cannot be before trade date");
            }
        }

        // Maturity date cannot be before start date
        if (tradeDTO.getTradeMaturityDate() != null && tradeDTO.getTradeStartDate() != null) {
            if (tradeDTO.getTradeMaturityDate().isBefore(tradeDTO.getTradeStartDate())) {
                result.addError("Maturity date cannot be before start date");
            }
        }

        return result;
    }

    public ValidationResult validateTradeLegConsistency(TradeDTO tradeDTO) {
        ValidationResult result = ValidationResult.success();

        List<TradeLegDTO> tradeLegs = tradeDTO.getTradeLegs();

        //Validate trade has exactly 2 legs
        if (tradeLegs.size() != 2) {
            result.addError("Trade must have exactly 2 legs");
        }

        TradeLegDTO leg1 = tradeLegs.get(0);
        TradeLegDTO leg2 = tradeLegs.get(1);

        List<CashflowDTO> leg1CashflowDTOs = leg1.getCashflows();
        List<CashflowDTO> leg2CashflowDTOs = leg2.getCashflows();

        List<Cashflow> leg1Cashflows = leg1CashflowDTOs.stream()
            .map(cashflowMapper::toEntity)
            .collect(Collectors.toList());
        
        List<Cashflow> leg2Cashflows = leg2CashflowDTOs.stream()
            .map(cashflowMapper::toEntity)
            .collect(Collectors.toList());

        String leg1PayReceiveFlag = leg1.getPayReceiveFlag();
        String leg2PayReceiveFlag = leg2.getPayReceiveFlag();
        leg1PayReceiveFlag = leg1PayReceiveFlag == null ? "" : leg1PayReceiveFlag.trim().toUpperCase();
        leg2PayReceiveFlag = leg2PayReceiveFlag == null ? "" : leg2PayReceiveFlag.trim().toUpperCase();

        // Both legs must have identical maturity dates
        if (!leg1Cashflows.isEmpty() && !leg2Cashflows.isEmpty()) {
            LocalDate leg1Maturity = leg1Cashflows.get(leg1Cashflows.size() - 1).getValueDate();
            LocalDate leg2Maturity = leg2Cashflows.get(leg2Cashflows.size() - 1).getValueDate();

            if (!leg1Maturity.equals(leg2Maturity)) {
                result.addError("Trade legs must have identical maturity dates");
            }
        }

        // Legs must have opposite pay/receive flags
       if (!leg1PayReceiveFlag.isEmpty() && !leg2PayReceiveFlag.isEmpty()) {

        boolean isOpposite = (leg1PayReceiveFlag.equals("PAY") && leg2PayReceiveFlag.equals("RECEIVE")) || 
                             (leg1PayReceiveFlag.equals("RECEIVE") && leg2PayReceiveFlag.equals("PAY"));

            if (!isOpposite) {
                result.addError("Legs must have opposite pay/receive flags");
            }
       }

       // Floating legs must have an index specified AND Fixed legs must have a valid rate
       for (TradeLegDTO leg : tradeLegs) {
        String legType = leg.getLegType() == null ? "" : leg.getLegType().trim().toUpperCase();

        if (legType.equals("FLOATING")) {
            if (leg.getIndexName() == null || leg.getIndexName().trim().isEmpty()) {
                result.addError("Floating legs must have an index specified");
            }
        } else if (legType.equals("FIXED")) {
            if (leg.getRate() == null || leg.getRate() < 0) {
                result.addError("Fixed legs must have a valid rate (>= 0)");
            }
        }        
       }
       
       return result;
    }

}
