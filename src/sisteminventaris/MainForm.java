package sisteminventaris;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * MainForm - Dashboard Utama Sistem Inventaris Toko Berkah Jaya
 * Mata Kuliah : Pemrograman 2
 */
public class MainForm extends JFrame {

    private final int idUser;
    private final String username;
    private final String level;

    private JLabel lblBarangVal;
    private JLabel lblCustomerVal;
    private JLabel lblPendapatanVal;
    

    // Sidebar state
    private JPanel sidebarPanel;
    private boolean sidebarVisible = true;
    private Image customLogo = null;

    // ─── Color Palette ───────────────────────────────────────────
    private static final Color C_SIDEBAR     = new Color(15, 23, 42);
    private static final Color C_CONTENT     = new Color(30, 41, 59);
    private static final Color C_CARD        = new Color(36, 48, 68);
    private static final Color C_CARD_BORDER = new Color(50, 64, 86);
    private static final Color C_SEPARATOR   = new Color(44, 55, 78);
    private static final Color C_WHITE       = Color.WHITE;
    private static final Color C_TEXT_DIM    = new Color(148, 163, 184);
    private static final Color C_TEXT_LIGHT  = new Color(203, 213, 225);
    private static final Color C_TEXT_MUTED  = new Color(71, 85, 105);

    private static final Color C_BLUE    = new Color(59, 130, 246);
    private static final Color C_TEAL    = new Color(20, 184, 166);
    private static final Color C_PURPLE  = new Color(139, 92, 246);
    private static final Color C_EMERALD = new Color(16, 185, 129);
    private static final Color C_AMBER   = new Color(245, 158, 11);
    private static final Color C_RED     = new Color(239, 68, 68);
    private static final Color C_PINK    = new Color(236, 72, 153);

    private static final int SIDEBAR_W = 260;

    // Menu configuration
    private static final String[] M_TITLE = { "Data Kategori", "Data Barang", "Data Customer", "Transaksi", "Laporan", "Data Petugas" };
    private static final String[] M_DESC  = { "Kelola kategori barang", "Stok, harga & data barang", "Data pelanggan toko", "Proses penjualan barang", "Riwayat transaksi penjualan", "Kelola kasir & petugas" };
    private static final Color[]  M_COLOR = { C_TEAL, C_BLUE, C_PURPLE, C_EMERALD, C_AMBER, C_PINK };
    private static final String[] M_ICON  = { "folder", "box", "people", "cart", "chart", "key" };

    public MainForm(int idUser, String username, String level) {
        this.idUser = idUser;
        this.username = username;
        this.level = level;
        loadCustomLogo();
        initUI();
    }

