import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BillingFrame extends JFrame {

    private JTable billTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbMonth;
    private JButton btnGenerateBill, btnRefresh, btnBack;

    public BillingFrame() {
        setTitle("Smart Hostel Mess System - Billing & Fines");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel lblTitle = new JLabel("Billing & Fines Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Month Selector
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Month:"));
        cmbMonth = new JComboBox<>();
        // Add last 6 months
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        for (int i = 0; i < 6; i++) {
            cmbMonth.addItem(sdf.format(cal.getTime()));
            cal.add(Calendar.MONTH, -1);
        }
        topPanel.add(cmbMonth);
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Bill ID", "Student Name", "Month", "Total Meals", "Amount (₹)", "Fines (₹)", "Total Amount (₹)"};
        tableModel = new DefaultTableModel(columns, 0);
        billTable = new JTable(tableModel);
        billTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(billTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        btnGenerateBill = new JButton("Generate Monthly Bill for All Students");
        btnRefresh = new JButton("Refresh");
        btnBack = new JButton("Back to Dashboard");

        buttonPanel.add(btnGenerateBill);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load bills on start
        loadBillsFromDB();

        // Button Actions
        btnGenerateBill.addActionListener(e -> generateMonthlyBill());
        btnRefresh.addActionListener(e -> loadBillsFromDB());
        btnBack.addActionListener(e -> {
            dispose();
            new DashboardFrame();
        });

        setVisible(true);
    }

    private void loadBillsFromDB() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT b.bill_id, s.name, b.bill_month, b.total_meals, b.amount, b.fines, b.total_amount " +
                     "FROM Bills b JOIN Students s ON b.student_id = s.student_id ORDER BY b.bill_month DESC")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("bill_id"),
                    rs.getString("name"),
                    rs.getString("bill_month").substring(0, 7),
                    rs.getInt("total_meals"),
                    rs.getDouble("amount"),
                    rs.getDouble("fines"),
                    rs.getDouble("total_amount")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void generateMonthlyBill() {
        String selectedMonth = (String) cmbMonth.getSelectedItem(); // e.g., "2026-03"

        int confirm = JOptionPane.showConfirmDialog(this,
                "Generate bills for month " + selectedMonth + " for ALL students?\nThis will calculate meals and fines automatically.",
                "Confirm Bill Generation", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection()) {
            // Get all students
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT student_id FROM Students");

            while (rs.next()) {
                int studentId = rs.getInt("student_id");

                // Count meals attended in the selected month
                PreparedStatement psCount = con.prepareStatement(
                        "SELECT COUNT(*) FROM Attendance WHERE student_id = ? AND TO_CHAR(attendance_date, 'yyyy-MM') = ? AND status = 'Present'");
                psCount.setInt(1, studentId);
                psCount.setString(2, selectedMonth);
                ResultSet rsCount = psCount.executeQuery();
                int totalMeals = rsCount.next() ? rsCount.getInt(1) : 0;

                double amount = totalMeals * 30.0;     // ₹30 per meal
                double fines = totalMeals < 60 ? 500.0 : 0.0;   // Example fine logic

                double totalAmount = amount + fines;

                // Insert or Update bill
                PreparedStatement psBill = con.prepareStatement(
                        "INSERT INTO Bills (student_id, bill_month, total_meals, amount, fines, total_amount) " +
                        "VALUES (?, TO_DATE(?,'YYYY-MM'), ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE total_meals=?, amount=?, fines=?, total_amount=?");

                psBill.setInt(1, studentId);
                psBill.setString(2, selectedMonth);
                psBill.setInt(3, totalMeals);
                psBill.setDouble(4, amount);
                psBill.setDouble(5, fines);
                psBill.setDouble(6, totalAmount);

                // For update part
                psBill.setInt(7, totalMeals);
                psBill.setDouble(8, amount);
                psBill.setDouble(9, fines);
                psBill.setDouble(10, totalAmount);

                psBill.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Monthly Bills Generated Successfully for " + selectedMonth + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBillsFromDB();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating bills: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new BillingFrame();
    }
}
