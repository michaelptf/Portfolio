package com.michael.portfolio.service;

import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.model.Trade;
import com.michael.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Port;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
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


    public void addTradeToPortfolio(Long id, Trade trade) {
//        if (trade.getQuantity() < 0) {
//            throw new IllegalArgumentException("Quantity cannot be negative");
//        }
//        if (trade.getPrice() < 0) {
//            throw new IllegalArgumentException("Price cannot be negative");
//        }
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));
        trade.setPortfolio(portfolio);
        portfolio.getTrades().add(trade);
        portfolioRepository.save(portfolio);

    }

    public List<Trade> getTradesByPortfolio(Long id) {
        Optional<Portfolio> portfolio = portfolioRepository.findPortfolioById(id);
        if(portfolio.isEmpty()){
            throw new IllegalArgumentException("Portfolio Id doesn't exist");
        }
        return portfolio.get().getTrades();
    }

    public Optional<Portfolio> findPortfolioById(long id) {
        return portfolioRepository.findPortfolioById(id);
    }
}
