package sisteminventaris;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * FormKategori - Manajemen Data Kategori Barang
 * Mata Kuliah : Pemrograman 2
 */
public class FormKategori extends JDialog {

    // Komponen UI
    private JTextField     txtIdKategori;
    private JTextField     txtNamaKategori;
    private JButton        btnTambah, btnEdit, btnHapus, btnBatal;
    private JTable         tblKategori;
    private DefaultTableModel tableModel;

    // Warna tema Slate Dark
    private static final Color C_BG           = new Color(30, 41, 59);   // Slate 800
    private static final Color C_SIDEBAR      = new Color(15, 23, 42);   // Slate 900
    private static final Color C_CARD         = new Color(36, 48, 68);   // Slate 750 / Card background
    private static final Color C_BORDER       = new Color(50, 64, 86);   // Slate 700 / Border
    private static final Color C_WHITE        = Color.WHITE;
    private static final Color C_TEXT_LIGHT   = new Color(203, 213, 225); // Slate 300
    private static final Color C_TEXT_DIM     = new Color(148, 163, 184); // Slate 400
    private static final Color C_ACCENT       = new Color(59, 130, 246);  // Blue 500
    private static final Color C_SUCCESS      = new Color(16, 185, 129);  // Emerald 500
    private static final Color C_DANGER       = new Color(239, 68, 68);   // Red 500
    private static final Color C_TEXT         = Color.WHITE;

    // ─── Constructor ────────────────────────────────────────────────
    public FormKategori(Frame parent, boolean modal) {
        super(parent, modal);
        initUI();
        loadData();
    }

    // ─── Inisialisasi UI ────────────────────────────────────────────
    private void initUI() {
        setTitle("Manajemen Data Kategori");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(700, 560); // Diperbesar tinggi awalnya
        setLocationRelativeTo(null);
        setResizable(true); // ✅ Supaya bisa maximize

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(C_BG);
        setContentPane(root);

        root.add(buildHeader(),    BorderLayout.NORTH);
        
        // ✅ JScrollPane untuk form input di kiri
        JScrollPane scrollForm = new JScrollPane(buildFormPanel());
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        scrollForm.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        root.add(scrollForm, BorderLayout.WEST);
        root.add(buildTablePanel(),BorderLayout.CENTER);
    }

