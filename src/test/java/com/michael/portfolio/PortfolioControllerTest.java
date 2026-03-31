package com.michael.portfolio;


import com.michael.portfolio.controller.PortfolioController;
import com.michael.portfolio.dto.PortfolioDTO;
import com.michael.portfolio.dto.TradeDTO;
import com.michael.portfolio.model.Portfolio;

import com.michael.portfolio.model.Trade;
import com.michael.portfolio.repository.PortfolioRepository;
import com.michael.portfolio.repository.TradeRepository;
import com.michael.portfolio.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @MockitoBean
    private TradeRepository tradeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Portfolio parent;

    @Test
    void testSayHello() throws Exception {
        mockMvc.perform(get("/portfolios/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World"));
    }

    @Test
    void testGetPortfolioEndpoint() throws Exception {
        PortfolioDTO portfolioDTO = new PortfolioDTO(1L, "Root Portfolio");

        // Mock service behavior
        when(portfolioService.findPortfolioById(1L)).thenReturn(Optional.of(portfolioDTO));

        mockMvc.perform(get("/portfolios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Root Portfolio"));
    }

    @Test
    void testCreatePortfolioEndpoint() throws Exception {
        PortfolioDTO portfolioDTO = new PortfolioDTO(1L, "Root Portfolio");

        // Mock service behavior
        when(portfolioService.createPortfolio(portfolioDTO)).thenReturn(portfolioDTO);

        mockMvc.perform(post("/portfolios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(portfolioDTO)))
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
        trade.setTicker("AAPL");

        // Mock service behavior
        doNothing().when(portfolioService).addTradeToPortfolio(eq(1L), any(TradeDTO.class));

        mockMvc.perform(post("/portfolios/1/trade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productType").value("Warrant"));
        verify(portfolioService, times(1)).addTradeToPortfolio(eq(1L), any(TradeDTO.class));
    }

    @Test
    void testAddPortfolioToPortfolio() throws Exception {

        PortfolioDTO portfolioDTO = new PortfolioDTO(1L, "Root Portfolio");

        PortfolioDTO childPortfolioTO = new PortfolioDTO(2L, "Child Portfolio");


        // Mock service behavior
        doNothing().when(portfolioService).addChildPortfolio(childPortfolioTO, portfolioDTO.id());

        mockMvc.perform(post("/portfolios/1/child")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(childPortfolioTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Child Portfolio"));
        verify(portfolioService, times(1)).addChildPortfolio(any(PortfolioDTO.class), eq(1L));
    }

    @Test
    void testUpdatePortfolio() throws Exception {

        parent = portfolioRepository.save(parent);
        PortfolioDTO updatedDTO = new PortfolioDTO(1L, "Updated Portfolio");


        when(portfolioService.updatePortfolio(eq(1L), any(PortfolioDTO.class)))
                .thenReturn(updatedDTO);

        mockMvc.perform(put("/portfolios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Portfolio"));

    }

    @Test
    void testGetTrades() throws Exception {
        TradeDTO tradeDTO = new TradeDTO(1L, "Warrant", 10, 100.00, "W-100");

        // Mock service behavior
        when(portfolioService.getTradesByPortfolio(1L)).thenReturn(List.of(tradeDTO));

        mockMvc.perform(get("/portfolios/1/trades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productType").value("Warrant"))
                .andExpect(jsonPath("$[0].quantity").value(10))
                .andExpect(jsonPath("$[0].price").value(100.00));
    }
}
