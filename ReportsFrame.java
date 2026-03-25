import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReportsFrame extends JFrame {

    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh, btnBack;

    public ReportsFrame() {
        setTitle("Smart Hostel Mess System - Monthly Reports");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitle = new JLabel("Monthly Reports & Summary", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Table
        String[] columns = {"Month", "Total Students", "Total Meals Served", "Total Revenue (₹)", "Total Fines (₹)", "Avg Attendance (%)"};
        tableModel = new DefaultTableModel(columns, 0);
        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(35);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        btnRefresh = new JButton("Generate / Refresh Report");
        btnBack = new JButton("Back to Dashboard");

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        loadReportData();

        btnRefresh.addActionListener(e -> loadReportData());
        btnBack.addActionListener(e -> {
            dispose();
            new DashboardFrame();
        });

        setVisible(true);
    }

    private void loadReportData() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT TO_CHAR(b.bill_month, 'YYYY-MM') as month, " +
                     "COUNT(DISTINCT b.student_id) as total_students, " +
                     "SUM(b.total_meals) as total_meals, " +
                     "SUM(b.amount) as revenue, " +
                     "SUM(b.fines) as fines, " +
                     "ROUND(AVG(b.total_meals)*100/90, 2) as avg_attendance " +  // assuming 90 meals per month
                     "FROM Bills b GROUP BY TO_CHAR(b.bill_month, 'YYYY-MM') ORDER BY month DESC")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("month"),
                    rs.getInt("total_students"),
                    rs.getInt("total_meals"),
                    rs.getDouble("revenue"),
                    rs.getDouble("fines"),
                    rs.getDouble("avg_attendance")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new ReportsFrame();
    }
}
