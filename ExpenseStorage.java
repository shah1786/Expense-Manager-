package com.expense;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes the expense list to a CSV file so data survives
 * between runs. One line per expense: id,description,amount,category,date
 *
 * Descriptions/categories containing commas or quotes are wrapped in
 * quotes and escaped, following standard CSV rules.
 */
public class ExpenseStorage {

    private final String filePath;

    public ExpenseStorage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads expenses from the CSV file. Returns an empty list if the file
     * doesn't exist yet (e.g. first run) or is empty.
     */
    public List<Expense> load() {
        List<Expense> expenses = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return expenses;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // skip header row
                }
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = parseCsvLine(line);
                if (fields.length < 5) {
                    continue; // skip malformed rows instead of crashing
                }
                int id = Integer.parseInt(fields[0]);
                String description = fields[1];
                double amount = Double.parseDouble(fields[2]);
                String category = fields[3];
                String date = fields[4];
                expenses.add(new Expense(id, description, amount, category, date));
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("⚠️  Could not fully read saved data: " + e.getMessage());
        }

        return expenses;
    }

    /**
     * Overwrites the CSV file with the current expense list.
     */
    public void save(List<Expense> expenses) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("id,description,amount,category,date");
            writer.newLine();
            for (Expense e : expenses) {
                writer.write(String.join(",",
                        String.valueOf(e.getId()),
                        escapeCsv(e.getDescription()),
                        String.valueOf(e.getAmount()),
                        escapeCsv(e.getCategory()),
                        escapeCsv(e.getDate())
                ));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("⚠️  Could not save data: " + e.getMessage());
        }
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Minimal CSV line parser that understands quoted fields
     * (so descriptions containing commas load back correctly).
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}