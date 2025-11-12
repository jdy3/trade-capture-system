package com.technicalchallenge.validation;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.repository.ApplicationUserRepository;
import com.technicalchallenge.repository.BookRepository;
import com.technicalchallenge.repository.CounterpartyRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TradeValidator {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CounterpartyRepository counterpartyRepository;

    public ValidationResult validateTradeBusinessRules(TradeDTO tradeDTO) {
        ValidationResult result = ValidationResult.success();

        if (tradeDTO.getTradeDate() != null) {
            // Validate Trade date is no more than 30 days in the past
            if (tradeDTO.getTradeDate().isBefore(LocalDate.now().minusDays(30))) {
                result.addError("Trade date cannot be more than 30 days in the past");
            }
        }

        // Validate start date is not before trade date
        if (tradeDTO.getTradeStartDate() != null && tradeDTO.getTradeDate() != null) {
            if (tradeDTO.getTradeStartDate().isBefore(tradeDTO.getTradeDate())) {
                result.addError("Start date cannot be before trade date");
            }
        }

        // Validate maturity date is not before trade date
        if (tradeDTO.getTradeMaturityDate() != null && tradeDTO.getTradeDate() != null) {
            if (tradeDTO.getTradeMaturityDate().isBefore(tradeDTO.getTradeDate())) {
                result.addError("Maturity date cannot be before trade date");
            }
        }

        // Validate maturity date is not before start date
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

        // Validate trade has exactly 2 legs
        if (tradeLegs.size() != 2) {
            result.addError("Trade must have exactly 2 legs");
            return result;
        }

        TradeLegDTO leg1 = tradeLegs.get(0);
        TradeLegDTO leg2 = tradeLegs.get(1);

        // Validate currency is set
        if (leg1.getCurrency() == null) {
            result.addError("Leg 1 currency must be set");
        }
        if (leg2.getCurrency() == null) {
            result.addError("Leg 2 currency must be set");
        }

        String leg1PayReceiveFlag = leg1.getPayReceiveFlag();
        String leg2PayReceiveFlag = leg2.getPayReceiveFlag();

        boolean leg1Missing = (leg1PayReceiveFlag == null);
        boolean leg2Missing = (leg2PayReceiveFlag == null);

        // Validate pay/receive flag is populated
        if (leg1Missing) {
            result.addError("Leg 1 pay/receive flag must be set");
        }
        if (leg2Missing) {
            result.addError("Leg 2 pay/receive flag must be set");
        }

        // Validate legs have opposite pay/receive flags
        if (!leg1Missing && !leg2Missing) {
            leg1PayReceiveFlag = leg1PayReceiveFlag.trim().toUpperCase();
            leg2PayReceiveFlag = leg2PayReceiveFlag.trim().toUpperCase();

            boolean isOpposite = (leg1PayReceiveFlag.equals("PAY") && leg2PayReceiveFlag.equals("RECEIVE")) ||
                    (leg1PayReceiveFlag.equals("RECEIVE") && leg2PayReceiveFlag.equals("PAY"));

            if (!isOpposite) {
                result.addError("Legs must have opposite pay/receive flags");
            }
        }

        String leg1Type = leg1.getLegType();
        String leg2Type = leg2.getLegType();

        boolean leg1TypeMissing = (leg1Type == null);
        boolean leg2TypeMissing = (leg2Type == null);

        // Validate leg type is populated
        if (leg1TypeMissing) {
            result.addError("Leg 1 type must be set");
        }
        if (leg2TypeMissing) {
            result.addError("Leg 2 type must be set");
        }

        // Validate floating leg index is populated AND Fixed legs have a valid rate
        if (!leg1TypeMissing && !leg2TypeMissing) {

            for (TradeLegDTO leg : tradeLegs) {
                String legType = leg.getLegType().trim().toUpperCase();

                if (legType.equals("FLOATING")) {
                    if (leg.getIndexName() == null) {
                        result.addError("Floating legs must have an index specified");
                    }
                } else if (legType.equals("FIXED")) {
                    if (leg.getRate() == null || leg.getRate() < 0) {
                        result.addError("Fixed legs must have a valid rate (>= 0)");
                    }
                }
            }
        }
        return result;
    }

    public ValidationResult confirmReferenceDataIsActive(TradeDTO tradeDTO) {
        ValidationResult result = ValidationResult.success();

        // Validate trader user is active in the system
        String traderName = tradeDTO.getTraderUserName();
        if (traderName == null) {
            result.addError("Trader must be set");
        } else {
            Optional<ApplicationUser> optTrader = applicationUserRepository.findByFirstNameIgnoreCase(traderName);
            if (optTrader.isEmpty()) {
                result.addError("Trader must exist in the system");
            } else if (!optTrader.get().isActive()) {
                result.addError("Trader must be active in the system");
            }
        }

        // Validate book is active in the system
        String bookName = tradeDTO.getBookName();
        if (bookName == null) {
            result.addError("Book must be set");
        } else {
            Optional<Book> optBook = bookRepository.findByBookName(bookName);
            if (optBook.isEmpty()) {
                result.addError("Book must exist in the system");
            } else if (!optBook.get().isActive()) {
                result.addError("Book must be active in the system");
            }
        }

        // Validate counterparty is active in the system
        String counterpartyName = tradeDTO.getCounterpartyName();
        if (counterpartyName == null) {
            result.addError("Counterparty must be set");
        } else {
            Optional<Counterparty> optCounterparty = counterpartyRepository.findByName(counterpartyName);
            if (optCounterparty.isEmpty()) {
                result.addError("Counterparty must exist in the system");
            } else if (!optCounterparty.get().isActive()) {
                result.addError("Counterparty must be active in the system");
            }
        }

        return result;
    }

    public ValidationResult validateTradeDTOReferenceData(TradeDTO tradeDTO) {
        ValidationResult result = ValidationResult.success();

        // Validate reference data is populated
        if (tradeDTO.getInputterUserName() == null) {
            result.addError("Inputter must be set");
        }

        if (tradeDTO.getTradeType() == null) {
            result.addError("Trade type must be set");
        }

        if (tradeDTO.getTradeSubType() == null) {
            result.addError("Trade subtype must be set");
        }

        return result;
    }
}
