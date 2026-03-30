package com.michael.portfolio.mapper;

import com.michael.portfolio.dto.PortfolioDTO;
import com.michael.portfolio.dto.TradeDTO;
import com.michael.portfolio.model.Portfolio;
import com.michael.portfolio.model.Trade;
import org.springframework.stereotype.Component;

@Component
public class TradeMapper {
    // Converts Entity -> DTO
    public static TradeDTO toDTO(Trade trade) {
        return new TradeDTO(
                trade.getId(),
                trade.getProductType(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getTicker()
        );
    }

    // Converts DTO -> Entity (Useful for Create/Update)
    public static Trade toEntity(TradeDTO dto) {
        Trade trade = new Trade();
        trade.setPrice(dto.price());
        trade.setQuantity(dto.quantity());
        trade.setProductType(dto.productType());
        trade.setTicker(dto.ticker());
        return trade;
    }
}
