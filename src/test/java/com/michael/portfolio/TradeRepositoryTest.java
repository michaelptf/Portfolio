package com.michael.portfolio;

import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.model.Trade;
import com.michael.portfolio.repository.PortfolioRepository;
import com.michael.portfolio.repository.TradeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TradeRepositoryTest {

    @Autowired
    private TradeRepository tradeRepository;
    @Autowired
    private PortfolioRepository portfolioRepository;


    @Test
    void testFindByPortfolioId(){
        //Arrange
        Portfolio root = new Portfolio();
        root.setName("Root Portfolio");

        Trade child = new Trade();
        child.setProductType("ETF");
        child.setQuantity(100);
        child.setPrice(50.0);
        child.setPortfolio(root);

        List<Trade> tradeList = new ArrayList<>();
        tradeList.add(child);
        root.setTrades(tradeList);

        //Act
        portfolioRepository.save(root);

        //Assert
        List<Trade> found = tradeRepository.findTradesByPortfolioId(root.getId());
        assertEquals(1, found.size());
        assertEquals("ETF", found.get(0).getProductType());


    }

    @Test
    void testFindTradeById(){
        //Arrange
        Portfolio root = new Portfolio();
        root.setName("Root Portfolio");

        Trade child = new Trade();
        child.setProductType("ETF");
        child.setQuantity(100);
        child.setPrice(50.0);
        child.setPortfolio(root);

        List<Trade> tradeList = new ArrayList<>();
        tradeList.add(child);
        root.setTrades(tradeList);

        //Act
        Portfolio savedPortfolio = portfolioRepository.save(root);
        Long tradeId = savedPortfolio.getTrades().get(0).getId();

        //Assert
        Optional<Trade> found = tradeRepository.findTradeById(tradeId);
        assertEquals(tradeId, found.get().getId());

    }
}
