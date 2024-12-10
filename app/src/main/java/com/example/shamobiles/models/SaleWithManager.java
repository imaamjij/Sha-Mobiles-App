package com.example.shamobiles.models;

public class SaleWithManager extends AccessorySale {
    private String managerName;
    private String id;

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
} 