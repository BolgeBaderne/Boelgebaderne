package com.example.bolgebaderne.controller;

public class EventAdminRequest {

    private String saunagusMasterName;
    private String saunagusMasterImageUrl;
    private String description;
    private int durationMinutes;
    private int capacity;
    private double price;

    public String getSaunagusMasterName() {
        return saunagusMasterName;
    }

    public void setSaunagusMasterName(String saunagusMasterName) {
        this.saunagusMasterName = saunagusMasterName;
    }

    public String getSaunagusMasterImageUrl() {
        return saunagusMasterImageUrl;
    }

    public void setSaunagusMasterImageUrl(String saunagusMasterImageUrl) {
        this.saunagusMasterImageUrl = saunagusMasterImageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
