package com.michael.portfolio;


import com.michael.portfolio.controller.PortfolioController;
import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PortfolioService portfolioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSayHello() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World"));
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
}
