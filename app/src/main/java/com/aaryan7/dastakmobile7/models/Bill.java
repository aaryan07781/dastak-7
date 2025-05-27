package com.aaryan7.dastakmobile7.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model class for Bill
 */
public class Bill {
    private long id;
    private Date date;
    private double total;
    private double discount;
    private double finalAmount;
    private List<BillItem> items;

    public Bill() {
        this.date = new Date();
        this.items = new ArrayList<>();
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

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
        calculateFinalAmount();
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public void setItems(List<BillItem> items) {
        this.items = items;
        calculateTotal();
    }

    /**
     * Add an item to the bill
     * @param item BillItem to add
     */
    public void addItem(BillItem item) {
        this.items.add(item);
        calculateTotal();
    }

    /**
     * Remove an item from the bill
     * @param item BillItem to remove
     */
    public void removeItem(BillItem item) {
        this.items.remove(item);
        calculateTotal();
    }

    /**
     * Calculate total amount based on items
     */
    private void calculateTotal() {
        double total = 0;
        for (BillItem item : items) {
            total += item.getSubtotal();
        }
        this.total = total;
        calculateFinalAmount();
    }

    /**
     * Calculate final amount after discount
     */
    private void calculateFinalAmount() {
        this.finalAmount = this.total - this.discount;
    }
}
