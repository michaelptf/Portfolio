package com.michael.portfolio.controller;
import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.model.Trade;
import com.michael.portfolio.repository.PortfolioRepository;
import com.michael.portfolio.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sound.sampled.Port;
import java.util.Optional;

@RestController
@RequestMapping("/portfolios")
public class PortfolioController {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioRepository portfolioRepository, PortfolioService portfolioService) {
        this.portfolioRepository = portfolioRepository;
        this.portfolioService = portfolioService;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable long id){
        Optional<Portfolio> portfolio = portfolioService.findPortfolioById(id);
        return portfolioService.findPortfolioById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody Portfolio portfolio) {
        portfolioService.createPortfolio(portfolio);
        return ResponseEntity.ok(portfolio);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Portfolio> deletePortfolio(@PathVariable long id){
        portfolioService.deletePortfolio(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/trade")
    public ResponseEntity<Trade> addTrade(@PathVariable long id, @RequestBody Trade trade){
        portfolioService.addTradeToPortfolio(id, trade);
        return ResponseEntity.ok(trade);
    }

    @PostMapping("/{id}/child")
    public ResponseEntity<Portfolio> addChildPortfolio(@PathVariable long id, @RequestBody Portfolio childPortfolio){
        portfolioService.addChildPortfolio(childPortfolio, id);
        return ResponseEntity.ok(childPortfolio);
    }
}

