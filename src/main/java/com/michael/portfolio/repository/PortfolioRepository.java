package com.michael.portfolio.repository;

import com.michael.portfolio.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findPortfolioById(Long id);
    List<Portfolio> findByParentId(Long id);

}
