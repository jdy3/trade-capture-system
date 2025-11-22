// package com.technicalchallenge.service;

// import com.technicalchallenge.dto.TradeDTO;
// import com.technicalchallenge.dto.TradeLegDTO;
// import com.technicalchallenge.model.Book;
// import com.technicalchallenge.model.Cashflow;
// import com.technicalchallenge.model.Counterparty;
// import com.technicalchallenge.model.LegType;
// import com.technicalchallenge.model.Schedule;
// import com.technicalchallenge.model.Trade;
// import com.technicalchallenge.model.TradeLeg;
// import com.technicalchallenge.model.TradeStatus;
// import com.technicalchallenge.repository.BookRepository;
// import com.technicalchallenge.repository.CashflowRepository;
// import com.technicalchallenge.repository.CounterpartyRepository;
// import com.technicalchallenge.repository.LegTypeRepository;
// import com.technicalchallenge.repository.ScheduleRepository;
// import com.technicalchallenge.repository.TradeLegRepository;
// import com.technicalchallenge.repository.TradeRepository;
// import com.technicalchallenge.repository.TradeStatusRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.math.BigDecimal;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class TradeServiceTest {

//     @Mock
//     private TradeRepository tradeRepository;

//     @Mock
//     private TradeLegRepository tradeLegRepository;

//     @Mock
//     private CashflowRepository cashflowRepository;

//     @Mock
//     private TradeStatusRepository tradeStatusRepository;

//     @Mock
//     private AdditionalInfoService additionalInfoService;

//     @Mock
//     private BookRepository bookRepository;

//     @Mock
//     private CounterpartyRepository counterpartyRepository;

//     @Mock
//     private LegTypeRepository legTypeRepository;

//     @Mock
//     private ScheduleRepository scheduleRepository;

//     @InjectMocks
//     private TradeService tradeService;

//     private TradeDTO tradeDTO;
//     private Trade trade;

//     @BeforeEach
//     void setUp() {
//         setUpTradeDTO();
//         setUpTradeEntity();       
//     }

//     void setUpTradeDTO() {
//         tradeDTO = new TradeDTO();
//         tradeDTO.setTradeId(100001L);
//         tradeDTO.setTradeDate(LocalDate.of(2025, 1, 15));
//         tradeDTO.setTradeStartDate(LocalDate.of(2025, 1, 17));
//         tradeDTO.setTradeMaturityDate(LocalDate.of(2026, 1, 17));
//         tradeDTO.setBookName("TestBook");
//         tradeDTO.setCounterpartyName("TestCounterparty");
//         tradeDTO.setTradeStatus("TestStatus");

//         TradeLegDTO leg1 = new TradeLegDTO();
//         leg1.setNotional(BigDecimal.valueOf(1000000));
//         leg1.setRate(0.05);
//         leg1.setCalculationPeriodSchedule("1M");
//         leg1.setLegType("Fixed"); // ADD THIS

//         TradeLegDTO leg2 = new TradeLegDTO();
//         leg2.setNotional(BigDecimal.valueOf(1000000));
//         leg2.setRate(0.0);
//         leg2.setCalculationPeriodSchedule("1M");
//         leg2.setLegType("Floating"); // ADD THIS

//         tradeDTO.setTradeLegs(Arrays.asList(leg1, leg2));
//     }

//    void setUpTradeEntity() {
//     trade = new Trade();
//     trade.setId(1L);
//     trade.setTradeId(100001L);
    
//     // Add date fields to match the DTO
//     trade.setTradeDate(LocalDate.of(2025, 1, 15));
//     trade.setTradeStartDate(LocalDate.of(2025, 1, 17));
//     trade.setTradeMaturityDate(LocalDate.of(2026, 1, 17));
    
//     // Add audit fields
//     trade.setActive(true);
//     trade.setCreatedDate(LocalDateTime.now());
//     trade.setVersion(1);
    
//     // Create that match the DTOs
//     List<TradeLeg> tradeLegs = new ArrayList<>();
    
