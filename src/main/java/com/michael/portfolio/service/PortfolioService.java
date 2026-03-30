package com.michael.portfolio.service;

import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.model.Trade;
import com.michael.portfolio.repository.PortfolioRepository;
import com.michael.portfolio.repository.TradeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final TradeRepository tradeRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, TradeRepository tradeRepository) {
        this.portfolioRepository = portfolioRepository;
        this.tradeRepository = tradeRepository;
    }

    public Portfolio createPortfolio(Portfolio portfolio) {
        if(portfolio.getName() == null){
            throw new IllegalArgumentException("Name cannot be empty");
        }
        return portfolioRepository.save(portfolio);
    }

    public void deletePortfolio(Long id) {
        portfolioRepository.deletePortfolioById(id);
    }

    public void addChildPortfolio(Portfolio child, Long parentId) {
        if (parentId == null) {
            throw new IllegalArgumentException("Parent portfolio ID must not be null");
        }
        Portfolio parent = portfolioRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found"));

        if (hasCircularRelation(parent, child)) {
            throw new IllegalArgumentException("Circular relation detected");
        }
        int depth = calculateDepth(parent);
        if(depth >= 5){
            throw new IllegalArgumentException("Portfolio depth cannot exceed 5");
        }
        if (parent.getChildren() == null) {
            parent.setChildren(new ArrayList<>());
        }
        child.setParent(parent);
        parent.getChildren().add(child);
        portfolioRepository.save(parent);

    }

    private int calculateDepth(Portfolio portfolio){
        int count = 0;
        Portfolio current = portfolio;
        while(current.getParent() != null){
            count++;
            current = current.getParent();
        }
        return count;

    }

    private boolean hasCircularRelation(Portfolio parent, Portfolio child) {
        Portfolio current = parent;
        while (current != null) {
            if (current.equals(child)) {
                return true; // circular detected
            }
            current = current.getParent();
        }
        return false;
    }

    @Transactional
    public Trade addTradeToPortfolio(long id, Trade trade) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));

        trade.setPortfolio(portfolio);

        // Synchronize the inverse side (important for bidirectional consistency)
        if (portfolio.getTrades() == null) {
            portfolio.setTrades(new ArrayList<>());
        }
        portfolio.getTrades().add(trade);

        // Save the trade (owning side) — cascade is NOT set on Portfolio.trades, so we save explicitly
        return tradeRepository.save(trade);

    }

    public List<Trade> getTradesByPortfolio(long id) {
        return tradeRepository.findTradesByPortfolioId(id);
    }


    @Transactional
    public Optional<Portfolio> findPortfolioById(long id) {
        return portfolioRepository.findPortfolioById(id);
    }

    public List<Portfolio> findAllChildPortfolio(long id){
        return portfolioRepository.findByParentId(id);
    }

    @Transactional
    public Portfolio updatePortfolio(long id, Portfolio updatedPortfolio) {
        Portfolio existing = portfolioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));

        existing.setName(updatedPortfolio.getName());
        return portfolioRepository.save(existing);
    }
}
