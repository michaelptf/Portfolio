package com.michael.portfolio.controller;
import com.michael.portfolio.dto.PortfolioDTO;
import com.michael.portfolio.dto.TradeDTO;
import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.model.Trade;
import com.michael.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/portfolios")
public class PortfolioController {
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World";
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDTO> getPortfolioById(@PathVariable long id){
        return portfolioService.findPortfolioById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PortfolioDTO> createPortfolio(@Valid @RequestBody PortfolioDTO portfolioDTO) {
        PortfolioDTO saved = portfolioService.createPortfolio(portfolioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PortfolioDTO> deletePortfolio(@PathVariable long id){
        portfolioService.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/trade")
    public ResponseEntity<TradeDTO> addTrade(@PathVariable long id, @RequestBody TradeDTO tradeDTO){
        TradeDTO saved = portfolioService.addTradeToPortfolio(id, tradeDTO);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/child")
    public ResponseEntity<PortfolioDTO> addChildPortfolio(
            @PathVariable("id") Long parentId,
            @Valid @RequestBody PortfolioDTO childDTO) {

        PortfolioDTO savedChild = portfolioService.addChildPortfolio(childDTO, parentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedChild);
    }

    @GetMapping("/{id}/all")
    public ResponseEntity<List<PortfolioDTO>> getAllChildPortfolios(@PathVariable long id) {
        List<PortfolioDTO> childList = portfolioService.findAllChildPortfolio(id);
        return ResponseEntity.ok(childList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PortfolioDTO> updatePortfolio(@PathVariable long id,
                                                     @RequestBody PortfolioDTO portfolioDTO) {
        PortfolioDTO updated = portfolioService.updatePortfolio(id, portfolioDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/trades")
    public ResponseEntity<List<TradeDTO>> getTrades(@PathVariable long id) {
        List<TradeDTO> tradeList = portfolioService.getTradesByPortfolio(id);
        return ResponseEntity.ok(tradeList);
    }
}

