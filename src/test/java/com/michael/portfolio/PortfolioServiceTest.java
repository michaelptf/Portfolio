package com.michael.portfolio;

import com.michael.portfolio.model.Portfolio;
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

    @BeforeEach
    void setUp(){
        //Arrange
        root = new Portfolio();
        root.setName("Root Portfolio");

        child = new Portfolio();
        child.setName("Child");
        child.setParent(root);

        root.setChildren(List.of(child));
    }

    @Test
    void testCreatePortfolio(){
        //Act
        Portfolio saved = portfolioService.createPortfolio(root);

        //Assert
        Optional<Portfolio> found = portfolioRepository.findPortfolioById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Root Portfolio", found.get().getName());
        assertEquals(1, found.get().getChildren().size());


    }

    @Test
    void testAddChildPortfolio(){
        //Act
        Portfolio saved = portfolioService.createPortfolio(root);
        Portfolio child2 = new Portfolio();
        child2.setName("Child2");
        portfolioService.addChildPortfolio(child2, saved);

        //Assert
        Optional<Portfolio> found = portfolioRepository.findPortfolioById(saved.getId());
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

        Portfolio finalCurrent = current;
        assertThrows(IllegalArgumentException.class, () -> {
            portfolioService.addChildPortfolio(tooDeep, finalCurrent); // finalCurrent is depth 5
        });
    }

}
