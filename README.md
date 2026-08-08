# 💳 Expense Manager

A Java Expense Tracker with two interfaces (console and desktop GUI), automatic
transaction import from Gmail, and persistent local storage — built with
Object-Oriented Programming principles.

## ✨ Features

- ➕ Add expenses with description, amount, category, and date
- ✏️ Update any existing expense
- 🗑️ Delete expenses by ID
- 📋 View all expenses in a formatted table
- 📊 Filter expenses by category
- 🔍 Live search bar (GUI) — filters the table as you type
- 💰 Calculate totals and averages
- 📑 Generate a summary report (total spend, category breakdown, largest expense)
- 📥 **Import from Gmail** — reads your Gmail inbox, finds transaction/payment
  emails, and automatically adds them as expenses using the Gmail API
- 💾 **Persistent storage** — all expenses save to `expenses.csv` and reload
  automatically on the next run
- 🖥️ Two ways to run it: a text menu in the console, or a Swing desktop window
- 🎨 User-friendly interface with emojis and formatting

## 🛠️ Tech Stack

- Java 17
- Object-Oriented Programming (encapsulation, single-responsibility classes)
- Java Swing (GUI)
- Google Gmail API + OAuth 2.0
- Java Collections (ArrayList) & Streams
- Maven

## 📂 Project Structure

```
src/main/java/com/expense/
├── Expense.java              # Data model for one expense
├── ExpenseManager.java       # Core CRUD logic + report generation
├── ExpenseStorage.java       # Reads/writes expenses.csv
├── ExpenseManagerApp.java    # Console (text menu) entry point
└── ExpenseManagerGUI.java    # Swing GUI entry point

src/main/java/com/expensetracker/gmail/
├── Expense.java              # Data model for one Gmail-parsed transaction
├── GmailAuth.java            # OAuth 2.0 login + Gmail service setup
├── ExpenseParser.java        # Extracts amount/merchant/category from emails
└── GmailExpenseImporter.java # Searches Gmail and fetches matching emails
```

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven
- A Google Cloud project with the Gmail API enabled (only needed for the
  Gmail import feature — everything else works without it)

### Setup

1. Clone the repository
   ```
   git clone https://github.com/shah1786/Expense-Manager-.git
   cd Expense-Manager-
   ```
2. (Gmail import only) Download your OAuth credentials from Google Cloud
   Console, rename the file to `credentials.json`, and place it in
   `src/main/resources/credentials.json`. This file is git-ignored and
   never uploaded — see `.gitignore`.

### Running the console version

Run `ExpenseManagerApp.java` as a Java application. You'll see a numbered
menu:

```
1. Add Expense
2. View All Expenses
3. View by Category
4. Show Totals
5. Update Expense
6. Delete Expense
7. Generate Report
8. Import from Gmail
9. Exit
```

### Running the GUI version

Run `ExpenseManagerGUI.java` as a Java application. A window opens with a
search bar, a table of your expenses, and buttons for Add, Update, Delete,
Totals, Report, and Import from Gmail.

### Data storage

All expenses are saved to `expenses.csv` in the project folder after every
change, and reloaded automatically the next time you start either version.
This file is git-ignored by default so your personal spending data doesn't
get uploaded — remove it from `.gitignore` if you want to track it.

## 🔒 Security Note

Never commit `credentials.json` — it contains your Google API client secret.
The included `.gitignore` excludes it, along with your saved login token
(`tokens/`) and your personal expense data (`expenses.csv`).

## 🔭 Future Improvements

- Migrate storage from CSV to a proper database (e.g. SQLite)
- Expand automatic categorization to recognize more merchants
- Export reports to PDF or Excel
