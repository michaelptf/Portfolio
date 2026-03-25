package com.michael.portfolio;


import com.michael.portfolio.controller.PortfolioController;
import com.michael.portfolio.model.Portfolio;

import com.michael.portfolio.model.Trade;
import com.michael.portfolio.repository.PortfolioRepository;
import com.michael.portfolio.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PortfolioService portfolioService;

    @MockitoBean
    private PortfolioRepository portfolioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSayHello() throws Exception {
        mockMvc.perform(get("/portfolios/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World"));
    }

    @Test
    void testGetPortfolioEndpoint() throws Exception {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Root Portfolio");

        // Mock service behavior
        when(portfolioService.findPortfolioById(1L)).thenReturn(Optional.of(portfolio));

        mockMvc.perform(get("/portfolios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Root Portfolio"));
    }

    @Test
    void testCreatePortfolioEndpoint() throws Exception {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Root Portfolio");

        // Mock service behavior
        when(portfolioService.createPortfolio(portfolio)).thenReturn(portfolio);

        mockMvc.perform(post("/portfolios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolio)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Root Portfolio"));
    }

    @Test
    void testDeletePortfolio() throws Exception {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Root Portfolio");
        // Mock service behavior
        doNothing().when(portfolioService).deletePortfolio(portfolio.getId());

        mockMvc.perform(delete("/portfolios/1"))
                .andExpect(status().isNoContent());

        verify(portfolioService, times(1)).deletePortfolio(1L);
    }

    @Test
    void testAddTradeToPortfolio() throws Exception {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Root Portfolio");

        Trade trade = new Trade();
        trade.setId(1L);
        trade.setProductType("Warrant");
        trade.setQuantity(10);
        trade.setPrice(100.00);

        // Mock service behavior
        doNothing().when(portfolioService).addTradeToPortfolio(portfolio.getId(), trade);

        mockMvc.perform(post("/portfolios/1/trade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productType").value("Warrant"));
        verify(portfolioService, times(1)).addTradeToPortfolio(eq(1L), any(Trade.class));
    }

    @Test
    void testAddPortfolioToPortfolio() throws Exception {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(1L);
        portfolio.setName("Root Portfolio");

        Portfolio childPortfolio = new Portfolio();
        childPortfolio.setId(2L);
        childPortfolio.setName("Child Portfolio");


        // Mock service behavior
        doNothing().when(portfolioService).addChildPortfolio(childPortfolio, portfolio.getId());

        mockMvc.perform(post("/portfolios/1/child")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(childPortfolio)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Child Portfolio"));
        verify(portfolioService, times(1)).addChildPortfolio(any(Portfolio.class), eq(1L));
    }
}
