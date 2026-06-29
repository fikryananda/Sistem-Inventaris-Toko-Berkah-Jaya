package sisteminventaris;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

/**
 * FormCustomer - Manajemen Data Customer/Pelanggan
 * Mata Kuliah : Pemrograman 2
 */
public class FormCustomer extends JDialog {

    // Komponen UI
    private JTextField  txtIdCustomer, txtNamaCustomer, txtTelepon;
    private JTextArea   txtAlamat;
    private JButton     btnTambah, btnEdit, btnHapus, btnBatal;
    private JTable      tblCustomer;
    private DefaultTableModel tableModel;
    private JTextField     txtCari;
    private TableRowSorter<DefaultTableModel> rowSorter;

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
    private static final Color C_GRAY         = new Color(24, 32, 47);    // Dark input bg

    // ─── Constructor ────────────────────────────────────────────────
    public FormCustomer(Frame parent, boolean modal) {
        super(parent, modal);
        initUI();
        loadData();
        txtIdCustomer.setText(generateNextId());
    }

    // ─── Inisialisasi UI ────────────────────────────────────────────
    private void initUI() {
        setTitle("Manajemen Data Customer");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 560); // Diperbesar tinggi awalnya
        setLocationRelativeTo(null);
        setResizable(true); // ✅ Supaya bisa maximize

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(C_BG);
        setContentPane(root);

        root.add(buildHeader(),     BorderLayout.NORTH);
        
        // ✅ JScrollPane untuk form input di kiri
        JScrollPane scrollForm = new JScrollPane(buildFormPanel());
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        scrollForm.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        root.add(scrollForm,  BorderLayout.WEST);
        root.add(buildTablePanel(), BorderLayout.CENTER);
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

