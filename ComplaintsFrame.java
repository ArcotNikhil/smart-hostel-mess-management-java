import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ComplaintsFrame extends JFrame {

    private JTable complaintTable;
    private DefaultTableModel tableModel;
    private JTextArea txtDescription;
    private JComboBox<String> cmbStudent;
    private JButton btnSubmit, btnResolve, btnRefresh, btnBack;

    public ComplaintsFrame() {
        setTitle("Smart Hostel Mess System - Complaints");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel lblTitle = new JLabel("Complaints Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Table
        String[] columns = {"Complaint ID", "Student Name", "Description", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        complaintTable = new JTable(tableModel);
        complaintTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(complaintTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form Panel (Bottom)
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Submit New Complaint / Resolve"));

        JPanel topForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topForm.add(new JLabel("Student:"));
        cmbStudent = new JComboBox<>();
        topForm.add(cmbStudent);

        formPanel.add(topForm, BorderLayout.NORTH);

        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        txtDescription = new JTextArea(3, 40);
        descPanel.add(new JScrollPane(txtDescription), BorderLayout.CENTER);
        formPanel.add(descPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        btnSubmit = new JButton("Submit Complaint");
        btnResolve = new JButton("Resolve Selected Complaint");
        btnRefresh = new JButton("Refresh");
        btnBack = new JButton("Back to Dashboard");

        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnResolve);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(formPanel, BorderLayout.SOUTH);

        // Load data
        loadStudentsIntoCombo();
        loadComplaintsFromDB();

        // Button Actions
        btnSubmit.addActionListener(e -> submitComplaint());
        btnResolve.addActionListener(e -> resolveComplaint());
        btnRefresh.addActionListener(e -> loadComplaintsFromDB());
        btnBack.addActionListener(e -> {
            dispose();
            new DashboardFrame();
        });

        setVisible(true);
    }

    private void loadStudentsIntoCombo() {
        cmbStudent.removeAllItems();
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT student_id, name FROM Students ORDER BY name")) {

            while (rs.next()) {
                cmbStudent.addItem(rs.getInt("student_id") + " - " + rs.getString("name"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage());
        }
    }

    private void loadComplaintsFromDB() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT c.complaint_id, s.name, c.description, c.complaint_date, c.status " +
                     "FROM Complaints c JOIN Students s ON c.student_id = s.student_id " +
                     "ORDER BY c.complaint_date DESC")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("complaint_id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("complaint_date"),
                    rs.getString("status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void submitComplaint() {
        String selectedStudent = (String) cmbStudent.getSelectedItem();
        if (selectedStudent == null || txtDescription.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select student and enter description!");
            return;
        }

        int studentId = Integer.parseInt(selectedStudent.split(" - ")[0]);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO Complaints (student_id, description) VALUES (?,?)")) {

            ps.setInt(1, studentId);
            ps.setString(2, txtDescription.getText().trim());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Complaint Submitted Successfully!");
            txtDescription.setText("");
            loadComplaintsFromDB();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resolveComplaint() {
        int row = complaintTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a complaint to resolve!");
            return;
        }

        int complaintId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(this, "Mark this complaint as Resolved?", "Resolve", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(
                         "UPDATE Complaints SET status = 'Resolved' WHERE complaint_id = ?")) {

                ps.setInt(1, complaintId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Complaint Resolved!");
                loadComplaintsFromDB();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new ComplaintsFrame();
    }
}
