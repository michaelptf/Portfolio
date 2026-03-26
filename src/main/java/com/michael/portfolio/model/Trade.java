package com.michael.portfolio.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "portfolio_id")
    @JsonIgnore
    private Portfolio portfolio;

    private String productType; // ETF, Warrant, DLC
    private int quantity;
    private double price;
    private String ticker;

    public Trade(String productType, int quantity, double price) {

        setProductType(productType);
        setQuantity(quantity);
        setPrice(price);
    }

    public Trade() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        this.id = id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        if (portfolio == null) {
            throw new IllegalArgumentException("portfolio cannot be null");
        }
        this.portfolio = portfolio;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        if (productType == null) {
            throw new IllegalArgumentException("productType cannot be null");
        }
        this.productType = productType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity cannot be negative number");
        }
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("price cannot be negative number");
        }
        this.price = price;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        if (ticker == null) {
            throw new IllegalArgumentException("ticker cannot be null");
        }
        this.ticker = ticker;
    }
}
