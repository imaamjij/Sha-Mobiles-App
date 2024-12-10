package com.example.shamobiles.models;

public class Service {
    private String mechanicId;
    private String customerName;
    private String mobileModel;
    private String fault;
    private double price;
    private long timestamp;

    public Service() {}

    // Getters
    public String getMechanicId() { return mechanicId; }
    public String getCustomerName() { return customerName; }
    public String getMobileModel() { return mobileModel; }
    public String getFault() { return fault; }
    public double getPrice() { return price; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setMechanicId(String mechanicId) { this.mechanicId = mechanicId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setMobileModel(String mobileModel) { this.mobileModel = mobileModel; }
    public void setFault(String fault) { this.fault = fault; }
    public void setPrice(double price) { this.price = price; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
} 