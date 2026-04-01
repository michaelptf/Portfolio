package com.michael.portfolio.repository;

import com.michael.portfolio.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;


import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findPortfolioById(Long id);
    List<Portfolio> findByParentId(Long id);
    @Modifying
    void deletePortfolioById(Long id);

}
