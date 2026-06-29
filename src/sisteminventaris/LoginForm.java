package sisteminventaris;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Form Login - Sistem Inventaris Toko Berkah Jaya
 * Mata Kuliah : Pemrograman 2
 */
public class LoginForm extends JFrame {

    private JTextField     tfUsername;
    private JPasswordField pfPassword;
    private JButton        btnLogin;
    private JCheckBox      cbShowPassword;
    private JLabel         lblStatus;
    private Image          customLogo = null;

    private static final Color C_BG_LEFT   = new Color(17,  24,  39);
    private static final Color C_BG_LEFT2  = new Color(37,  99,  235);
    private static final Color C_BG_CARD   = new Color(255, 255, 255);
    private static final Color C_ACCENT    = new Color(37,  99,  235);
    private static final Color C_ACCENT_HV = new Color(29,  78,  216);
    private static final Color C_TEXT_DARK = new Color(15,  23,  42);
    private static final Color C_TEXT_GRAY = new Color(100, 116, 139);
    private static final Color C_BORDER    = new Color(203, 213, 225);
    private static final Color C_ERROR     = new Color(220, 38,  38);
    private static final Color C_SUCCESS   = new Color(22,  163, 74);

    private static final Font FONT_TITLE = new Font("Poppins", Font.BOLD,  22);
    private static final Font FONT_SUB   = new Font("Poppins", Font.PLAIN, 12);
    private static final Font FONT_LABEL = new Font("Poppins", Font.BOLD,  13);
    private static final Font FONT_INPUT = new Font("Poppins", Font.PLAIN, 14);
    private static final Font FONT_BTN   = new Font("Poppins", Font.BOLD,  14);

    public LoginForm() {
        super("Login - Sistem Inventaris Toko Berkah Jaya");
        loadCustomLogo();
        initUI();
    }

