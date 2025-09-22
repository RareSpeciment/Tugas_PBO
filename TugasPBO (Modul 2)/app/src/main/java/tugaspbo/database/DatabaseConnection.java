package tugaspbo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/sistem_mahasiswa";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "1234567890"; // Password PostgreSQL Anda
    private static Connection connection = null;
    
    // Method untuk mendapatkan koneksi database
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.postgresql.Driver");
                
                // Jika password kosong, minta input dari user
                String password = DB_PASSWORD;
                if (password.isEmpty()) {
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Masukkan password PostgreSQL untuk user 'postgres': ");
                    password = scanner.nextLine();
                }
                
                connection = DriverManager.getConnection(DB_URL, DB_USER, password);
                System.out.println("Koneksi database PostgreSQL berhasil!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Driver PostgreSQL tidak ditemukan: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Gagal koneksi ke database PostgreSQL: " + e.getMessage());
            System.err.println("Periksa:");
            System.err.println("1. PostgreSQL server sudah berjalan");
            System.err.println("2. Password PostgreSQL benar");
            System.err.println("3. Database 'sistem_mahasiswa' sudah dibuat");
        }
        return connection;
    }
    
    // Method untuk menutup koneksi database
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi database ditutup.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal menutup koneksi: " + e.getMessage());
        }
    }
    
    // Method untuk mengecek koneksi
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                // Test query sederhana
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT 1");
                rs.close();
                stmt.close();
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Test koneksi gagal: " + e.getMessage());
            return false;
        }
    }
}
