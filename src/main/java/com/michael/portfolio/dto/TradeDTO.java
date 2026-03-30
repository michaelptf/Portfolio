package com.michael.portfolio.dto;

public record TradeDTO(
        Long id,
        String productType,
        int quantity,
        double price,
        String ticker
) {}
