package com.michael.portfolio;
import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.repository.PortfolioRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PortfolioRepositoryTest {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Test
    void testFindPortfolio() {
        //Arrange
        Portfolio root = new Portfolio();
        root.setName("Root Portfolio");

        //Act
        Portfolio saved = portfolioRepository.save(root);

        //Assert
        Optional<Portfolio> found = portfolioRepository.findPortfolioById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Root Portfolio", found.get().getName());
    }

    @Test
    void testGetChildPortfolio(){
        //Arrange
        Portfolio root = new Portfolio();
        ArrayList<Portfolio> childList = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            Portfolio child = new Portfolio();
            child.setName("Child " + i);
            child.setParent(root);
            childList.add(child);
        }
        root.setName("Root Portfolio");
        root.setChildren(childList);

        //Act
        Portfolio saved = portfolioRepository.save(root);

        //Assert
        List<Portfolio> children = portfolioRepository.findByParentId(saved.getId());

        assertEquals(5, children.size());
        assertTrue(children.stream().anyMatch(c -> "Child 1".equals(c.getName())));
    }
}
