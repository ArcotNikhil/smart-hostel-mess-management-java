import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("Smart Hostel Mess System - Dashboard");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(0, 102, 204));
        topPanel.setPreferredSize(new Dimension(1000, 90));

        JLabel lblWelcome = new JLabel("Welcome to Smart Hostel Mess System", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 26));
        lblWelcome.setForeground(Color.WHITE);
        topPanel.add(lblWelcome);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(3, 3, 30, 30));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 60, 50, 60));

        JButton btnStudents   = createModuleButton("Manage Students", "👥");
        JButton btnMenu       = createModuleButton("Mess Menu", "🍽️");
        JButton btnAttendance = createModuleButton("Mark Attendance", "📅");
        JButton btnBilling    = createModuleButton("Billing & Fines", "💰");
        JButton btnComplaints = createModuleButton("Complaints", "📢");
        JButton btnReports    = createModuleButton("Monthly Reports", "📊");
        JButton btnLogout     = createModuleButton("Logout", "🚪");

        centerPanel.add(btnStudents);
        centerPanel.add(btnMenu);
        centerPanel.add(btnAttendance);
        centerPanel.add(btnBilling);
        centerPanel.add(btnComplaints);
        centerPanel.add(btnReports);
        centerPanel.add(new JLabel()); 
        centerPanel.add(new JLabel()); 
        centerPanel.add(btnLogout);

        add(centerPanel, BorderLayout.CENTER);

        // === ALL BUTTONS FULLY CONNECTED ===
        btnStudents.addActionListener(e -> { dispose(); new StudentManagementFrame(); });
        btnMenu.addActionListener(e -> { dispose(); new MessMenuFrame(); });
        btnAttendance.addActionListener(e -> { dispose(); new AttendanceFrame(); });
        btnBilling.addActionListener(e -> { dispose(); new BillingFrame(); });
        btnComplaints.addActionListener(e -> { dispose(); new ComplaintsFrame(); });
        
        // Reports button now opens the actual ReportsFrame
        btnReports.addActionListener(e -> { 
            dispose(); 
            new ReportsFrame(); 
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Do you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
        });

        setVisible(true);
    }

    private JButton createModuleButton(String text, String emoji) {
        JButton btn = new JButton(emoji + "   " + text);
        btn.setFont(new Font("Arial", Font.PLAIN, 18));
        btn.setPreferredSize(new Dimension(230, 95));
        btn.setFocusPainted(false);
        return btn;
    }

    public static void main(String[] args) {
        new DashboardFrame();
    }
}