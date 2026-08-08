package com.expensetracker.gmail;

import java.time.LocalDate;

/**
 * Simple data holder for one imported transaction.
 * Replace this with your project's existing Expense class if you already have one.
 */
public class Expense {

    private String merchant;
    private double amount;
    private LocalDate date;
    private String category;
    private String rawSnippet;

    public Expense(String merchant, double amount, LocalDate date, String category, String rawSnippet) {
        this.merchant = merchant;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.rawSnippet = rawSnippet;
    }

    public String getMerchant() {
        return merchant;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public String getRawSnippet() {
        return rawSnippet;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | Rs. %.2f | %s", date, merchant, amount, category);
    }
}