//     TradeLeg leg1 = new TradeLeg();
//     leg1.setLegId(1L);
//     leg1.setNotional(BigDecimal.valueOf(1000000));
//     leg1.setRate(0.05);
//     leg1.setTrade(trade);
    
//     Schedule monthlySchedule = new Schedule();
//     monthlySchedule.setSchedule("1M");
//     leg1.setCalculationPeriodSchedule(monthlySchedule);
    
//     tradeLegs.add(leg1);
    
//     TradeLeg leg2 = new TradeLeg();
//     leg2.setLegId(2L);
//     leg2.setNotional(BigDecimal.valueOf(1000000)); 
//     leg2.setRate(0.0);
//     leg2.setTrade(trade);
    
//     leg2.setCalculationPeriodSchedule(monthlySchedule);
    
//     tradeLegs.add(leg2);
    
//     // Set the legs on the trade
//     trade.setTradeLegs(tradeLegs);
// }

//     void setUpTradeCreationMocks() {
//     Book mockBook = new Book();
//     mockBook.setId(1L);
//     mockBook.setBookName("TestBook");
    
//     Counterparty mockCounterparty = new Counterparty();
//     mockCounterparty.setId(1L);
//     mockCounterparty.setName("TestCounterparty");
    
//     TradeStatus mockStatus = new TradeStatus();
//     mockStatus.setId(1L);
//     mockStatus.setTradeStatus("TestStatus");
    
//     Schedule schedule = new Schedule();
//     schedule.setId(1L);
//     schedule.setSchedule("1M");
    
//     LegType fixedLegType = new LegType();
//     fixedLegType.setType("Fixed");
    
//     LegType floatingLegType = new LegType();
//     floatingLegType.setType("Floating");
    
//     // Mock repository calls 
//     when(bookRepository.findByBookName("TestBook")).thenReturn(Optional.of(mockBook));
//     when(counterpartyRepository.findByName("TestCounterparty")).thenReturn(Optional.of(mockCounterparty));
//     when(tradeStatusRepository.findByTradeStatus("TestStatus")).thenReturn(Optional.of(mockStatus));
//     when(scheduleRepository.findBySchedule("1M")).thenReturn(Optional.of(schedule));
//     when(legTypeRepository.findByType("Fixed")).thenReturn(Optional.of(fixedLegType));
//     when(legTypeRepository.findByType("Floating")).thenReturn(Optional.of(floatingLegType));
    
//     // Return the entities
//     when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> invocation.getArgument(0));
//     when(tradeLegRepository.save(any(TradeLeg.class))).thenAnswer(invocation -> invocation.getArgument(0));
// }

//     @Test
//     void testCreateTrade_Success() {
//         // Given
//         setUpTradeCreationMocks();

//         // When
//         Trade result = tradeService.createTrade(tradeDTO);

//         // Then
//         assertNotNull(result);
//         assertEquals(100001L, result.getTradeId());
//         verify(tradeRepository).save(any(Trade.class));
//     }

//     @Test
//     void testCreateTrade_InvalidDates_ShouldFail() {
//         tradeDTO.setTradeStartDate(LocalDate.of(2025, 1, 10)); // Before trade date

//         // When & Then
//         RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//             tradeService.createTrade(tradeDTO);
//         });

//         assertEquals("Start date cannot be before trade date", exception.getMessage()); // Update with correct error message for start date < trade date
//     }

//     @Test
//     void testCreateTrade_InvalidLegCount_ShouldFail() {
//         // Given
//         tradeDTO.setTradeLegs(Arrays.asList(new TradeLegDTO())); // Only 1 leg

//         // When & Then
//         RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//             tradeService.createTrade(tradeDTO);
//         });

//         assertTrue(exception.getMessage().contains("exactly 2 legs"));
//     }

//     @Test
//     void testGetTradeById_Found() {
//         // Given
//         when(tradeRepository.findByTradeIdAndActiveTrue(100001L)).thenReturn(Optional.of(trade));

//         // When
//         Optional<Trade> result = tradeService.getTradeById(100001L);

//         // Then
//         assertTrue(result.isPresent());
//         assertEquals(100001L, result.get().getTradeId());
//     }

