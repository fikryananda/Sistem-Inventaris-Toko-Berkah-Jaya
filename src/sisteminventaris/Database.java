package sisteminventaris;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Kelas Database: mengelola koneksi ke MySQL via JDBC.
 * Pastikan MySQL XAMPP berjalan dan database "db_inventaris" sudah dibuat.
 *
 * Mata Kuliah : Pemrograman 2
 */
public class Database {

    // ===================== KONFIGURASI KONEKSI =====================
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DB_NAME  = "db_inventaris";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";         // kosong = default XAMPP
    // ================================================================

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME
            + "?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";

    /**
     * Mendapatkan koneksi ke database.
     * @return objek Connection
     * @throws SQLException jika koneksi gagal
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("=== ERROR: Driver MySQL tidak ditemukan! ===");
            System.err.println("Pastikan mysql-connector-j-8.0.33.jar ada di folder lib/");
            throw new SQLException("Driver MySQL tidak ditemukan. Tambahkan mysql-connector ke library.", e);
        }
        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✓ Koneksi ke database berhasil! URL: " + URL);
            return conn;
        } catch (SQLException e) {
            System.err.println("=== ERROR: Gagal konek ke database ===");
            System.err.println("URL  : " + URL);
            System.err.println("User : " + USERNAME);
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Menutup koneksi dengan aman.
     * @param conn koneksi yang akan ditutup
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Gagal menutup koneksi: " + e.getMessage());
            }
        }
    }
}
