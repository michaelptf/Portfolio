package com.michael.portfolio.service;

import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;

    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }
    public Portfolio createPortfolio(Portfolio root) {


        return portfolioRepository.save(root);
    }
}
