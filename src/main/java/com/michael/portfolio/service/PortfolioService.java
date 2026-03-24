package com.michael.portfolio.service;

import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }
    public Portfolio createPortfolio(Portfolio portfolio) {


        return portfolioRepository.save(portfolio);
    }

    public void addChildPortfolio(Portfolio child, Portfolio parent) {
        child.setParent(parent);
        parent.getChildren().add(child);
        portfolioRepository.save(parent);

    }
}