    private void loadCustomLogo() {
        try {
            java.net.URL imgURL = LoginForm.class.getResource("/sisteminventaris/images/logo.png");
            if (imgURL != null) {
                customLogo = new ImageIcon(imgURL).getImage();
            }
        } catch (Exception e) {
            // abaikan, pakai vector fallback
        }
    }

    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(920, 580);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 920, 580, 24, 24));

        JPanel root = new JPanel(null);
        root.setBackground(C_BG_LEFT);
        setContentPane(root);

        JPanel left  = buildLeftPanel();
        JPanel right = buildRightPanel();
        left.setBounds(0, 0, 420, 580);
        right.setBounds(420, 0, 500, 580);
        root.add(left);
        root.add(right);

        // Tombol close
        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Poppins", Font.BOLD, 14));
        btnClose.setForeground(new Color(148, 163, 184));
        btnClose.setOpaque(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.setBounds(876, 8, 36, 36);
        btnClose.addActionListener(e -> System.exit(0));
        btnClose.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnClose.setForeground(C_ERROR); }
            @Override public void mouseExited(MouseEvent e)  { btnClose.setForeground(new Color(148,163,184)); }
        });
        root.add(btnClose);

        enableDrag(root);
    }

    private JPanel buildLeftPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(15,23,42), getWidth(), getHeight(), new Color(37,99,235));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Dekorasi lingkaran
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.07f));
                g2.setColor(Color.WHITE);
                g2.fillOval(-80, -80, 300, 300);
                g2.fillOval(getWidth() - 130, getHeight() - 130, 280, 280);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.04f));
                g2.fillOval(60, getHeight()/2 - 100, 200, 200);

                // ✅ Reset AlphaComposite
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        };
        p.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JPanel icon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (customLogo != null) {
                    g2.drawImage(customLogo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setColor(Color.WHITE);
                    // Gambar tas belanja (Shopping Bag)
                    g2.fillRoundRect(15, 25, 50, 45, 10, 10);
                    g2.setStroke(new BasicStroke(4));
                    g2.drawArc(27, 10, 26, 26, 0, 180);
                }
            }
        };
        icon.setPreferredSize(new Dimension(80, 80));
        icon.setMaximumSize(new Dimension(80, 80));
        icon.setMinimumSize(new Dimension(80, 80));
        icon.setOpaque(false);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Toko Berkah Jaya");
        title.setFont(new Font("Poppins", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Sistem Manajemen ");
        sub.setFont(new Font("Poppins", Font.PLAIN, 13));
        sub.setForeground(new Color(147, 197, 253));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Separator line
        JPanel line = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), 1);

                // ✅ Reset AlphaComposite
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        };
        line.setOpaque(false);
        line.setMaximumSize(new Dimension(200, 1));
        line.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblFeature1 = makeBadge("•  Manajemen Stok Barang");
        JLabel lblFeature2 = makeBadge("•  Transaksi Penjualan");
        JLabel lblFeature3 = makeBadge("•  Laporan Real-time");

        JLabel lblCopyright = new JLabel("Pemrograman 2  •  2025");
        lblCopyright.setFont(new Font("Poppins", Font.PLAIN, 11));
        lblCopyright.setForeground(new Color(71, 95, 135));
        lblCopyright.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(icon);
        inner.add(Box.createVerticalStrut(14));
        inner.add(title);
        inner.add(Box.createVerticalStrut(5));
        inner.add(sub);
        inner.add(Box.createVerticalStrut(24));
        inner.add(line);
        inner.add(Box.createVerticalStrut(20));
        inner.add(lblFeature1);
        inner.add(Box.createVerticalStrut(8));
        inner.add(lblFeature2);
        inner.add(Box.createVerticalStrut(8));
        inner.add(lblFeature3);
        inner.add(Box.createVerticalStrut(28));
        inner.add(lblCopyright);

        p.add(inner);
        return p;
    }

    private JLabel makeBadge(String text) {
        JLabel lbl = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Poppins", Font.PLAIN, 12));
        lbl.setForeground(new Color(191, 219, 254));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(5, 16, 5, 16));
        lbl.setOpaque(false);
        return lbl;
    }

    private JPanel buildRightPanel() {
        JPanel card = new JPanel();
        card.setBackground(C_BG_CARD);
        card.setLayout(new GridBagLayout());

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(10, 52, 10, 52));

        JLabel lblTitle = new JLabel("Selamat Datang");
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(C_TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Masuk ke akun Anda untuk melanjutkan");
        lblSub.setFont(FONT_SUB);
        lblSub.setForeground(C_TEXT_GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = makeLabel("Username");
        tfUsername = makeTextField("Masukkan username");

        JLabel lblPass = makeLabel("Password");
        pfPassword = makePasswordField("Masukkan password");

        cbShowPassword = new JCheckBox("Tampilkan Password");
        cbShowPassword.setFont(new Font("Poppins", Font.PLAIN, 12));
        cbShowPassword.setForeground(C_TEXT_GRAY);
        cbShowPassword.setOpaque(false);
        cbShowPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbShowPassword.addActionListener(e ->
                pfPassword.setEchoChar(cbShowPassword.isSelected() ? (char)0 : '●'));

        btnLogin = new JButton("Masuk") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? C_ACCENT_HV : C_ACCENT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                // Shimmer effect
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight()/2, 10, 10);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                super.paintComponent(g);
            }
        };
        btnLogin.setFont(FONT_BTN);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setOpaque(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> doLogin());

        ActionListener enter = e -> doLogin();
        tfUsername.addActionListener(enter);
        pfPassword.addActionListener(enter);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Poppins", Font.BOLD, 12));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(lblTitle);
        form.add(Box.createVerticalStrut(4));
        form.add(lblSub);
        form.add(Box.createVerticalStrut(32));
        form.add(lblUser);
        form.add(Box.createVerticalStrut(6));
        form.add(tfUsername);
        form.add(Box.createVerticalStrut(18));
        form.add(lblPass);
        form.add(Box.createVerticalStrut(6));
        form.add(pfPassword);
        form.add(Box.createVerticalStrut(8));
        form.add(cbShowPassword);
        form.add(Box.createVerticalStrut(26));
        form.add(btnLogin);
        form.add(Box.createVerticalStrut(12));
        form.add(lblStatus);

        card.add(form);
        return card;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(C_TEXT_DARK);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField makeTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(FONT_INPUT);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setForeground(C_TEXT_GRAY);
        tf.setText(placeholder);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        tf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) { tf.setText(""); tf.setForeground(C_TEXT_DARK); }
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_ACCENT, 2, true), new EmptyBorder(7,11,7,11)));
            }
            @Override public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) { tf.setText(placeholder); tf.setForeground(C_TEXT_GRAY); }
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_BORDER, 1, true), new EmptyBorder(8,12,8,12)));
            }
        });
        return tf;
    }

    private JPasswordField makePasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_INPUT);
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        pf.setForeground(C_TEXT_GRAY);
        pf.setEchoChar((char) 0);
        pf.setText(placeholder);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        pf.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (String.valueOf(pf.getPassword()).equals(placeholder)) {
                    pf.setText(""); pf.setEchoChar('●'); pf.setForeground(C_TEXT_DARK);
                }
                pf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_ACCENT, 2, true), new EmptyBorder(7,11,7,11)));
            }
            @Override public void focusLost(FocusEvent e) {
                if (pf.getPassword().length == 0) {
                    pf.setEchoChar((char) 0); pf.setText(placeholder); pf.setForeground(C_TEXT_GRAY);
                }
                pf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_BORDER, 1, true), new EmptyBorder(8,12,8,12)));
            }
        });
        return pf;
    }

    private void enableDrag(JPanel root) {
        final int[] startX = {0}, startY = {0};
        root.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { startX[0] = e.getX(); startY[0] = e.getY(); }
        });
        root.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                Point loc = getLocation();
                setLocation(loc.x + e.getX() - startX[0], loc.y + e.getY() - startY[0]);
            }
        });
    }

    private void doLogin() {
        String username = tfUsername.getText().trim();
        String password = String.valueOf(pfPassword.getPassword());

        if (username.isEmpty() || username.equals("Masukkan username")) {
            showStatus("Username tidak boleh kosong!", C_ERROR); tfUsername.requestFocus(); return;
        }
        if (password.isEmpty() || password.equals("Masukkan password")) {
            showStatus("Password tidak boleh kosong!", C_ERROR); pfPassword.requestFocus(); return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Memeriksa...");
        lblStatus.setText(" ");

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            String levelResult = "";
            int    idUser      = 0;

            @Override
            protected Boolean doInBackground() {
                Connection conn = null;
                try {
                    conn = Database.getConnection();
                    String sql = "SELECT * FROM tb_user WHERE username = ? AND password = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        levelResult = rs.getString("level");
                        idUser      = rs.getInt("id_user");
                        return true;
                    }
                    return false;
                } catch (SQLException ex) {
                    levelResult = "DB_ERROR: " + ex.getMessage();
                    return false;
                } finally {
                    Database.closeConnection(conn);
                }
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                btnLogin.setText("Masuk");
                try {
                    boolean ok = get();
                    if (ok) {
                        showStatus("✓ Login berhasil! Selamat datang, " + username, C_SUCCESS);
                        new MainForm(idUser, username, levelResult).setVisible(true);
                        dispose();
                    } else {
                        if (levelResult.startsWith("DB_ERROR")) {
                            String errorDetail = levelResult.replace("DB_ERROR: ", "");
                            System.err.println("=== DB ERROR DETAIL: " + errorDetail + " ===");
                            showStatus("⚠ " + errorDetail, C_ERROR);
                        } else {
                            showStatus("✗ Username atau password salah!", C_ERROR);
                            pfPassword.setText("");
                            pfPassword.requestFocus();
                        }
                    }
                } catch (Exception ex) {
                    showStatus("⚠ Terjadi kesalahan: " + ex.getMessage(), C_ERROR);
                }
            }
        };
        worker.execute();
    }

    private void showStatus(String msg, Color color) {
        lblStatus.setText(msg);
        lblStatus.setForeground(color);
    }
}
// Force recompile dummy comment
