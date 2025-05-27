package com.aaryan7.dastakmobile7.models;

/**
 * Model class for Product
 */
public class Product {
    private long id;
    private String name;
    private double purchasePrice;
    private double sellingPrice;
    private int quantity;
    private double profit;

    public Product() {
        // Default constructor
    }

    public Product(String name, double purchasePrice, double sellingPrice, int quantity) {
        this.name = name;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.quantity = quantity;
        calculateProfit();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
        calculateProfit();
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
        calculateProfit();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getProfit() {
        return profit;
    }

    /**
     * Calculate profit based on selling price and purchase price
     */
    private void calculateProfit() {
        this.profit = this.sellingPrice - this.purchasePrice;
    }

    /**
     * Decrease quantity when product is sold
     * @param soldQuantity quantity sold
     * @return true if successful, false if not enough stock
     */
    public boolean decreaseQuantity(int soldQuantity) {
        if (this.quantity >= soldQuantity) {
            this.quantity -= soldQuantity;
            return true;
        }
        return false;
    }

    /**
     * Increase quantity when product is added to stock
     * @param addedQuantity quantity added
     */
    public void increaseQuantity(int addedQuantity) {
        this.quantity += addedQuantity;
    }
}
