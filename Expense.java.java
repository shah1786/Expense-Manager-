package com.expense;

/**
 * Represents an individual expense entry
 * @author Expense Manager
 */
public class Expense {
    private int id;
    private String description;
    private double amount;
    private String category;
    private String date;
    
    /**
     * Constructor for Expense
     */
    public Expense(int id, String description, double amount, String category, String date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    @Override
    public String toString() {
        return String.format("ID: %d | %-20s | $%8.2f | %-12s | %s", 
                           id, description, amount, category, date);
    }
}