    private void loadCustomLogo() {
        try {
            java.net.URL imgURL = MainForm.class.getResource("/sisteminventaris/images/logo.png");
            if (imgURL != null) {
                customLogo = new ImageIcon(imgURL).getImage();
            }
        } catch (Exception e) {
            // abaikan, pakai vector fallback
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  INIT UI
    // ═══════════════════════════════════════════════════════════════
    private void initUI() {
        setTitle("Toko Berkah Jaya - Dashboard Utama");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_CONTENT);
        setContentPane(root);

        sidebarPanel = buildSidebar();
        root.add(sidebarPanel, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(C_CONTENT);
        content.add(buildTopBar(), BorderLayout.NORTH);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(C_CONTENT);
        main.setBorder(new EmptyBorder(28, 40, 20, 40));
        main.add(buildStatsPanel(), BorderLayout.NORTH);

        // Wrapper for chart and cards
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setOpaque(false);

        // 1. Chart Section
        JPanel chartSection = new JPanel(new BorderLayout());
        chartSection.setOpaque(false);
        chartSection.setBorder(new EmptyBorder(28, 0, 0, 0));
        
        JLabel lblChart = new JLabel("Tren Penjualan 7 Hari Terakhir");
        lblChart.setFont(new Font("Poppins", Font.BOLD, 16));
        lblChart.setForeground(C_WHITE);
        lblChart.setBorder(new EmptyBorder(0, 4, 12, 0));
        chartSection.add(lblChart, BorderLayout.NORTH);
        
       

        // 2. Feature cards section
        JPanel cardsSection = new JPanel(new BorderLayout());
        cardsSection.setOpaque(false);
        cardsSection.setBorder(new EmptyBorder(28, 0, 0, 0));

        JLabel lblSection = new JLabel("Akses Cepat");
        lblSection.setFont(new Font("Poppins", Font.BOLD, 16));
        lblSection.setForeground(C_WHITE);
        lblSection.setBorder(new EmptyBorder(0, 4, 16, 0));
        cardsSection.add(lblSection, BorderLayout.NORTH);
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(buildFeatureCards(), BorderLayout.NORTH);
        cardsSection.add(gridWrapper, BorderLayout.CENTER);
        
        centerWrapper.add(cardsSection);

        main.add(centerWrapper, BorderLayout.CENTER);

        JScrollPane scrollMain = new JScrollPane(main);
        scrollMain.setBorder(null);
        scrollMain.getViewport().setBackground(C_CONTENT);
        scrollMain.setBackground(C_CONTENT);
        scrollMain.getVerticalScrollBar().setUnitIncrement(16);
        scrollMain.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        content.add(scrollMain, BorderLayout.CENTER);
        content.add(buildFooter(), BorderLayout.SOUTH);

        root.add(content, BorderLayout.CENTER);
        refreshStats();
    }

    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;
        sidebarPanel.setVisible(sidebarVisible);
        revalidate();
        repaint();
    }

    private boolean isMenuAllowed(String title) {
        if (level != null && level.equalsIgnoreCase("Petugas")) {
            return !title.equals("Data Kategori") && !title.equals("Data Barang") && !title.equals("Laporan") && !title.equals("Data Petugas");
        }
        return true;
    }

    // ═══════════════════════════════════════════════════════════════
    //  SIDEBAR
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sb = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(C_SEPARATOR);
                g.fillRect(getWidth() - 1, 0, 1, getHeight());
            }
        };
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(C_SIDEBAR);
        sb.setPreferredSize(new Dimension(SIDEBAR_W, 0));

        sb.add(buildLogo());
        sb.add(makeSep());
        sb.add(Box.createVerticalStrut(16));
        sb.add(makeSection("MENU UTAMA"));
        sb.add(Box.createVerticalStrut(8));

        for (int i = 0; i < M_TITLE.length; i++) {
            if (isMenuAllowed(M_TITLE[i])) {
                sb.add(makeMenuItem(M_TITLE[i], M_COLOR[i], M_ICON[i]));
                sb.add(Box.createVerticalStrut(4));
            }
        }

        sb.add(Box.createVerticalStrut(20));
        sb.add(makeSep());
        sb.add(Box.createVerticalStrut(16));
        sb.add(makeSection("SISTEM"));
        sb.add(Box.createVerticalStrut(8));
        sb.add(makeMenuItem("Logout", C_RED, "logout"));

        sb.add(Box.createVerticalGlue());
        sb.add(makeSep());

        JLabel lblVer = new JLabel("v1.0  \u2022  Pemrograman 2  \u2022  2025");
        lblVer.setFont(new Font("Poppins", Font.PLAIN, 9));
        lblVer.setForeground(C_TEXT_MUTED);
        lblVer.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblVer.setBorder(new EmptyBorder(12, 0, 16, 0));
        sb.add(lblVer);

        return sb;
    }

    private JPanel buildLogo() {
        // All painted in paintComponent for pixel-perfect left alignment
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Shopping bag icon or custom logo
                int ix = 24, iy = 18;
                if (customLogo != null) {
                    g2.drawImage(customLogo, ix, iy, 40, 40, this);
                } else {
                    GradientPaint gp = new GradientPaint(ix, iy, C_BLUE, ix + 40, iy + 40, C_TEAL);
                    g2.setPaint(gp);
                    g2.fillRoundRect(ix, iy, 40, 40, 10, 10);
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(ix + 9, iy + 15, 22, 18, 4, 4);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawArc(ix + 13, iy + 8, 14, 12, 0, 180);
                }

                // Store name & subtitle
                g2.setFont(new Font("Poppins", Font.BOLD, 15));
                g2.setColor(C_WHITE);
                g2.drawString("Toko Berkah Jaya", ix + 52, iy + 18);
                g2.setFont(new Font("Poppins", Font.PLAIN, 11));
                g2.setColor(C_TEXT_DIM);
                g2.drawString("Sistem Inventaris", ix + 52, iy + 34);
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(SIDEBAR_W, 76));
        p.setMaximumSize(new Dimension(SIDEBAR_W, 76));
        return p;
    }

    // All painting in paintComponent so mouse events work on the entire area
    private JPanel makeMenuItem(String title, Color accentColor, String iconType) {
        final boolean allowed = isMenuAllowed(title);
        final Color clr = allowed ? accentColor : new Color(51, 65, 85);

        JPanel item = new JPanel() {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (allowed) {
                            hovered = true;
                            repaint();
                            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                        setCursor(Cursor.getDefaultCursor());
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (allowed) {
                            handleClick(title);
                        } else {
                            JOptionPane.showMessageDialog(MainForm.this,
                                    "Akses Ditolak!\nMenu '" + title + "' hanya dapat diakses oleh Admin.",
                                    "Akses Dibatasi", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Hover: tinted background + left accent bar
                if (hovered && allowed) {
                    g2.setColor(new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 18));
                    g2.fillRoundRect(8, 2, getWidth() - 16, getHeight() - 4, 10, 10);
                    g2.setColor(clr);
                    g2.fillRoundRect(0, 6, 3, getHeight() - 12, 3, 3);
                }

                // Icon background (rounded rect with tinted accent)
                int ibg = 28, ix = 22, iy = (getHeight() - ibg) / 2;
                g2.setColor(new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 25));
                g2.fillRoundRect(ix, iy, ibg, ibg, 8, 8);
                paintIcon(g2, iconType, clr, ix + ibg / 2, iy + ibg / 2, 7);

                // Title text
                g2.setFont(new Font("Poppins", Font.PLAIN, 13));
                g2.setColor(allowed ? C_TEXT_LIGHT : C_TEXT_MUTED);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(title, ix + ibg + 12,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);

                // Lock indicator for restricted items
                if (!allowed) {
                    g2.setColor(C_TEXT_MUTED);
                    int lx = getWidth() - 30, ly = getHeight() / 2;
                    g2.fillRoundRect(lx, ly - 2, 10, 8, 2, 2);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawArc(lx + 2, ly - 7, 6, 8, 0, 180);
                }
            }
        };
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(SIDEBAR_W, 44));
        item.setPreferredSize(new Dimension(SIDEBAR_W, 44));
        return item;
    }

    private JPanel makeSep() {
        JPanel s = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(C_SEPARATOR);
                g.fillRect(24, 0, getWidth() - 48, 1);
            }
        };
        s.setOpaque(false);
        s.setMaximumSize(new Dimension(SIDEBAR_W, 1));
        s.setPreferredSize(new Dimension(SIDEBAR_W, 1));
        return s;
    }

    private JLabel makeSection(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Poppins", Font.BOLD, 10));
        lbl.setForeground(C_TEXT_MUTED);
        lbl.setBorder(new EmptyBorder(0, 26, 0, 0));
        lbl.setMaximumSize(new Dimension(SIDEBAR_W, 18));
        return lbl;
    }

    // ═══════════════════════════════════════════════════════════════
    //  TOP BAR
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(C_SEPARATOR);
                g.fillRect(0, getHeight() - 1, getWidth(), 1);
            }
        };
        bar.setBackground(C_CONTENT);
        bar.setPreferredSize(new Dimension(0, 64));
        bar.setBorder(new EmptyBorder(0, 20, 0, 36));

        // ── Left: hamburger toggle + greeting ──
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.setOpaque(false);

        // Hamburger toggle button
        JPanel toggleBtn = new JPanel() {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        toggleSidebar();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered) {
                    g2.setColor(new Color(59, 130, 246, 15));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setColor(hovered ? C_WHITE : C_TEXT_LIGHT);
                for (int i = 0; i < 3; i++) {
                    g2.fillRoundRect(8, 10 + i * 6, 18, 2, 1, 1);
                }
            }
        };
        toggleBtn.setPreferredSize(new Dimension(34, 34));
        toggleBtn.setMaximumSize(new Dimension(34, 34));
        toggleBtn.setOpaque(false);
        toggleBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleBtn.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Greeting text
        JPanel greeting = new JPanel();
        greeting.setLayout(new BoxLayout(greeting, BoxLayout.Y_AXIS));
        greeting.setOpaque(false);
        greeting.setBorder(new EmptyBorder(0, 16, 0, 0));
        greeting.setAlignmentY(Component.CENTER_ALIGNMENT);

        JLabel lblHi = new JLabel(getGreeting() + ", " + username + "!");
        lblHi.setFont(new Font("Poppins", Font.BOLD, 17));
        lblHi.setForeground(C_WHITE);
        lblHi.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDate = new JLabel(new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date()));
        lblDate.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblDate.setForeground(C_TEXT_DIM);
        lblDate.setAlignmentX(Component.LEFT_ALIGNMENT);

        greeting.add(lblHi);
        greeting.add(Box.createVerticalStrut(2));
        greeting.add(lblDate);

        left.add(toggleBtn);
        left.add(greeting);

        // ── Right: role badge + avatar ──
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(14, 0, 14, 0));

        final boolean isAdmin = level != null && level.equalsIgnoreCase("Admin");
        final Color badgeAccent = isAdmin ? C_BLUE : C_EMERALD;
        final String roleText = level != null ? level : "User";
        final String init = (username != null && !username.isEmpty())
                ? username.substring(0, 1).toUpperCase() : "U";

        // Role badge (pill shape)
        JLabel lblRole = new JLabel(roleText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(badgeAccent.getRed(), badgeAccent.getGreen(), badgeAccent.getBlue(), 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        lblRole.setFont(new Font("Poppins", Font.BOLD, 11));
        lblRole.setForeground(badgeAccent);
        lblRole.setOpaque(false);
        lblRole.setBorder(new EmptyBorder(6, 14, 6, 14));

        // Avatar circle
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, C_BLUE, 34, 34, C_TEAL);
                g2.setPaint(gp);
                g2.fillOval(0, 0, 34, 34);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Poppins", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(init,
                        (34 - fm.stringWidth(init)) / 2,
                        (34 + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        avatar.setPreferredSize(new Dimension(34, 34));
        avatar.setOpaque(false);

        // Logout button in header next to profile
        JButton btnLogoutHeader = new JButton("Logout") {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                if (hovered) {
                    g2.setColor(new Color(239, 68, 68, 30));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(239, 68, 68));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                } else {
                    g2.setColor(new Color(239, 68, 68, 15));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.setColor(new Color(239, 68, 68, 60));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                }
                
                int cx = 18;
                int cy = getHeight() / 2;
                int r = 6;
                paintIcon(g2, "logout", new Color(239, 68, 68), cx, cy, r);
                
                g2.setFont(new Font("Poppins", Font.BOLD, 12));
                g2.setColor(new Color(239, 68, 68));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("Logout", 32, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btnLogoutHeader.setPreferredSize(new Dimension(95, 34));
        btnLogoutHeader.setOpaque(false);
        btnLogoutHeader.setContentAreaFilled(false);
        btnLogoutHeader.setBorderPainted(false);
        btnLogoutHeader.addActionListener(e -> {
            int pilihan = JOptionPane.showConfirmDialog(MainForm.this,
                    "Apakah Anda yakin ingin logout?",
                    "Konfirmasi Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (pilihan == JOptionPane.YES_OPTION) {
                new LoginForm().setVisible(true);
                dispose();
            }
        });

        right.add(lblRole);
        right.add(avatar);
        right.add(Box.createHorizontalStrut(8));
        right.add(btnLogoutHeader);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    private String getGreeting() {
        int h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (h < 12) return "Selamat Pagi";
        if (h < 15) return "Selamat Siang";
        if (h < 18) return "Selamat Sore";
        return "Selamat Malam";
    }

    // ═══════════════════════════════════════════════════════════════
    //  STATS PANEL
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildStatsPanel() {
        JPanel p = new JPanel(new GridLayout(1, 3, 20, 0));
        p.setOpaque(false);

        lblBarangVal = new JLabel("0 Item");
        lblCustomerVal = new JLabel("0 Orang");
        lblPendapatanVal = new JLabel("Rp 0");

        p.add(createStatCard("Total Produk", lblBarangVal, C_TEAL));
        p.add(createStatCard("Total Pelanggan", lblCustomerVal, C_PURPLE));
        p.add(createStatCard("Penjualan Hari Ini", lblPendapatanVal, C_EMERALD));

        return p;
    }

    private JPanel createStatCard(String title, JLabel lblValue, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Card background
                g2.setColor(C_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Subtle border
                g2.setColor(C_CARD_BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                // Left accent stripe
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(18, 22, 18, 18));
        card.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblTitle.setForeground(C_TEXT_DIM);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblValue.setFont(new Font("Poppins", Font.BOLD, 24));
        lblValue.setForeground(C_WHITE);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(lblValue);

        return card;
    }

    // ═══════════════════════════════════════════════════════════════
    //  FEATURE CARDS
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildFeatureCards() {
        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setOpaque(false);

        for (int i = 0; i < M_TITLE.length; i++) {
            if (isMenuAllowed(M_TITLE[i])) {
                grid.add(buildFeatureCard(M_TITLE[i], M_DESC[i], M_COLOR[i], M_ICON[i]));
            }
        }

        return grid;
    }

    private JPanel buildFeatureCard(String title, String desc, Color accent, String iconType) {
        final boolean allowed = isMenuAllowed(title);
        final Color clr = allowed ? accent : new Color(51, 65, 85);

        JPanel card = new JPanel() {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (allowed) {
                            hovered = true;
                            repaint();
                            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                        setCursor(Cursor.getDefaultCursor());
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (allowed) {
                            handleClick(title);
                        } else {
                            JOptionPane.showMessageDialog(MainForm.this,
                                    "Akses Ditolak!\nMenu '" + title + "' hanya dapat diakses oleh Admin.",
                                    "Akses Dibatasi", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Card background
                g2.setColor(C_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                // Border (accent on hover)
                Color borderClr = (hovered && allowed)
                        ? new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 120)
                        : C_CARD_BORDER;
                g2.setColor(borderClr);
                g2.setStroke(new BasicStroke(hovered && allowed ? 1.5f : 1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);

                // Icon area (rounded square with tinted accent bg)
                int ibg = 44, ix = 24, iy = 24;
                g2.setColor(new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), 20));
                g2.fillRoundRect(ix, iy, ibg, ibg, 12, 12);
                paintIcon(g2, iconType, clr, ix + ibg / 2, iy + ibg / 2, 10);

                // Title
                g2.setFont(new Font("Poppins", Font.BOLD, 14));
                g2.setColor(allowed ? C_WHITE : new Color(100, 116, 139));
                g2.drawString(title, ix, iy + ibg + 26);

                // Description
                g2.setFont(new Font("Poppins", Font.PLAIN, 12));
                g2.setColor(allowed ? C_TEXT_DIM : C_TEXT_MUTED);
                g2.drawString(allowed ? desc : desc + " (Admin)", ix, iy + ibg + 44);

                // Lock icon for restricted items
                if (!allowed) {
                    g2.setColor(C_TEXT_MUTED);
                    int lx = getWidth() - 32, ly = 28;
                    g2.fillRoundRect(lx, ly, 12, 10, 3, 3);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawArc(lx + 2, ly - 6, 8, 10, 0, 180);
                }
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(200, 140));
        return card;
    }

    // ═══════════════════════════════════════════════════════════════
    //  FOOTER
    // ═══════════════════════════════════════════════════════════════
    private JPanel buildFooter() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.CENTER));
        f.setBackground(C_CONTENT);
        f.setBorder(new EmptyBorder(4, 0, 8, 0));
        JLabel lbl = new JLabel("Toko Berkah Jaya  \u2022  Sistem Inventaris  \u2022  Pemrograman 2  \u2022  2025");
        lbl.setFont(new Font("Poppins", Font.PLAIN, 11));
        lbl.setForeground(C_TEXT_MUTED);
        f.add(lbl);
        return f;
    }

    // ═══════════════════════════════════════════════════════════════
    //  ICON PAINTER — draws simple vector icons at center (cx, cy)
    //  r = half-size radius of the icon
    // ═══════════════════════════════════════════════════════════════
    private void paintIcon(Graphics2D g2, String type, Color color, int cx, int cy, int r) {
        g2.setColor(color);

        switch (type) {
            case "folder": {
                // Folder body
                g2.fillRoundRect(cx - r, cy - r / 3, r * 2, r + r / 3, 3, 3);
                // Folder tab (top-left)
                g2.fillRoundRect(cx - r, cy - r + 2, (int) (r * 0.8), r / 2 + 1, 2, 2);
                break;
            }
            case "box": {
                // Box outline with cross (package tape)
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(cx - r + 1, cy - r + 1, r * 2 - 2, r * 2 - 2, 3, 3);
                g2.drawLine(cx - r + 1, cy, cx + r - 1, cy);
                g2.drawLine(cx, cy - r + 1, cx, cy + r - 1);
                break;
            }
            case "people": {
                // Main person (head + body)
                int hr = r / 3;
                g2.fillOval(cx - hr - 2, cy - r + 1, hr * 2, hr * 2);
                g2.fillArc(cx - r / 2 - 2, cy - r / 4, r, r * 3 / 4, 0, 180);
                // Second person behind (fainter)
                Color fade = new Color(color.getRed(), color.getGreen(), color.getBlue(), 130);
                g2.setColor(fade);
                g2.fillOval(cx + 2, cy - r + 2, hr * 2 - 1, hr * 2 - 1);
                g2.fillArc(cx, cy - r / 4 + 1, r - 2, r * 3 / 4 - 2, 0, 180);
                g2.setColor(color);
                break;
            }
            case "cart": {
                // Shopping cart body + wheels
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(cx - r, cy - r / 2, cx - r + 3, cy - r / 2);
                g2.drawPolyline(
                        new int[]{cx - r + 3, cx - r + 4, cx + r - 4, cx + r - 2},
                        new int[]{cy - r / 2, cy + r / 3, cy + r / 3, cy - r / 2 + 1}, 4);
                int wr = r / 4;
                g2.fillOval(cx - r / 2, cy + r / 2, wr * 2, wr * 2);
                g2.fillOval(cx + r / 4 - 1, cy + r / 2, wr * 2, wr * 2);
                break;
            }
            case "chart": {
                // Three ascending bars
                int bw = r * 2 / 5;
                int gap = r / 5;
                int x0 = cx - r + 1;
                g2.fillRoundRect(x0, cy + 1, bw, r - 1, 2, 2);
                g2.fillRoundRect(x0 + bw + gap, cy - r / 3, bw, r + r / 3, 2, 2);
                g2.fillRoundRect(x0 + (bw + gap) * 2, cy - r + 2, bw, r * 2 - 2, 2, 2);
                break;
            }
            case "logout": {
                // Door frame + exit arrow
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(cx - r + 1, cy - r + 2, r - 1, r * 2 - 4, 2, 2);
                g2.drawLine(cx, cy, cx + r - 1, cy);
                g2.drawLine(cx + r - 4, cy - 3, cx + r - 1, cy);
                g2.drawLine(cx + r - 4, cy + 3, cx + r - 1, cy);
                break;
            }
            case "key": {
                // Key icon
                g2.setStroke(new BasicStroke(1.8f));
                int headR = r * 10 / 20;
                int headY = cy - r + headR;
                g2.drawOval(cx - headR, headY - headR, headR * 2, headR * 2);
                g2.drawLine(cx, headY + headR, cx, cy + r - 1);
                g2.drawLine(cx, cy + r - 1, cx + r/2, cy + r - 1);
                g2.drawLine(cx, cy + r - r/2, cx + r/2, cy + r - r/2);
                break;
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  BUSINESS LOGIC (UNCHANGED)
    // ═══════════════════════════════════════════════════════════════
    private void handleClick(String menu) {
        switch (menu) {
            case "Data Kategori":
                new FormKategori(this, true).setVisible(true);
                refreshStats();
                break;
            case "Data Barang":
                new FormBarang(this, true).setVisible(true);
                refreshStats();
                break;
            case "Data Customer":
                new FormCustomer(this, true).setVisible(true);
                refreshStats();
                break;
            case "Transaksi":
                new FormTransaksi(this, true, idUser).setVisible(true);
                refreshStats();
                break;
            case "Laporan":
                new FormLaporan(this, true).setVisible(true);
                refreshStats();
                break;
            case "Data Petugas":
                new FormPetugas(this, true, idUser).setVisible(true);
                refreshStats();
                break;
            case "Logout":
                int pilihan = JOptionPane.showConfirmDialog(this,
                        "Apakah Anda yakin ingin logout?",
                        "Konfirmasi Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (pilihan == JOptionPane.YES_OPTION) {
                    new LoginForm().setVisible(true);
                    dispose();
                }
                break;
        }
    }

    // ─── Stats Refresh ───────────────────────────────────────────
    private void refreshStats() {
        int totalBarang = 0;
        int totalCustomer = 0;
        double totalPendapatan = 0;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            ResultSet rs1 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM tb_barang");
            if (rs1.next()) totalBarang = rs1.getInt(1);

            ResultSet rs2 = conn.createStatement().executeQuery("SELECT COUNT(*) FROM tb_customer");
            if (rs2.next()) totalCustomer = rs2.getInt(1);

            ResultSet rs3 = conn.createStatement().executeQuery(
                    "SELECT SUM(total_bayar) FROM tb_penjualan WHERE tgl_transaksi = CURDATE()");
            if (rs3.next()) totalPendapatan = rs3.getDouble(1);
        } catch (SQLException ex) {
            System.err.println("Gagal memuat statistik dashboard: " + ex.getMessage());
        } finally {
            Database.closeConnection(conn);
        }

        lblBarangVal.setText(totalBarang + " Item");
        lblCustomerVal.setText(totalCustomer + " Orang");
        lblPendapatanVal.setText("Rp " + new DecimalFormat("#,###").format(totalPendapatan));
    }
}
