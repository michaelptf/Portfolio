package com.michael.portfolio.service;

import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import javax.sound.sampled.Port;
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
        int depth = calculateDepth(parent);
        if(depth >= 5){
            throw new IllegalArgumentException("Portfolio depth cannot exceed 5");
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
}
