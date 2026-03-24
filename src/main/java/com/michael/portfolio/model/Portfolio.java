package com.michael.portfolio.model;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Portfolio parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Portfolio> children = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "portfolio_id")
    private List<Trade> trades = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Portfolio getParent() {
        return parent;
    }

    public void setParent(Portfolio parent) {
        this.parent = parent;
    }

    public List<Portfolio> getChildren() {
        return children;
    }

    public void setChildren(List<Portfolio> children) {
        this.children = children;
    }

    public List<Trade> getTrades() {
        return trades;
    }

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }

}
