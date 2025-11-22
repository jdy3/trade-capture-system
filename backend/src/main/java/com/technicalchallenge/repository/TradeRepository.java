package com.technicalchallenge.repository;

import com.technicalchallenge.model.Trade;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long>, JpaSpecificationExecutor<Trade> {
    // Existing methods
    List<Trade> findByTradeId(Long tradeId);

    @Query("SELECT MAX(t.tradeId) FROM Trade t")
    Optional<Long> findMaxTradeId();

    @Query("SELECT MAX(t.version) FROM Trade t WHERE t.tradeId = :tradeId")
    Optional<Integer> findMaxVersionByTradeId(@Param("tradeId") Long tradeId);

    // NEW METHODS for service layer compatibility
    Optional<Trade> findByTradeIdAndActiveTrue(Long tradeId);

    List<Trade> findByActiveTrueOrderByTradeIdDesc();

    @Query("SELECT t FROM Trade t WHERE t.tradeId = :tradeId AND t.active = true ORDER BY t.version DESC")
    Optional<Trade> findLatestActiveVersionByTradeId(@Param("tradeId") Long tradeId);

    //ENHANCEMENT-1: MULTI-CRITERIA SEARCH METHODS
    List<Trade> findByCounterparty_Name(String name); 

    List<Trade> findByTraderUser_LoginId(String loginId);

    List<Trade> findByTradeDateBetween(LocalDate fromDate, LocalDate toDate);

    @Query("SELECT t FROM Trade t WHERE t.book.bookName = :bookName")
    List<Trade> findByBookName(@Param("bookName") String bookName);

    @Query("SELECT t FROM Trade t WHERE t.tradeStatus.tradeStatus = :tradeStatus")
    List<Trade> findByTradeStatus(@Param("tradeStatus") String tradeStatus);

    //ENHANCEMENT-3: AUTHENTICATED USER SEES ONLY THEIR RELEVANT TRADES
    List<Trade> findByTraderUser_LoginIdIgnoreCase(String loginId);
}
