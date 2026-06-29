package sisteminventaris;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

/**
 * Main entry point aplikasi Sistem Inventaris
 * Mata Kuliah : Pemrograman 2
 */
public class Main {
    public static void main(String[] args) {
        initFonts();
        // Jalankan di EDT (Event Dispatch Thread) agar aman untuk Swing
        java.awt.EventQueue.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }

    private static void initFonts() {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            
            // Register Regular
            try (InputStream is = Main.class.getResourceAsStream("/sisteminventaris/fonts/Poppins-Regular.ttf")) {
                if (is != null) {
                    ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, is));
                }
            }
            
            try (InputStream is = Main.class.getResourceAsStream("/sisteminventaris/fonts/Poppins-Medium.ttf")) {
                if (is != null) {
                    ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, is));
                }
            }
            
         
            try (InputStream is = Main.class.getResourceAsStream("/sisteminventaris/fonts/Poppins-Bold.ttf")) {
                if (is != null) {
                    ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, is));
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat font Poppins: " + e.getMessage());
        }
    }
}
