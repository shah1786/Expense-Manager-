package com.expense;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Manages the collection of expenses and provides CRUD operations
 * @author Expense Manager
 */
public class ExpenseManager {
    private List<Expense> expenses;
    private int nextId;
    
    public ExpenseManager() {
        expenses = new ArrayList<>();
        nextId = 1;
    }
    
    public void addExpense(Scanner scanner) {
        System.out.print("📝 Enter description: ");
        String description = scanner.nextLine();
        
        System.out.print("💰 Enter amount: $");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        
        System.out.print("🏷️  Enter category: ");
        String category = scanner.nextLine();
        
        System.out.print("📅 Enter date (MM/DD/YYYY): ");
        String date = scanner.nextLine();
        
        Expense expense = new Expense(nextId++, description, amount, category, date);
        expenses.add(expense);
        System.out.println("✅ Expense added successfully!");
    }
    
    public void viewAllExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("📭 No expenses recorded yet.");
            return;
        }
        
        System.out.println("\n📋 ALL EXPENSES:");
        System.out.println("=".repeat(70));
        for (Expense expense : expenses) {
            System.out.println(expense);
        }
        System.out.println("=".repeat(70));
    }
    
    public void viewExpensesByCategory(Scanner scanner) {
        System.out.print("🔍 Enter category to filter: ");
        String category = scanner.nextLine().toLowerCase();
        
        boolean found = false;
        System.out.println("\n📊 EXPENSES IN CATEGORY: " + category.toUpperCase());
        System.out.println("=".repeat(70));
        
        for (Expense expense : expenses) {
            if (expense.getCategory().toLowerCase().contains(category)) {
                System.out.println(expense);
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("❌ No expenses found in this category.");
        }
        System.out.println("=".repeat(70));
    }
    
    public void showTotalExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("📭 No expenses recorded yet.");
            return;
        }
        
        double total = expenses.stream()
                              .mapToDouble(Expense::getAmount)
                              .sum();
        
        System.out.printf("\n💰 TOTAL EXPENSES: $%,.2f\n", total);
        System.out.printf("📈 AVERAGE EXPENSE: $%,.2f\n", total / expenses.size());
    }
    
    public void deleteExpense(Scanner scanner) {
        System.out.print("🗑️  Enter expense ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        
        boolean removed = expenses.removeIf(expense -> expense.getId() == id);
        if (removed) {
            System.out.println("✅ Expense deleted successfully!");
        } else {
            System.out.println("❌ Expense ID not found!");
        }
    }
    
    public void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("💳 EXPENSE MANAGER v1.0 💳");
        System.out.println("=".repeat(50));
        System.out.println("1. ➕ Add Expense");
        System.out.println("2. 📋 View All Expenses");
        System.out.println("3. 📊 View by Category");
        System.out.println("4. 💰 Show Totals");
        System.out.println("5. 🗑️  Delete Expense");
        System.out.println("6. ❌ Exit");
        System.out.print("\n➤ Choose option (1-6): ");
    }
}