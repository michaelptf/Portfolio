package com.michael.portfolio.dto;

import jakarta.validation.constraints.NotBlank;

public record PortfolioDTO(
        Long id,
        @NotBlank(message = "Portfolio name is required")
        String name
) {}