//     @Test
//     void testGetTradeById_NotFound() {
//         // Given
//         when(tradeRepository.findByTradeIdAndActiveTrue(999L)).thenReturn(Optional.empty());

//         // When
//         Optional<Trade> result = tradeService.getTradeById(999L);

//         // Then
//         assertFalse(result.isPresent());
//     }

//     @Test
//     void testAmendTrade_Success() {
//         // Given
//         Long tradeId = 100001L;
//         TradeDTO amendedTradeDTO = new TradeDTO();
//         amendedTradeDTO.setTradeId(tradeId);
//         amendedTradeDTO.setBookName("TestBook");
//         amendedTradeDTO.setCounterpartyName("TestCounterparty");
        
//         // Add missing trade legs
//         TradeLegDTO leg1 = new TradeLegDTO();
//         leg1.setNotional(BigDecimal.valueOf(1000000));
//         leg1.setRate(0.05);
//         leg1.setCalculationPeriodSchedule("1M");
//         leg1.setLegType("Fixed");

//         TradeLegDTO leg2 = new TradeLegDTO();
//         leg2.setNotional(BigDecimal.valueOf(1000000));
//         leg2.setRate(0.0);
//         leg2.setCalculationPeriodSchedule("1M");
//         leg2.setLegType("Floating");

//         amendedTradeDTO.setTradeLegs(Arrays.asList(leg1, leg2));
        
//         // Mock AMENDED status that the service looks for
//         TradeStatus amendedStatus = new TradeStatus();
//         amendedStatus.setTradeStatus("AMENDED");
        
//         // Mock reference data for the amended trade
//         Book mockBook = new Book();
//         mockBook.setId(1L);
//         mockBook.setBookName("TestBook");
        
//         Counterparty mockCounterparty = new Counterparty();
//         mockCounterparty.setId(1L);
//         mockCounterparty.setName("TestCounterparty");
        
//         LegType fixedLegType = new LegType();
//         fixedLegType.setType("Fixed");
        
//         LegType floatingLegType = new LegType();
//         floatingLegType.setType("Floating");
        
//         Schedule schedule = new Schedule();
//         schedule.setSchedule("1M");
        
//         when(tradeRepository.findByTradeIdAndActiveTrue(tradeId)).thenReturn(Optional.of(trade));
//         when(tradeStatusRepository.findByTradeStatus("AMENDED")).thenReturn(Optional.of(amendedStatus));
//         when(bookRepository.findByBookName("TestBook")).thenReturn(Optional.of(mockBook));
//         when(counterpartyRepository.findByName("TestCounterparty")).thenReturn(Optional.of(mockCounterparty));
//         when(legTypeRepository.findByType("Fixed")).thenReturn(Optional.of(fixedLegType));
//         when(legTypeRepository.findByType("Floating")).thenReturn(Optional.of(floatingLegType));
//         when(scheduleRepository.findBySchedule("1M")).thenReturn(Optional.of(schedule));
//         when(tradeRepository.save(any(Trade.class))).thenAnswer(invocation -> invocation.getArgument(0));
//         when(tradeLegRepository.save(any(TradeLeg.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         // When
//         Trade result = tradeService.amendTrade(tradeId, amendedTradeDTO);

//         // Then
//         assertNotNull(result);
//         verify(tradeRepository, times(2)).save(any(Trade.class));
//     }

//     @Test
//     void testAmendTrade_TradeNotFound() {
//         // Given
//         when(tradeRepository.findByTradeIdAndActiveTrue(999L)).thenReturn(Optional.empty());

//         // When & Then
//         RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//             tradeService.amendTrade(999L, tradeDTO);
//         });

//         assertTrue(exception.getMessage().contains("Trade not found"));
//     }

//     @Test
//     void testCashflowGeneration_MonthlySchedule() {
//         // Given
//         setUpTradeCreationMocks();

//         // When 
//          Trade result = tradeService.createTrade(tradeDTO);

//         // Then - Verify cashflows were generated
//         assertNotNull(result);
//         // Verify cashflow repository was called for both legs
//         verify(cashflowRepository, times(24)).save(any(Cashflow.class));
//     }
// }
