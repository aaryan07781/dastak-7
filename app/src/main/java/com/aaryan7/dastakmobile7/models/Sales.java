package com.aaryan7.dastakmobile7.models;

import java.util.Date;

/**
 * Model class for Sales data
 */
public class Sales {
    private long id;
    private Date date;
    private double amount;
    private double profit;

    public Sales() {
        // Default constructor
    }

    public Sales(Date date, double amount, double profit) {
        this.date = date;
        this.amount = amount;
        this.profit = profit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }
}
