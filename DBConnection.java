import java.sql.*;

public class DBConnection {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "scott";      // Change if your username is different
    private static final String PASS = "tiger"; // ←←← CHANGE THIS TO YOUR ORACLE PASSWORD

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}