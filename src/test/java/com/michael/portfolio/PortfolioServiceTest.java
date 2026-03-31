package com.michael.portfolio;

import com.michael.portfolio.dto.PortfolioDTO;
import com.michael.portfolio.dto.TradeDTO;
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

    private Portfolio root;
    private Portfolio child;
    private PortfolioDTO rootDTO;
    private PortfolioDTO childDTO;

    @BeforeEach
    void setUp(){
        //Arrange
        rootDTO = new PortfolioDTO(1L, "Root Portfolio");
        root = PortfolioMapper.toEntity(rootDTO);


        childDTO = new PortfolioDTO(1L, "Child");
        child = PortfolioMapper.toEntity(childDTO);
        child.setParent(root);

        List<Portfolio> children = new ArrayList<>();
        children.add(child);
        root.setChildren(children);
    }

    @Test
    void testCreatePortfolio(){
        //Act
        PortfolioDTO saved = portfolioService.createPortfolio(rootDTO);

        //Assert
        Optional<Portfolio> found = portfolioRepository.findPortfolioById(saved.id());
        assertTrue(found.isPresent());
        assertEquals("Root Portfolio", found.get().getName());
        assertEquals(1, found.get().getChildren().size());


    }

    @Test
    void testAddChildPortfolio(){
        //Act
        PortfolioDTO saved = portfolioService.createPortfolio(rootDTO);
        PortfolioDTO child2 = new PortfolioDTO(2L, "Child2");
        portfolioService.addChildPortfolio(child2, saved.id());

        //Assert
        Optional<Portfolio> found = portfolioRepository.findPortfolioById(saved.id());
        assertTrue(found.isPresent());
        assertEquals(2, found.get().getChildren().size());
        assertEquals("Child2", found.get().getChildren().get(1).getName());


    }

    @Test
    void shouldRejectPortfolioBeyondDepthLimit() {

        Portfolio current = root;
        // build depth = 5
        for (int i = 1; i <= 5; i++) {
            Portfolio child = new Portfolio();
            child.setName("Child " + i);
            child.setParent(current);
            current.setChildren(new ArrayList<>(List.of(child)));
            current = child;
        }

        Portfolio tooDeep = new Portfolio();
        tooDeep.setName("Too Deep");

        PortfolioDTO tooDeepDTO = PortfolioMapper.toDTO(tooDeep);

        Portfolio finalCurrent = current;
        assertThrows(IllegalArgumentException.class, () -> {
            portfolioService.addChildPortfolio(tooDeepDTO, finalCurrent.getId()); // finalCurrent is depth 5
        });


    }

    @Test
    void testDeletePortfolio() {
        //Act
        Portfolio saved = PortfolioMapper.toEntity(portfolioService.createPortfolio(rootDTO));
        Long childId = child.getId();
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
    void testRejectPortfolioWithoutName() {
        Portfolio unnamed = new Portfolio();
        assertThrows(IllegalArgumentException.class, () -> {
            portfolioService.createPortfolio(PortfolioMapper.toDTO(unnamed));
        });
    }

    @Test
    void testRejectCircularRelation() {
        PortfolioDTO saved = portfolioService.createPortfolio(rootDTO);
        // Trying to set root as its own child
        assertThrows(IllegalArgumentException.class, () -> {
            portfolioService.addChildPortfolio(rootDTO, root.getId());
        });
    }


}
