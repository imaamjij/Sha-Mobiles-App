package com.example.shamobiles.models;

public class AccessorySale {
    private String managerId;
    private String item;
    private int quantity;
    private double price;
    private long timestamp;

    public AccessorySale() {}

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
    
    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
} 