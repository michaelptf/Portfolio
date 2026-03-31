package com.michael.portfolio.service;

import com.michael.portfolio.dto.PortfolioDTO;
import com.michael.portfolio.dto.TradeDTO;
import com.michael.portfolio.exception.ResourceNotFoundException;
import com.michael.portfolio.mapper.PortfolioMapper;
import com.michael.portfolio.mapper.TradeMapper;
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

    public PortfolioDTO createPortfolio(PortfolioDTO portfolioDTO) {
        Portfolio entity = PortfolioMapper.toEntity(portfolioDTO);
        Portfolio saved = portfolioRepository.save(entity);
        return PortfolioMapper.toDTO(saved);
    }

    public void deletePortfolio(Long id) {
        portfolioRepository.deletePortfolioById(id);
    }

    public PortfolioDTO addChildPortfolio(PortfolioDTO childDTO, Long parentId) {
        Portfolio parent = portfolioRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent portfolio not found with ID: " + parentId));

        Portfolio child = PortfolioMapper.toEntity(childDTO);

        if (hasCircularRelation(parent, child)) {
            throw new BusinessRuleException("Circular relation detected");
        }
        if (calculateDepth(parent) >= 5) {
            throw new BusinessRuleException("Portfolio hierarchy too deep. Maximum depth is 5.");
        }

        child.setParent(parent);
        parent.getChildren().add(child);

        Portfolio savedChild = portfolioRepository.save(child);
        return PortfolioMapper.toDTO(savedChild);

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
    public TradeDTO addTradeToPortfolio(long id, TradeDTO tradeDTO) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio not found"));

        Trade trade = TradeMapper.toEntity(tradeDTO);

        trade.setPortfolio(portfolio);

        // Synchronize the inverse side (important for bidirectional consistency)
        if (portfolio.getTrades() == null) {
            portfolio.setTrades(new ArrayList<>());
        }
        portfolio.getTrades().add(trade);

        Trade saved = tradeRepository.save(trade);

        return TradeMapper.toDTO(saved);

    }

    public List<TradeDTO> getTradesByPortfolio(long id) {
        return tradeRepository.findTradesByPortfolioId(id)
                .stream()
                .map(TradeMapper::toDTO)
                .toList();
    }



    @Transactional
    public Optional<PortfolioDTO> findPortfolioById(long id) {
        return portfolioRepository.findPortfolioById(id)
                .map(PortfolioMapper::toDTO);
    }

    public List<PortfolioDTO> findAllChildPortfolio(long id){

        return portfolioRepository.findByParentId(id)
                .stream()
                .map(PortfolioMapper::toDTO)
                .toList();
    }

    @Transactional
    public PortfolioDTO updatePortfolio(long id, PortfolioDTO updatedPortfolioDTO) {
        Portfolio existing = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio with ID " + id + " was not found"));

        existing.setName(updatedPortfolioDTO.name());

        Portfolio saved = portfolioRepository.save(existing);
        return PortfolioMapper.toDTO(saved);
    }
}
