import java.sql.Connection;
import java.sql.DriverManager;

public class TestKoneksi {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/db_inventaris?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";
        System.out.println("Testing koneksi ke: " + url);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[OK] Driver ditemukan.");
        } catch (ClassNotFoundException e) {
            System.err.println("[GAGAL] Driver TIDAK ditemukan: " + e.getMessage());
            System.err.println(">> Pastikan mysql-connector-j-8.0.33.jar ada di classpath.");
            return;
        }
        try {
            Connection conn = DriverManager.getConnection(url, "root", "");
            System.out.println("[OK] KONEKSI BERHASIL!");
            conn.close();
        } catch (Exception e) {
            System.err.println("[GAGAL] Koneksi gagal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
