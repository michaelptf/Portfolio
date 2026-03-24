package com.michael.portfolio;

import com.michael.portfolio.model.Portfolio;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PortfolioApplicationTests {

    @Test
    void shouldSetAndGetName() {
        // Arrange
        Portfolio portfolio = new Portfolio();
        String expectedName = "My Stocks";

        // Act
        portfolio.setName(expectedName);

        // Assert
        assertEquals(expectedName, portfolio.getName());
    }

}
