package com.example.kitchen_assistant_backend.model;

public class ConversionResponse {
    private String status;
    private double result;
    private String unit;
    private String message;

    // --- THIS IS THE MISSING CONSTRUCTOR ---
    // Add this block to your file.
    public ConversionResponse(String status, double result, String unit, String message) {
        this.status = status;
        this.result = result;
        this.unit = unit;
        this.message = message;
    }

    // Default constructor (it's good practice to have this too)
    public ConversionResponse() {
    }

    // --- Getters and Setters ---
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getResult() { return result; }
    public void setResult(double result) { this.result = result; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}