 Gmail Expense Importer

Reads your Gmail for bank transaction emails and converts them into Expense objects.

 Setup

1. Rename your downloaded OAuth file to `credentials.json`.
2. Create the folder `src/main/resources` in this project.
3. Put `credentials.json` inside `src/main/resources`.
4. Never commit `credentials.json` to GitHub. Add this line to `.gitignore`:
   ```
   credentials.json
   tokens/
   ```

Running in Eclipse

1. File > Import > Existing Maven Project. Select this folder.
2. Right-click `GmailExpenseImporter.java` > Run As > Java Application.
3. A browser window opens. Log in and approve access.
4. Check the console for imported expenses.

 Running in VS Code

1. Open this folder in VS Code with the Java Extension Pack installed.
2. Open `GmailExpenseImporter.java`.
3. Click Run above the `main` method.
4. Approve Gmail access in the browser window that opens.

## Matching your bank's emails

Open `GmailExpenseImporter.java` and update `GMAIL_SEARCH_QUERY` to match your bank's sender address, for example:

```java
private static final String GMAIL_SEARCH_QUERY = "from:alerts@yourbank.com newer_than:30d";
```

Open `ExpenseParser.java` and adjust `AMOUNT_PATTERN` and `MERCHANT_PATTERN` if your bank's email wording doesn't match. Paste a sample email into a chat with Claude to get the exact regex for your bank's format.

 Files

- `GmailAuth.java` — handles Google login and token storage
- `GmailExpenseImporter.java` — fetches emails and runs the import
- `ExpenseParser.java` — extracts amount, merchant, category from email text
- `Expense.java` — data model for one transaction (swap in your existing class if you have one)
