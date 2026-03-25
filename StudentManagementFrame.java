import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentManagementFrame extends JFrame {

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField txtStudentId, txtName, txtRollNo, txtRoomNo, txtPhone, txtSearch;

    public StudentManagementFrame() {
        setTitle("Smart Hostel Mess System - Manage Students");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel lblTitle = new JLabel("Student Management", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search by Name / Roll No:"));
        txtSearch = new JTextField(20);
        searchPanel.add(txtSearch);
        JButton btnSearch = new JButton("Search");
        JButton btnRefresh = new JButton("Refresh All");
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);
        add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Student ID", "Name", "Roll No", "Room No", "Phone"};
        tableModel = new DefaultTableModel(columns, 0);
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form Panel (Bottom)
        JPanel formPanel = new JPanel(new GridLayout(2, 6, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));

        formPanel.add(new JLabel("Student ID:"));
        txtStudentId = new JTextField();
        formPanel.add(txtStudentId);

        formPanel.add(new JLabel("Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Roll No:"));
        txtRollNo = new JTextField();
        formPanel.add(txtRollNo);

        formPanel.add(new JLabel("Room No:"));
        txtRoomNo = new JTextField();
        formPanel.add(txtRoomNo);

        formPanel.add(new JLabel("Phone:"));
        txtPhone = new JTextField();
        formPanel.add(txtPhone);

        // Buttons
        JButton btnAdd = new JButton("Add Student");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        JButton btnBack = new JButton("Back to Dashboard");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load data when window opens
        loadStudentsFromDB();

        // Button Listeners
        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearFields());
        btnRefresh.addActionListener(e -> loadStudentsFromDB());
        btnSearch.addActionListener(e -> searchStudent());
        btnBack.addActionListener(e -> {
            dispose();
            new DashboardFrame();
        });

        // Double click on table to load data into fields
        studentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = studentTable.getSelectedRow();
                    txtStudentId.setText(tableModel.getValueAt(row, 0).toString());
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    txtRollNo.setText(tableModel.getValueAt(row, 2).toString());
                    txtRoomNo.setText(tableModel.getValueAt(row, 3).toString());
                    txtPhone.setText(tableModel.getValueAt(row, 4).toString());
                }
            }
        });

        setVisible(true);
    }

    private void loadStudentsFromDB() {
        tableModel.setRowCount(0); // clear table
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Students ORDER BY student_id")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getString("roll_no"),
                    rs.getString("room_no"),
                    rs.getString("phone")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addStudent() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO Students (student_id, name, roll_no, room_no, phone) VALUES (?,?,?,?,?)")) {

            ps.setInt(1, Integer.parseInt(txtStudentId.getText()));
            ps.setString(2, txtName.getText());
            ps.setString(3, txtRollNo.getText());
            ps.setString(4, txtRoomNo.getText());
            ps.setString(5, txtPhone.getText());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student Added Successfully!");
            loadStudentsFromDB();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Students SET name=?, roll_no=?, room_no=?, phone=? WHERE student_id=?")) {

            ps.setString(1, txtName.getText());
            ps.setString(2, txtRollNo.getText());
            ps.setString(3, txtRoomNo.getText());
            ps.setString(4, txtPhone.getText());
            ps.setInt(5, Integer.parseInt(txtStudentId.getText()));

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Student Updated Successfully!");
                loadStudentsFromDB();
                clearFields();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this student?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM Students WHERE student_id=?")) {

                ps.setInt(1, Integer.parseInt(tableModel.getValueAt(row, 0).toString()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student Deleted!");
                loadStudentsFromDB();
                clearFields();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void searchStudent() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT * FROM Students WHERE name LIKE ? OR roll_no LIKE ?")) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("student_id"), rs.getString("name"),
                    rs.getString("roll_no"), rs.getString("room_no"), rs.getString("phone")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search Error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        txtStudentId.setText("");
        txtName.setText("");
        txtRollNo.setText("");
        txtRoomNo.setText("");
        txtPhone.setText("");
    }

    // For testing this screen alone
    public static void main(String[] args) {
        new StudentManagementFrame();
    }
}
