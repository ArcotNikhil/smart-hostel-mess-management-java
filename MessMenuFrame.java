import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessMenuFrame extends JFrame {

    private JTable menuTable;
    private DefaultTableModel tableModel;
    private JTextField txtDate, txtBreakfast, txtLunch, txtDinner;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnBack;

    public MessMenuFrame() {
        setTitle("Smart Hostel Mess System - Mess Menu Planner");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel lblTitle = new JLabel("Mess Menu Planner (Admin Only)", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(new Color(0, 102, 204));
        add(lblTitle, BorderLayout.NORTH);

        // Table
        String[] columns = {"Date", "Breakfast", "Lunch", "Dinner"};
        tableModel = new DefaultTableModel(columns, 0);
        menuTable = new JTable(tableModel);
        menuTable.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(menuTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add / Update Daily Menu"));

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        txtDate = new JTextField();
        formPanel.add(txtDate);

        formPanel.add(new JLabel("Breakfast:"));
        txtBreakfast = new JTextField();
        formPanel.add(txtBreakfast);

        formPanel.add(new JLabel("Lunch:"));
        txtLunch = new JTextField();
        formPanel.add(txtLunch);

        formPanel.add(new JLabel("Dinner:"));
        txtDinner = new JTextField();
        formPanel.add(txtDinner);

        add(formPanel, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel();
        btnAdd = new JButton("Add Menu");
        btnUpdate = new JButton("Update Menu");
        btnDelete = new JButton("Delete Menu");
        btnRefresh = new JButton("Refresh");
        btnBack = new JButton("Back to Dashboard");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnBack);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load menu when window opens
        loadMenuFromDB();

        // Button Actions
        btnAdd.addActionListener(e -> addMenu());
        btnUpdate.addActionListener(e -> updateMenu());
        btnDelete.addActionListener(e -> deleteMenu());
        btnRefresh.addActionListener(e -> loadMenuFromDB());
        btnBack.addActionListener(e -> {
            dispose();
            new DashboardFrame();
        });

        // Double-click on table to load data
        menuTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = menuTable.getSelectedRow();
                    txtDate.setText(tableModel.getValueAt(row, 0).toString());
                    txtBreakfast.setText(tableModel.getValueAt(row, 1).toString());
                    txtLunch.setText(tableModel.getValueAt(row, 2).toString());
                    txtDinner.setText(tableModel.getValueAt(row, 3).toString());
                }
            }
        });

        setVisible(true);
    }

    private void loadMenuFromDB() {
        tableModel.setRowCount(0);
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM MessMenu ORDER BY menu_date DESC")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getDate("menu_date"),
                    rs.getString("breakfast"),
                    rs.getString("lunch"),
                    rs.getString("dinner")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void addMenu() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO MessMenu (menu_date, breakfast, lunch, dinner) VALUES (?,?,?,?)")) {

            ps.setDate(1, java.sql.Date.valueOf(txtDate.getText()));
            ps.setString(2, txtBreakfast.getText());
            ps.setString(3, txtLunch.getText());
            ps.setString(4, txtDinner.getText());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Menu Added Successfully!");
            loadMenuFromDB();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMenu() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE MessMenu SET breakfast=?, lunch=?, dinner=? WHERE menu_date=?")) {

            ps.setString(1, txtBreakfast.getText());
            ps.setString(2, txtLunch.getText());
            ps.setString(3, txtDinner.getText());
            ps.setDate(4, java.sql.Date.valueOf(txtDate.getText()));

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Menu Updated Successfully!");
                loadMenuFromDB();
                clearFields();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteMenu() {
        int row = menuTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a menu to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this day's menu?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM MessMenu WHERE menu_date=?")) {

                ps.setDate(1, java.sql.Date.valueOf(tableModel.getValueAt(row, 0).toString()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Menu Deleted!");
                loadMenuFromDB();
                clearFields();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void clearFields() {
        txtDate.setText("");
        txtBreakfast.setText("");
        txtLunch.setText("");
        txtDinner.setText("");
    }

    public static void main(String[] args) {
        new MessMenuFrame();
    }
}