    // ─── Header ─────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // Slate dark gradient
                GradientPaint gp = new GradientPaint(0, 0, C_SIDEBAR, getWidth(), 0, C_BG);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Accent bottom border
                g2.setColor(C_ACCENT);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);

                // Draw folder vector icon
                int ix = 24, iy = 16, w = 28, h = 20;
                g2.setColor(C_ACCENT);
                g2.fillRoundRect(ix, iy, 12, 6, 2, 2); // Tab
                g2.fillRoundRect(ix, iy + 4, w, h, 4, 4); // Main folder body
                g2.setColor(new Color(Math.min(C_ACCENT.getRed() + 40, 255), Math.min(C_ACCENT.getGreen() + 40, 255), Math.min(C_ACCENT.getBlue() + 40, 255)));
                g2.fillRoundRect(ix + 2, iy + 7, w - 4, h - 5, 2, 2); // flap
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(10, 68, 10, 25)); // margin left to 68px to clear icon

        JLabel lbl = new JLabel("Data Kategori Barang");
        lbl.setFont(new Font("Poppins", Font.BOLD, 18));
        lbl.setForeground(C_WHITE);
        header.add(lbl, BorderLayout.WEST);

        JLabel lblSub = new JLabel("Toko Berkah Jaya");
        lblSub.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblSub.setForeground(C_TEXT_DIM);
        header.add(lblSub, BorderLayout.EAST);

        return header;
    }

    // ─── Panel Form Input ────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(C_SIDEBAR);
        panel.setPreferredSize(new Dimension(230, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 15, 20, 15));

        // ID Kategori (read-only, auto)
        panel.add(makeLabel("ID Kategori"));
        panel.add(Box.createVerticalStrut(4));
        txtIdKategori = makeTextField("Otomatis");
        txtIdKategori.setEditable(false);
        txtIdKategori.setBackground(new Color(30, 41, 59));
        txtIdKategori.setForeground(C_TEXT_DIM);
        panel.add(txtIdKategori);

        panel.add(Box.createVerticalStrut(14));

        // Nama Kategori
        panel.add(makeLabel("Nama Kategori"));
        panel.add(Box.createVerticalStrut(4));
        txtNamaKategori = makeTextField("Masukkan nama kategori");
        panel.add(txtNamaKategori);

        panel.add(Box.createVerticalStrut(24));

        // Tombol-tombol
        btnTambah = makeButton("Tambah",  C_ACCENT);
        btnEdit   = makeButton("Simpan Edit", C_SUCCESS);
        btnHapus  = makeButton("Hapus",   C_DANGER);
        btnBatal  = makeButton("Batal",   new Color(71, 85, 105));

        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);

        panel.add(btnTambah);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnEdit);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnHapus);
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnBatal);

        // ─── Event Tombol ────────────────────────────────────────────
        btnTambah.addActionListener(e -> tambahData());
        btnEdit.addActionListener(e -> editData());
        btnHapus.addActionListener(e -> hapusData());
        btnBatal.addActionListener(e -> resetForm());

        return panel;
    }

    // ─── Panel Tabel ─────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(C_BG);
        panel.setBorder(new EmptyBorder(15, 10, 15, 15));

        // Model tabel
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Nama Kategori"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // tabel tidak bisa diedit langsung
            }
        };

        tblKategori = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? C_CARD : new Color(30, 41, 59));
                    c.setForeground(C_WHITE);
                } else {
                    c.setForeground(C_WHITE);
                }
                return c;
            }
        };
        tblKategori.setBackground(C_CARD);
        tblKategori.setForeground(C_WHITE);
        tblKategori.setFont(new Font("Poppins", Font.PLAIN, 13));
        tblKategori.setRowHeight(32);
        tblKategori.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 13));
        tblKategori.getTableHeader().setBackground(C_SIDEBAR);
        tblKategori.getTableHeader().setForeground(C_WHITE);
        tblKategori.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tblKategori.setSelectionBackground(new Color(59, 130, 246, 80));
        tblKategori.setSelectionForeground(C_WHITE);
        tblKategori.setGridColor(C_BORDER);

        // Lebar kolom
        tblKategori.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblKategori.getColumnModel().getColumn(1).setPreferredWidth(240);

        // Klik baris tabel → isi form
        tblKategori.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblKategori.getSelectedRow();
                if (row >= 0) {
                    txtIdKategori.setText(tableModel.getValueAt(row, 0).toString());
                    txtNamaKategori.setText(tableModel.getValueAt(row, 1).toString());
                    btnEdit.setEnabled(true);
                    btnHapus.setEnabled(true);
                    btnTambah.setEnabled(false);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblKategori);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER));
        scroll.getViewport().setBackground(C_BG);
        scroll.setBackground(C_BG);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ─── Load Data dari DB ───────────────────────────────────────────
    private void loadData() {
        tableModel.setRowCount(0); // kosongkan tabel dulu
        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "SELECT * FROM tb_kategori ORDER BY id_kategori";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id_kategori"),
                    rs.getString("nama_kategori")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Tambah Data ─────────────────────────────────────────────────
    private void tambahData() {
        String nama = txtNamaKategori.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nama kategori tidak boleh kosong!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            txtNamaKategori.requestFocus();
            return;
        }

        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "INSERT INTO tb_kategori (nama_kategori) VALUES (?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nama);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Kategori berhasil ditambahkan!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menambahkan data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Edit Data ────────────────────────────────────────────────────
    private void editData() {
        String id   = txtIdKategori.getText().trim();
        String nama = txtNamaKategori.getText().trim();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nama kategori tidak boleh kosong!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "UPDATE tb_kategori SET nama_kategori = ? WHERE id_kategori = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nama);
            ps.setInt(2, Integer.parseInt(id));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Kategori berhasil diperbarui!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memperbarui data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Hapus Data ───────────────────────────────────────────────────
    private void hapusData() {
        String id = txtIdKategori.getText().trim();

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus kategori ini?\n(Barang yang terkait mungkin ikut terpengaruh)",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (konfirmasi != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "DELETE FROM tb_kategori WHERE id_kategori = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Kategori berhasil dihapus!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadData();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(this, 
                        "Gagal menghapus kategori!\nKategori ini tidak bisa dihapus karena masih terhubung dengan data barang.\nSilakan hapus atau ubah kategori barang terkait terlebih dahulu.", 
                        "Error Integrasi Data", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus data: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Reset Form ───────────────────────────────────────────────────
    private void resetForm() {
        txtIdKategori.setText("");
        txtNamaKategori.setText("");
        btnTambah.setEnabled(true);
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
        tblKategori.clearSelection();
        txtNamaKategori.requestFocus();
    }

    // ─── Helper: Label ────────────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Poppins", Font.BOLD, 12));
        lbl.setForeground(C_TEXT_LIGHT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // ─── Helper: TextField ────────────────────────────────────────────
    private JTextField makeTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Poppins", Font.PLAIN, 13));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBackground(new Color(24, 32, 47));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER, 1),
                new EmptyBorder(6, 10, 6, 10)
        ));
        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_ACCENT, 2),
                        new EmptyBorder(5, 9, 5, 9)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_BORDER, 1),
                        new EmptyBorder(6, 10, 6, 10)
                ));
            }
        });
        tf.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!tf.hasFocus()) {
                    tf.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(74, 85, 104), 1),
                            new EmptyBorder(6, 10, 6, 10)
                    ));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!tf.hasFocus()) {
                    tf.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(C_BORDER, 1),
                            new EmptyBorder(6, 10, 6, 10)
                    ));
                }
            }
        });
        return tf;
    }

    // ─── Helper: Button ───────────────────────────────────────────────
    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color paintBg = bg;
                if (!isEnabled()) {
                    paintBg = new Color(51, 65, 85);
                } else if (getModel().isPressed()) {
                    paintBg = bg.darker();
                } else if (hovered) {
                    paintBg = new Color(Math.min(bg.getRed() + 20, 255), Math.min(bg.getGreen() + 20, 255), Math.min(bg.getBlue() + 20, 255));
                }
                g2.setColor(paintBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Poppins", Font.BOLD, 12));
        btn.setForeground(C_WHITE);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