                // Draw People vector icon
                int ix = 24, iy = 16;
                g2.setColor(C_ACCENT);
                // Head
                g2.fillOval(ix + 8, iy, 12, 12);
                // Body
                g2.fillArc(ix, iy + 10, 28, 20, 0, 180);
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(10, 68, 10, 25)); // margin left to 68px to clear icon

        JLabel lbl = new JLabel("Data Customer / Pelanggan");
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
        panel.setPreferredSize(new Dimension(255, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(18, 15, 18, 15));

        // ID Customer
        panel.add(makeLabel("ID Customer"));
        panel.add(Box.createVerticalStrut(3));
        txtIdCustomer = makeTextField("Otomatis");
        txtIdCustomer.setEditable(false);
        txtIdCustomer.setBackground(new Color(30, 41, 59));
        txtIdCustomer.setForeground(C_TEXT_DIM);
        panel.add(txtIdCustomer);
        panel.add(Box.createVerticalStrut(10));

        // Nama Customer
        panel.add(makeLabel("Nama Customer"));
        panel.add(Box.createVerticalStrut(3));
        txtNamaCustomer = makeTextField("Masukkan nama lengkap");
        panel.add(txtNamaCustomer);
        panel.add(Box.createVerticalStrut(10));

        // Alamat (JTextArea)
        panel.add(makeLabel("Alamat"));
        panel.add(Box.createVerticalStrut(3));
        txtAlamat = new JTextArea(3, 1);
        txtAlamat.setFont(new Font("Poppins", Font.PLAIN, 13));
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setBackground(new Color(24, 32, 47));
        txtAlamat.setForeground(Color.WHITE);
        txtAlamat.setCaretColor(Color.WHITE);
        txtAlamat.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                new EmptyBorder(5, 8, 5, 8)));
        txtAlamat.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtAlamat.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_ACCENT, 2),
                        new EmptyBorder(4, 7, 4, 7)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtAlamat.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_BORDER, 1),
                        new EmptyBorder(5, 8, 5, 8)
                ));
            }
        });
        txtAlamat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!txtAlamat.hasFocus()) {
                    txtAlamat.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(74, 85, 104), 1),
                            new EmptyBorder(5, 8, 5, 8)
                    ));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!txtAlamat.hasFocus()) {
                    txtAlamat.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(C_BORDER, 1),
                            new EmptyBorder(5, 8, 5, 8)
                    ));
                }
            }
        });
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        scrollAlamat.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        scrollAlamat.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollAlamat.setBorder(null);
        scrollAlamat.setOpaque(false);
        scrollAlamat.getViewport().setOpaque(false);
        panel.add(scrollAlamat);
        panel.add(Box.createVerticalStrut(10));

        // Telepon
        panel.add(makeLabel("No. Telepon"));
        panel.add(Box.createVerticalStrut(3));
        txtTelepon = makeTextField("Contoh: 08123456789");
        panel.add(txtTelepon);
        panel.add(Box.createVerticalStrut(22));

        // Tombol
        btnTambah = makeButton("Tambah",      C_ACCENT);
        btnEdit   = makeButton("Simpan Edit", C_SUCCESS);
        btnHapus  = makeButton("Hapus",       C_DANGER);
        btnBatal  = makeButton("Batal",       new Color(71, 85, 105));

        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);

        panel.add(btnTambah);
        panel.add(Box.createVerticalStrut(7));
        panel.add(btnEdit);
        panel.add(Box.createVerticalStrut(7));
        panel.add(btnHapus);
        panel.add(Box.createVerticalStrut(7));
        panel.add(btnBatal);

        // Event tombol
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
        panel.setBorder(new EmptyBorder(15, 5, 15, 15));

        tableModel = new DefaultTableModel(
                new String[]{"ID Customer", "Nama Customer", "Alamat", "No. Telepon"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        rowSorter = new TableRowSorter<>(tableModel);

        tblCustomer = new JTable(tableModel) {
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
        tblCustomer.setRowSorter(rowSorter);
        tblCustomer.setBackground(C_CARD);
        tblCustomer.setForeground(C_WHITE);
        tblCustomer.setFont(new Font("Poppins", Font.PLAIN, 13));
        tblCustomer.setRowHeight(32);
        tblCustomer.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 13));
        tblCustomer.getTableHeader().setBackground(C_SIDEBAR);
        tblCustomer.getTableHeader().setForeground(C_WHITE);
        tblCustomer.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tblCustomer.setSelectionBackground(new Color(59, 130, 246, 80));
        tblCustomer.setSelectionForeground(C_WHITE);
        tblCustomer.setGridColor(C_BORDER);

        // Lebar kolom
        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(90);
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblCustomer.getColumnModel().getColumn(2).setPreferredWidth(230);
        tblCustomer.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Panel Pencarian di atas tabel
        JPanel panelCari = new JPanel(new BorderLayout(8, 0));
        panelCari.setBackground(C_BG);
        panelCari.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblCari = new JLabel("Cari Customer: ");
        lblCari.setFont(new Font("Poppins", Font.BOLD, 12));
        lblCari.setForeground(C_WHITE);

        txtCari = makeTextField("Cari...");

        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filter(); }

            private void filter() {
                String text = txtCari.getText().trim();
                if (text.isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
                }
            }
        });

        panelCari.add(lblCari, BorderLayout.WEST);
        panelCari.add(txtCari, BorderLayout.CENTER);
        panel.add(panelCari, BorderLayout.NORTH);

        // Klik baris -> isi form
        tblCustomer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = tblCustomer.getSelectedRow();
                if (viewRow >= 0) {
                    int row = tblCustomer.convertRowIndexToModel(viewRow);
                    txtIdCustomer.setText(tableModel.getValueAt(row, 0).toString());
                    txtNamaCustomer.setText(tableModel.getValueAt(row, 1).toString());
                    txtAlamat.setText(tableModel.getValueAt(row, 2).toString());
                    txtTelepon.setText(tableModel.getValueAt(row, 3).toString());

                    txtIdCustomer.setEditable(false);
                    txtIdCustomer.setBackground(C_GRAY);
                    btnEdit.setEnabled(true);
                    btnHapus.setEnabled(true);
                    btnTambah.setEnabled(false);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblCustomer);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER));
        scroll.getViewport().setBackground(C_BG);
        scroll.setBackground(C_BG);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ─── Load Data ────────────────────────────────────────────────────
    private void loadData() {
        tableModel.setRowCount(0);
        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "SELECT * FROM tb_customer ORDER BY id_customer";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("id_customer"),
                    rs.getString("nama_customer"),
                    rs.getString("alamat"),
                    rs.getString("telepon")
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
        if (!validasiInput()) return;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "INSERT INTO tb_customer (id_customer, nama_customer, alamat, telepon) " +
                         "VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtIdCustomer.getText().trim().toUpperCase());
            ps.setString(2, txtNamaCustomer.getText().trim());
            ps.setString(3, txtAlamat.getText().trim());
            ps.setString(4, txtTelepon.getText().trim());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Customer berhasil ditambahkan!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Edit Data ────────────────────────────────────────────────────
    private void editData() {
        if (txtIdCustomer.getText().trim().equalsIgnoreCase("CUST000")) {
            warn("Template customer 'No Name' tidak dapat diubah!");
            return;
        }
        if (!validasiInput()) return;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "UPDATE tb_customer SET nama_customer=?, alamat=?, telepon=? " +
                         "WHERE id_customer=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtNamaCustomer.getText().trim());
            ps.setString(2, txtAlamat.getText().trim());
            ps.setString(3, txtTelepon.getText().trim());
            ps.setString(4, txtIdCustomer.getText().trim());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data customer berhasil diperbarui!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Hapus Data ───────────────────────────────────────────────────
    private void hapusData() {
        if (txtIdCustomer.getText().trim().equalsIgnoreCase("CUST000")) {
            warn("Template customer 'No Name' tidak dapat dihapus!");
            return;
        }
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus customer ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM tb_customer WHERE id_customer = ?");
            ps.setString(1, txtIdCustomer.getText().trim());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Customer berhasil dihapus!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadData();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(this, 
                        "Gagal menghapus customer!\nCustomer ini tidak bisa dihapus karena memiliki riwayat transaksi penjualan.\nHal ini diperlukan untuk menjaga laporan keuangan Anda.", 
                        "Error Integrasi Data", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Validasi Input ───────────────────────────────────────────────
    private boolean validasiInput() {
        if (txtIdCustomer.getText().trim().isEmpty()) {
            warn("ID Customer tidak boleh kosong!"); return false;
        }
        
        String nama = txtNamaCustomer.getText().trim();
        if (nama.isEmpty()) {
            warn("Nama customer tidak boleh kosong!"); return false;
        }
        if (!nama.matches("^[a-zA-Z\\s]+$")) {
            warn("Nama customer hanya boleh berisi huruf dan spasi!"); return false;
        }
        
        String telp = txtTelepon.getText().trim();
        if (telp.isEmpty()) {
            warn("No. telepon tidak boleh kosong!"); return false;
        }
        if (!telp.matches("^[0-9]+$")) {
            warn("No. telepon hanya boleh berisi angka!"); return false;
        }
        
        return true;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    // ─── Reset Form ───────────────────────────────────────────────────
    private void resetForm() {
        if (txtCari != null) txtCari.setText("");
        txtIdCustomer.setText(generateNextId());
        txtNamaCustomer.setText("");
        txtAlamat.setText("");
        txtTelepon.setText("");
        txtIdCustomer.setEditable(false);
        txtIdCustomer.setBackground(C_GRAY);
        btnTambah.setEnabled(true);
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
        tblCustomer.clearSelection();
        txtNamaCustomer.requestFocus();
    }

    // ─── Auto-Generate ID ─────────────────────────────────────────────
    private String generateNextId() {
        Connection conn = null;
        String nextId = "CUST001";
        try {
            conn = Database.getConnection();
            String sql = "SELECT id_customer FROM tb_customer ORDER BY id_customer DESC LIMIT 1";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                String lastId = rs.getString("id_customer");
                if (lastId != null && lastId.startsWith("CUST")) {
                    try {
                        int num = Integer.parseInt(lastId.substring(4));
                        nextId = String.format("CUST%03d", num + 1);
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Gagal generate ID Customer: " + ex.getMessage());
        } finally {
            Database.closeConnection(conn);
        }
        return nextId;
    }

    // ─── Helper ───────────────────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Poppins", Font.BOLD, 12));
        lbl.setForeground(C_TEXT_LIGHT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField makeTextField(String hint) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Poppins", Font.PLAIN, 13));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBackground(C_GRAY);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                new EmptyBorder(5, 9, 5, 9)));
        tf.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_ACCENT, 2),
                        new EmptyBorder(4, 8, 4, 8)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                tf.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_BORDER, 1),
                        new EmptyBorder(5, 9, 5, 9)
                ));
            }
        });
        tf.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!tf.hasFocus()) {
                    tf.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(74, 85, 104), 1),
                            new EmptyBorder(5, 9, 5, 9)
                    ));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (!tf.hasFocus()) {
                    tf.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(C_BORDER, 1),
                            new EmptyBorder(5, 9, 5, 9)
                    ));
                }
            }
        });
        return tf;
    }

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
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
