package com.michael.portfolio.mapper;

import com.michael.portfolio.dto.PortfolioDTO;
import com.michael.portfolio.model.Portfolio;
import org.springframework.stereotype.Component;

@Component
public class PortfolioMapper {

    // Converts Entity -> DTO
    public static PortfolioDTO toDTO(Portfolio portfolio) {
        return new PortfolioDTO(
                portfolio.getId(),
                portfolio.getName()
        );
    }

    // Converts DTO -> Entity (Useful for Create/Update)
    public static Portfolio toEntity(PortfolioDTO dto) {
        Portfolio portfolio = new Portfolio();
        portfolio.setName(dto.name());
        return portfolio;
    }
}
