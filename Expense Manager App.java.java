package com.expense;

import java.util.Scanner;

/**
 * Main application class for Expense Manager
 * @author Expense Manager
 */
public class ExpenseManagerApp {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpenseManager manager = new ExpenseManager();
        
        System.out.println("🎉 Welcome to Expense Manager!");
        System.out.println("=".repeat(50));
        
        int choice;
        do {
            try {
                manager.displayMenu();
                choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1 -> manager.addExpense(scanner);
                    case 2 -> manager.viewAllExpenses();
                    case 3 -> manager.viewExpensesByCategory(scanner);
                    case 4 -> manager.showTotalExpenses();
                    case 5 -> manager.deleteExpense(scanner);
                    case 6 -> {
                        System.out.println("\n👋 Thank you for using Expense Manager!");
                        System.out.println("💾 Your data is saved in memory. Goodbye!");
                        choice = 6;
                    }
                    default -> System.out.println("❌ Invalid option! Please enter 1-6.");
                }
                
                if (choice != 6) {
                    System.out.print("\n⏳ Press Enter to continue...");
                    scanner.nextLine();
                    System.out.println();
                }
                
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number!");
                scanner.nextLine(); // clear input
            }
            
        } while (choice != 6);
        
        scanner.close();
    }
}