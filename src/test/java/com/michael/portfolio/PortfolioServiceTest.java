package com.michael.portfolio;

import com.michael.portfolio.dto.PortfolioDTO;
import com.michael.portfolio.dto.TradeDTO;
import com.michael.portfolio.exception.BusinessRuleException;
import com.michael.portfolio.mapper.PortfolioMapper;
import com.michael.portfolio.mapper.TradeMapper;
import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.model.Trade;
import com.michael.portfolio.repository.PortfolioRepository;
import com.michael.portfolio.service.PortfolioService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PortfolioServiceTest {

    @Autowired
    private PortfolioService portfolioService;
    @Autowired
    private PortfolioRepository portfolioRepository;

    private PortfolioDTO rootDTO;

    @BeforeEach
    void setUp(){
        //Arrange
        rootDTO = new PortfolioDTO(null, "Root Portfolio");
    }

    @Test
    void testCreatePortfolio() {
        // Act
        PortfolioDTO saved = portfolioService.createPortfolio(rootDTO);

        // Assert
        Optional<Portfolio> found = portfolioRepository.findById(saved.id());
        assertTrue(found.isPresent());
        assertEquals("Root Portfolio", found.get().getName());
        // It's 0 because we just created a single portfolio without children
        assertEquals(0, found.get().getChildren().size());
    }

    @Test
    void testAddChildPortfolio(){
        // Arrange
        PortfolioDTO parent = portfolioService.createPortfolio(rootDTO);
        PortfolioDTO childDTO = new PortfolioDTO(null, "Child1");
        // Act
        portfolioService.addChildPortfolio(childDTO, parent.id());

        // Assert
        Optional<Portfolio> found = portfolioRepository.findById(parent.id());
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getChildren().size());
        assertEquals("Child1", found.get().getChildren().get(0).getName());
    }

    @Test
    void shouldRejectPortfolioBeyondDepthLimit() {

        //Arrange: Create a chain 5 levels deep
        PortfolioDTO current = portfolioService.createPortfolio(rootDTO);


        for (int i = 1; i <= 5; i++) {
            PortfolioDTO child = portfolioService.createPortfolio(new PortfolioDTO(null, "Level "+ i));
            current = portfolioService.addChildPortfolio(child, current.id());
        }

        //Act: Try to add the 6th level
        PortfolioDTO level6 = portfolioService.createPortfolio(new PortfolioDTO(null, "Too Deep"));


        //Assert
        PortfolioDTO finalCurrent = current;
        assertThrows(BusinessRuleException.class, () -> {
            portfolioService.addChildPortfolio(level6, finalCurrent.id());
        });


    }

    @Test
    void testDeletePortfolio() {
        //Act
        Portfolio saved = PortfolioMapper.toEntity(portfolioService.createPortfolio(rootDTO));
        PortfolioDTO childDTO = new PortfolioDTO(null, "Child1");
        Long childId = childDTO.id();
        portfolioRepository.save(saved);
        portfolioService.deletePortfolio(saved.getId());


        // Assert
        Optional<Portfolio> foundParent = portfolioRepository.findPortfolioById(saved.getId());
        Optional<Portfolio> foundChild = portfolioRepository.findPortfolioById(childId);
        assertFalse(foundParent.isPresent()); // parent gone
        assertFalse(foundChild.isPresent());  // child gone too

    }

    @Test
    void testAddTradeToPortfolio() {
        // Arrange
        PortfolioDTO saved = portfolioService.createPortfolio(rootDTO);
        Trade trade = new Trade();
        trade.setProductType("ETF");
        trade.setQuantity(100);
        trade.setPrice(50.0);

        // Act
        portfolioService.addTradeToPortfolio(saved.id(), TradeMapper.toDTO(trade));

        // Assert
        Optional<Portfolio> found = portfolioRepository.findPortfolioById(saved.id());
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getTrades().size());
        assertEquals("ETF", found.get().getTrades().get(0).getProductType());
    }

    @Test
    void testFetchTradesByPortfolio() {
        // Arrange
        PortfolioDTO saved = portfolioService.createPortfolio(rootDTO);
        Trade trade1 = new Trade("ETF", 100, 50.0);
        Trade trade2 = new Trade("Bond", 200, 100.0);
        portfolioService.addTradeToPortfolio(saved.id(), TradeMapper.toDTO(trade1));
        portfolioService.addTradeToPortfolio(saved.id(), TradeMapper.toDTO(trade2));

        // Act
        List<TradeDTO> trades = portfolioService.getTradesByPortfolio(saved.id());

        // Assert
        assertEquals(2, trades.size());
        assertEquals("ETF", trades.get(0).productType());
        assertEquals("Bond", trades.get(1).productType());
    }

    @Test
    void testRejectNegativeTradeQuantity() {
        PortfolioDTO saved = portfolioService.createPortfolio(rootDTO);
        assertThrows(IllegalArgumentException.class, () -> {
            Trade invalidTrade = new Trade("ETF", -10, 50.0);
            portfolioService.addTradeToPortfolio(saved.id(), TradeMapper.toDTO(invalidTrade));
        });
    }


    @Test
    void testRejectCircularRelation() {
        PortfolioDTO saved = portfolioService.createPortfolio(rootDTO);
        // Trying to set root as its own child
        assertThrows(BusinessRuleException.class, () -> {
            portfolioService.addChildPortfolio(saved, saved.id());
        });
    }


}
