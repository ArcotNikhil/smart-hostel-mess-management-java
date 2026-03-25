import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("Smart Hostel Mess System - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel lblTitle = new JLabel("Smart Hostel Mess System");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        add(new JLabel("Username:"), gbc);
        txtUsername = new JTextField(15);
        gbc.gridx = 1;
        add(txtUsername, gbc);

        // Password
        gbc.gridy = 2; gbc.gridx = 0;
        add(new JLabel("Password:"), gbc);
        txtPassword = new JPasswordField(15);
        gbc.gridx = 1;
        add(txtPassword, gbc);

        // Login Button
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        add(btnLogin, gbc);

        btnLogin.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Simple hardcoded credentials (you can change later)
        if (username.equals("admin") && password.equals("1234")) {
            dispose(); // close login window
            new DashboardFrame(); // open main dashboard
        } else if (username.equals("student") && password.equals("1234")) {
            dispose();
            JOptionPane.showMessageDialog(this, "Student login successful!\n(Full student view coming soon)", "Welcome", JOptionPane.INFORMATION_MESSAGE);
            new DashboardFrame(); // for now same dashboard
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new LoginFrame();
    }
}
