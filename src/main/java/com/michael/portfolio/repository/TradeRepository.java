package com.michael.portfolio.repository;

import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findTradesByPortfolioId(Long id);
    Optional<Trade> findTradeById(long id);
}
