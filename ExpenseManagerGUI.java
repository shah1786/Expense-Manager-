package com.expense;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Swing GUI front-end for ExpenseManager. Same add/update/delete/report/
 * Gmail-import logic as the console version, just with buttons and a table
 * instead of typed menu numbers.
 */
public class ExpenseManagerGUI extends JFrame {

    private final ExpenseManager manager;
    private final ExpenseTableModel tableModel;
    private final JTable table;
    private final TableRowSorter<ExpenseTableModel> sorter;

    public ExpenseManagerGUI() {
        super("💳 Expense Manager");
        manager = new ExpenseManager();
        tableModel = new ExpenseTableModel(manager);
        table = new JTable(tableModel);
        tableModel.bindTable(table);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        add(buildSearchPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     * A single search box that filters the table live as you type,
     * matching against description, category, and merchant/date text
     * across all columns.
     */
    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));

        JLabel label = new JLabel("🔍 Search: ");
        JTextField searchField = new JTextField();

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilter(); }

            private void applyFilter() {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    // Case-insensitive match across every column (ID, Description, Amount, Category, Date).
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
                }
            }
        });

        panel.add(label, BorderLayout.WEST);
        panel.add(searchField, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton addBtn = new JButton("➕ Add");
        JButton updateBtn = new JButton("✏️ Update");
        JButton deleteBtn = new JButton("🗑️ Delete");
        JButton reportBtn = new JButton("📑 Report");
        JButton importBtn = new JButton("📥 Import from Gmail");
        JButton totalsBtn = new JButton("💰 Totals");

        addBtn.addActionListener(e -> onAdd());
        updateBtn.addActionListener(e -> onUpdate());
        deleteBtn.addActionListener(e -> onDelete());
        reportBtn.addActionListener(e -> onReport());
        importBtn.addActionListener(e -> onImportFromGmail());
        totalsBtn.addActionListener(e -> onTotals());

        panel.add(addBtn);
        panel.add(updateBtn);
        panel.add(deleteBtn);
        panel.add(totalsBtn);
        panel.add(reportBtn);
        panel.add(importBtn);
        return panel;
    }

    private void onAdd() {
        JTextField descField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField dateField = new JTextField();

        Object[] fields = {
            "Description:", descField,
            "Amount:", amountField,
            "Category:", categoryField,
            "Date (MM/DD/YYYY):", dateField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Expense", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());
            manager.addExpense(descField.getText(), amount, categoryField.getText(), dateField.getText());
            tableModel.refresh();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a number.", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);

        Expense target = tableModel.getExpenseAt(modelRow);

        JTextField descField = new JTextField(target.getDescription());
        JTextField amountField = new JTextField(String.valueOf(target.getAmount()));
        JTextField categoryField = new JTextField(target.getCategory());
        JTextField dateField = new JTextField(target.getDate());

        Object[] fields = {
            "Description:", descField,
            "Amount:", amountField,
            "Category:", categoryField,
            "Date:", dateField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Update Expense #" + target.getId(), JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            target.setDescription(descField.getText());
            target.setAmount(Double.parseDouble(amountField.getText()));
            target.setCategory(categoryField.getText());
            target.setDate(dateField.getText());
            manager.save();
            tableModel.refresh();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Amount must be a number.", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(selectedRow);

        Expense target = tableModel.getExpenseAt(modelRow);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete \"" + target.getDescription() + "\"?", "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            manager.deleteExpenseById(target.getId());
            tableModel.refresh();
        }
    }

    private void onReport() {
        JTextArea textArea = new JTextArea(manager.getReportText());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Expense Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onTotals() {
        List<Expense> expenses = manager.getAllExpenses();
        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No expenses recorded yet.", "Totals", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        double average = total / expenses.size();
        JOptionPane.showMessageDialog(this,
                String.format("Total: $%,.2f%nAverage: $%,.2f", total, average),
                "Totals", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onImportFromGmail() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        // Runs off the GUI thread so the window doesn't freeze while the browser login happens.
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                manager.importFromGmail();
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                tableModel.refresh();
                JOptionPane.showMessageDialog(ExpenseManagerGUI.this, "Gmail import finished. Check the table.", "Import", JOptionPane.INFORMATION_MESSAGE);
            }
        }.execute();
    }

    /**
     * Bridges ExpenseManager's list into something JTable can display.
     */
    private static class ExpenseTableModel extends AbstractTableModel {
        private final ExpenseManager manager;
        private JTable boundTable;
        private final String[] columns = {"ID", "Description", "Amount", "Category", "Date"};

        ExpenseTableModel(ExpenseManager manager) {
            this.manager = manager;
        }

        /** Called once after the JTable is created, so refresh() can force a repaint on it. */
        void bindTable(JTable table) {
            this.boundTable = table;
        }

        void refresh() {
            fireTableDataChanged();
            if (boundTable != null) {
                boundTable.revalidate();
                boundTable.repaint();
            }
        }

        Expense getExpenseAt(int row) {
            return manager.getAllExpenses().get(row);
        }

        @Override
        public int getRowCount() {
            return manager.getAllExpenses().size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Expense e = manager.getAllExpenses().get(rowIndex);
            return switch (columnIndex) {
                case 0 -> e.getId();
                case 1 -> e.getDescription();
                case 2 -> String.format("$%,.2f", e.getAmount());
                case 3 -> e.getCategory();
                case 4 -> e.getDate();
                default -> "";
            };
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseManagerGUI().setVisible(true));
    }
}
