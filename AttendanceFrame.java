import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AttendanceFrame extends JFrame {

    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbStudent;
    private JComboBox<String> cmbMealType;
    private JComboBox<String> cmbStatus;
    private JTextField txtDate;
    private JButton btnMark, btnRefresh, btnBack;

    public AttendanceFrame() {
        setTitle("Smart Hostel Mess System - Mark Attendance");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel lblTitle = new JLabel("Daily Attendance Marking", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Form Panel (Top)
        JPanel formPanel = new JPanel(new GridLayout(1, 5, 15, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Mark Today's Attendance"));

        formPanel.add(new JLabel("Student:"));
        cmbStudent = new JComboBox<>();
        formPanel.add(cmbStudent);

        formPanel.add(new JLabel("Meal Type:"));
        cmbMealType = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner"});
        formPanel.add(cmbMealType);

        formPanel.add(new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"Present", "Absent"});
        formPanel.add(cmbStatus);

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        txtDate = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        formPanel.add(txtDate);

        add(formPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Attendance ID", "Student Name", "Date", "Meal Type", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        btnMark = new JButton("Mark Attendance");
        btnRefresh = new JButton("Refresh All");
        btnBack = new JButton("Back to Dashboard");

        buttonPanel.add(btnMark);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data
        loadStudentsIntoCombo();
        loadAttendanceFromDB();

        // Button Actions
        btnMark.addActionListener(e -> markAttendance());
        btnRefresh.addActionListener(e -> loadAttendanceFromDB());
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

    private void loadAttendanceFromDB() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT a.attendance_id, s.name, a.attendance_date, a.meal_type, a.status " +
                     "FROM Attendance a JOIN Students s ON a.student_id = s.student_id " +
                     "ORDER BY a.attendance_date DESC")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("attendance_id"),
                    rs.getString("name"),
                    rs.getDate("attendance_date"),
                    rs.getString("meal_type"),
                    rs.getString("status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void markAttendance() {
        String selectedStudent = (String) cmbStudent.getSelectedItem();
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Please select a student!");
            return;
        }

        int studentId = Integer.parseInt(selectedStudent.split(" - ")[0]);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO Attendance (student_id, attendance_date, meal_type, status) VALUES (?,?,?,?)")) {

            ps.setInt(1, studentId);
            ps.setDate(2, java.sql.Date.valueOf(txtDate.getText()));
            ps.setString(3, (String) cmbMealType.getSelectedItem());
            ps.setString(4, (String) cmbStatus.getSelectedItem());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Attendance Marked Successfully!");
            loadAttendanceFromDB();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new AttendanceFrame();
    }
}
