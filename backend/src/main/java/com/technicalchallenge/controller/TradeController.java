package com.technicalchallenge.controller;

import com.technicalchallenge.dto.TradeDTO;
import com.technicalchallenge.mapper.TradeMapper;
import com.technicalchallenge.model.Trade;
import com.technicalchallenge.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/trades")
@Validated
@Tag(name = "Trades", description = "Trade management operations including booking, searching, and lifecycle management")
public class TradeController {
    private static final Logger logger = LoggerFactory.getLogger(TradeController.class);

    @Autowired
    private TradeService tradeService;
    @Autowired
    private TradeMapper tradeMapper;

    @GetMapping
    @Operation(summary = "Get all trades",
               description = "Retrieves a list of all trades in the system. Returns comprehensive trade information including legs and cashflows.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all trades",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public List<TradeDTO> getAllTrades() {
        logger.info("Fetching all trades");
        return tradeService.getAllTrades().stream()
                .map(tradeMapper::toDto)
                .toList();
    }

   //ENHANCEMENT-1: MULTI-CRITERIA SEARCH METHODS
    @GetMapping("/search/counterparty/{name}")
    @Operation(summary = "Searches trades by counterparty",
               description = "Retrieves trades by counterparty")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trades found and returned successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trades not found"),
        @ApiResponse(responseCode = "400", description = "Invalid counterparty format")
    })
    public List<TradeDTO> searchTradesByCounterpartyName(
            @Parameter(description = "Trade counterparty", required = true)
            @PathVariable(name = "name") String name) {
        logger.debug("Fetching trades by counterparty: {}", name);
        return tradeService.searchTradesByCounterpartyName(name).stream()
                .map(tradeMapper::toDto)
                .toList();
    }

    @GetMapping("/search/book/{bookName}")
    @Operation(summary = "Search trades by book",
               description = "Retrieves trades by book")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trades found and returned successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trades not found"),
        @ApiResponse(responseCode = "400", description = "Invalid book format")
    })
    public List<TradeDTO> searchTradesByBookName(
            @Parameter(description = "Trade book", required = true)
            @PathVariable(name = "bookName") String bookName) {
        logger.debug("Fetching trades by bookName: {}", bookName);
        return tradeService.searchTradesByBookName(bookName).stream()
                .map(tradeMapper::toDto)
                .toList();
    }

    @GetMapping("/search/trader/{loginId}")
    @Operation(summary = "Search trades by trader",
               description = "Retrieves trades by trader")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trades found and returned successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trades not found"),
        @ApiResponse(responseCode = "400", description = "Invalid trader format")
    })
    public List<TradeDTO> searchTradesByTraderLoginId(
            @Parameter(description = "Trader login ID", required = true)
            @PathVariable(name = "loginId") String loginId) {
        logger.debug("Fetching trades by traderLoginId: {}", loginId);
        return tradeService.searchTradesByTraderLoginId(loginId).stream()
                .map(tradeMapper::toDto)
                .toList();
    }

    @GetMapping("/search/status/{tradeStatus}")
    @Operation(summary = "Search trades by status",
               description = "Retrieves trades by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trades found and returned successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trades not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status format")
    })
    public List<TradeDTO> searchTradesByStatus(
            @Parameter(description = "Trade status", required = true)
            @PathVariable(name = "tradeStatus") String tradeStatus) {
        logger.debug("Fetching trades by tradeStatus: {}", tradeStatus);
        return tradeService.searchTradesByStatus(tradeStatus).stream()
                .map(tradeMapper::toDto)
                .toList();
    }

    @GetMapping("/search/trade-date-between/{tradeDateFrom}/{tradeDateTo}")
    @Operation(summary = "Search trades by trade date between",
               description = "Retrieves trades by trade date between")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trades found and returned successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trades not found"),
        @ApiResponse(responseCode = "400", description = "Invalid trade date between format")
    })
    public List<TradeDTO> searchTradesByDateBetween(
            @Parameter(description = "Trade date from (yyyy-MM-dd)", required = true)
            @PathVariable(name = "tradeDateFrom") LocalDate tradeDateFrom,
            @Parameter(description = "Trade date to (yyyy-MM-dd)", required = true)
            @PathVariable(name = "tradeDateTo") LocalDate tradeDateTo) {
        logger.debug("Fetching trades by trade date between: {} and {}", tradeDateFrom, tradeDateTo);
        return tradeService.searchTradesByDateBetween(tradeDateFrom, tradeDateTo).stream()
                .map(tradeMapper::toDto)
                .toList();
    }

    // ENHANCEMENT-1: PAGINATED FILTERING METHODS 
    @GetMapping("/filter")
    @Operation(summary = "Get all trades by page",
              description = "Retrieves trades with pagination.")
     @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all trades by page",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
     })
     public ResponseEntity<Page<TradeDTO>> getAllTrades(
        @Parameter(description = "Page number (zero-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "10")
        @RequestParam(defaultValue = "10") int size) {
        logger.debug("Retrieving all trades - page: {}, size: {}", page, size);
        Page<Trade> trades = tradeService.getAllTrades(page, size);
        return ResponseEntity.ok(trades.map(tradeMapper::toDto));
     }
             
    @GetMapping("/filter/search")
    @Operation(summary = "Get trades filtered by multi-criteria search parameters, by page",
              description = "Retrieves trades filtered by multi-criteria search parameters, by page. Supports pagination with 'page', 'size', and 'sort' query parameters.")
     @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered trades by page",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
     })
     public ResponseEntity<Page<TradeDTO>> filterTrades(
        @RequestParam(required = false) String counterpartyName, 
        @RequestParam(required = false) String bookName, 
        @RequestParam(required = false) String loginId, 
        @RequestParam(required = false) String tradeStatus, 
        @RequestParam(required = false) LocalDate tradeDateFrom,
        @RequestParam(required = false) LocalDate tradeDateTo,
        @Parameter(hidden = true)
        Pageable pageable) {
        logger.debug("Retrieving filtered trades by page");
        Page<Trade> trades = tradeService.filterTrades(counterpartyName, bookName, loginId, tradeStatus, tradeDateFrom, tradeDateTo, pageable);
        return ResponseEntity.ok(trades.map(tradeMapper::toDto));
     }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get trade by ID",
               description = "Retrieves a specific trade by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trade found and returned successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trade not found"),
        @ApiResponse(responseCode = "400", description = "Invalid trade ID format")
    })
    public ResponseEntity<TradeDTO> getTradeById(
            @Parameter(description = "Unique identifier of the trade", required = true)
            @PathVariable(name = "id") Long id) {
        logger.debug("Fetching trade by id: {}", id);
        return tradeService.getTradeById(id)
                .map(tradeMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new trade",
               description = "Creates a new trade with the provided details. Automatically generates cashflows and validates business rules.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Trade created successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid trade data or business rule violation"),
        @ApiResponse(responseCode = "500", description = "Internal server error during trade creation")
    })
    public ResponseEntity<?> createTrade(
            @Parameter(description = "Trade details for creation", required = true)
            @Valid @RequestBody TradeDTO tradeDTO) {
        logger.info("Creating new trade: {}", tradeDTO);
        try {
            Trade trade = tradeMapper.toEntity(tradeDTO);
            tradeService.populateReferenceDataByName(trade, tradeDTO);
            Trade savedTrade = tradeService.saveTrade(trade, tradeDTO);
            TradeDTO responseDTO = tradeMapper.toDto(savedTrade);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            logger.error("Error creating trade: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error creating trade: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing trade",
               description = "Updates an existing trade with new information. Subject to business rule validation and user privileges.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trade updated successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trade not found"),
        @ApiResponse(responseCode = "400", description = "Invalid trade data or business rule violation"),
        @ApiResponse(responseCode = "403", description = "Insufficient privileges to update trade")
    })
    public ResponseEntity<?> updateTrade(
            @Parameter(description = "Unique identifier of the trade to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated trade details", required = true)
            @Valid @RequestBody TradeDTO tradeDTO) {
        logger.info("Updating trade with id: {}", id);
        try {
            // Validate IDs match instead of overriding tradeDTO tradeId with path id
            if (tradeDTO.getTradeId() != null && !tradeDTO.getTradeId().equals(id)) {
                return ResponseEntity.badRequest().body("Trade ID in path must match Trade ID in request body");
            }
            tradeDTO.setTradeId(id); // Set if null
            Trade amendedTrade = tradeService.amendTrade(id, tradeDTO);
            TradeDTO responseDTO = tradeMapper.toDto(amendedTrade);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error updating trade: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error updating trade: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete trade",
               description = "Deletes an existing trade. This is a soft delete that changes the trade status.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trade deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Trade not found"),
        @ApiResponse(responseCode = "400", description = "Trade cannot be deleted in current status"),
        @ApiResponse(responseCode = "403", description = "Insufficient privileges to delete trade")
    })
    public ResponseEntity<?> deleteTrade(
            @Parameter(description = "Unique identifier of the trade to delete", required = true)
            @PathVariable Long id) {
        logger.info("Deleting trade with id: {}", id);
        try {
            tradeService.deleteTrade(id);
            return ResponseEntity.ok().body("Trade cancelled successfully");
        } catch (Exception e) {
            logger.error("Error deleting trade: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error deleting trade: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/terminate")
    @Operation(summary = "Terminate trade",
               description = "Terminates an existing trade before its natural maturity date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trade terminated successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trade not found"),
        @ApiResponse(responseCode = "400", description = "Trade cannot be terminated in current status"),
        @ApiResponse(responseCode = "403", description = "Insufficient privileges to terminate trade")
    })
    public ResponseEntity<?> terminateTrade(
            @Parameter(description = "Unique identifier of the trade to terminate", required = true)
            @PathVariable Long id) {
        logger.info("Terminating trade with id: {}", id);
        try {
            Trade terminatedTrade = tradeService.terminateTrade(id);
            TradeDTO responseDTO = tradeMapper.toDto(terminatedTrade);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error terminating trade: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error terminating trade: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel trade",
               description = "Cancels an existing trade by changing its status to cancelled")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trade cancelled successfully",
                    content = @Content(mediaType = "application/json",
                                     schema = @Schema(implementation = TradeDTO.class))),
        @ApiResponse(responseCode = "404", description = "Trade not found"),
        @ApiResponse(responseCode = "400", description = "Trade cannot be cancelled in current status"),
        @ApiResponse(responseCode = "403", description = "Insufficient privileges to cancel trade")
    })
    public ResponseEntity<?> cancelTrade(
            @Parameter(description = "Unique identifier of the trade to cancel", required = true)
            @PathVariable Long id) {
        logger.info("Cancelling trade with id: {}", id);
        try {
            Trade cancelledTrade = tradeService.cancelTrade(id);
            TradeDTO responseDTO = tradeMapper.toDto(cancelledTrade);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            logger.error("Error cancelling trade: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Error cancelling trade: " + e.getMessage());
        }
    }
}
