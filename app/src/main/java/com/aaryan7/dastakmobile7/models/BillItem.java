package com.aaryan7.dastakmobile7.models;

/**
 * Model class for Bill Item
 */
public class BillItem {
    private long id;
    private long billId;
    private long productId;
    private String productName;
    private int quantity;
    private double price;
    private double subtotal;

    public BillItem() {
        // Default constructor
    }

    public BillItem(long productId, String productName, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        calculateSubtotal();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        calculateSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Calculate subtotal based on quantity and price
     */
    private void calculateSubtotal() {
        this.subtotal = this.quantity * this.price;
    }
}
