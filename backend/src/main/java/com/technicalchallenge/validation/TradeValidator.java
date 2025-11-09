package com.technicalchallenge.validation;

import com.technicalchallenge.dto.CashflowDTO;
import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.dto.TradeLegDTO;
import com.technicalchallenge.mapper.CashflowMapper;
import com.technicalchallenge.model.ApplicationUser;
import com.technicalchallenge.model.Book;
import com.technicalchallenge.model.Cashflow;
import com.technicalchallenge.model.Counterparty;
import com.technicalchallenge.repository.ApplicationUserRepository;
import com.technicalchallenge.repository.BookRepository;
import com.technicalchallenge.repository.CounterpartyRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TradeValidator {

    @Autowired
    private CashflowMapper cashflowMapper;
    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CounterpartyRepository counterpartyRepository;

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

    public ValidationResult confirmReferenceDataIsActive(TradeDTO tradeDTO) {
        ValidationResult result = ValidationResult.success();

        // Trader user must be active in the system
        String traderName = tradeDTO.getTraderUserName();
        if (traderName == null || traderName.trim().isEmpty()) {
            result.addError("Trader must be set");
            return result;
        }

        String[] traderNames = traderName.trim().split("\\s+", 2);
        String firstName = traderNames[0];
        String lastName = traderNames.length > 1 ? traderNames[1] : "";      
        
        Optional<ApplicationUser> optTrader = applicationUserRepository.findByFirstNameAndLastName(firstName, lastName);
        if (optTrader.isEmpty()) {
            result.addError("Trader must exist in the system");
            return result;
        }
        if (!optTrader.get().isActive()) {
            result.addError("Trader must be active in the system");
        }

        // Book must be active in the system
        String bookName = tradeDTO.getBookName();
        if (bookName == null || bookName.trim().isEmpty()) {
            result.addError("Book must be set");
            return result;
        }

        Optional<Book> optBook = bookRepository.findByBookName(bookName);
        if (optBook.isEmpty()) {
            result.addError("Book must exist in the system");
            return result;
        }
        if (!optBook.get().isActive()) {
            result.addError("Book must be active in the system");
        }

        // Counterparty must be active in the system
        String counterpartyName = tradeDTO.getCounterpartyName();
        if (counterpartyName == null || counterpartyName.trim().isEmpty()) {
            result.addError("Counterparty must be set");
            return result;
        }

        Optional<Counterparty> optCounterparty = counterpartyRepository.findByName(counterpartyName);
        if (optCounterparty.isEmpty()) {
            result.addError("Counterparty must exist in the system");
            return result;
        }
        if (!optCounterparty.get().isActive()) {
            result.addError("Counterparty must be active in the system");
        }
         return result;
    }

    public ValidationResult validateReferenceData(TradeDTO tradeDTO) {
        ValidationResult result = ValidationResult.success();


        return result;
    }
}


//  private void validateReferenceData(Trade trade) {
        
//         All reference data must exist and be valid

//         Check populateReferenceDataByName() -includes populateUserReferences() + populateTradeTypeReference()

//         Check populateLegReferenceData()

//         Validate essential reference data is populated
//         if (trade.getBook() == null) {
//             throw new RuntimeException("Book not found or not set");
//         }
//         if (trade.getCounterparty() == null) {
//             throw new RuntimeException("Counterparty not found or not set");
//         }
//         if (trade.getTradeStatus() == null) {
//             throw new RuntimeException("Trade status not found or not set");
//         }

//         logger.debug("Reference data validation passed for trade");
//     }