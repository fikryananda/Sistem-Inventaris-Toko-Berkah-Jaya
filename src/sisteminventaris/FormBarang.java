package sisteminventaris;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

/**
 * FormBarang - Manajemen Data Barang
 * Mata Kuliah : Pemrograman 2
 */
public class FormBarang extends JDialog {

    // Komponen UI
    private JTextField     txtIdBarang, txtNamaBarang, txtSatuan, txtHargaJual, txtStok;
    private JComboBox<String> cmbKategori;
    private JButton        btnTambah, btnEdit, btnHapus, btnBatal, btnTambahKategori;
    private JTable         tblBarang;
    private DefaultTableModel tableModel;
    private JTextField     txtCari;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Simpan id_kategori dari combobox
    private int[] idKategoriList;

    // Format angka
    private static final DecimalFormat FMT = new DecimalFormat("#,###");

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
    public FormBarang(Frame parent, boolean modal) {
        super(parent, modal);
        initUI();
        loadKategori();
        loadData();
        txtIdBarang.setText(generateNextId());
    }

    // ─── Inisialisasi UI ────────────────────────────────────────────
    private void initUI() {
        setTitle("Manajemen Data Barang");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(950, 580); // Diperbesar tinggi awalnya
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

                // Draw Box vector icon
                int ix = 24, iy = 17, w = 26, h = 26;
                g2.setColor(C_ACCENT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(ix, iy, w, h, 4, 4);
                // Tape horizontal line
                g2.drawLine(ix, iy + h/2, ix + w, iy + h/2);
                // Tape vertical line
                g2.drawLine(ix + w/2, iy, ix + w/2, iy + h);
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(10, 68, 10, 25)); // margin left to 68px to clear icon

        JLabel lbl = new JLabel("Data Barang");
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
        panel.setPreferredSize(new Dimension(260, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(18, 15, 18, 15));

        // ID Barang
        panel.add(makeLabel("ID Barang"));
        panel.add(Box.createVerticalStrut(3));
        txtIdBarang = makeTextField("Otomatis");
        txtIdBarang.setEditable(false);
        txtIdBarang.setBackground(new Color(30, 41, 59));
        txtIdBarang.setForeground(C_TEXT_DIM);
        panel.add(txtIdBarang);
        panel.add(Box.createVerticalStrut(10));

        // Nama Barang
        panel.add(makeLabel("Nama Barang"));
        panel.add(Box.createVerticalStrut(3));
        txtNamaBarang = makeTextField("Masukkan nama barang");
        panel.add(txtNamaBarang);
        panel.add(Box.createVerticalStrut(10));

        // Kategori (ComboBox + Tombol "+")
        panel.add(makeLabel("Kategori"));
        panel.add(Box.createVerticalStrut(3));
        cmbKategori = new JComboBox<String>();
        cmbKategori.setFont(new Font("Poppins", Font.PLAIN, 13));
        cmbKategori.setBackground(new Color(24, 32, 47));
        cmbKategori.setForeground(Color.WHITE);
        cmbKategori.setBorder(BorderFactory.createLineBorder(C_BORDER));
        cmbKategori.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? C_ACCENT : new Color(24, 32, 47));
                setForeground(Color.WHITE);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });

        // Tombol "+" untuk tambah kategori baru
        btnTambahKategori = new JButton() {
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
                Color paintBg = C_SUCCESS;
                if (getModel().isPressed()) {
                    paintBg = C_SUCCESS.darker();
                } else if (hovered) {
                    paintBg = new Color(Math.min(C_SUCCESS.getRed() + 30, 255), Math.min(C_SUCCESS.getGreen() + 30, 255), Math.min(C_SUCCESS.getBlue() + 30, 255));
                }
                g2.setColor(paintBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Draw "+" icon manually
                g2.setColor(C_WHITE);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int len = 7; // half-length of the plus arms
                g2.drawLine(cx - len, cy, cx + len, cy); // horizontal
                g2.drawLine(cx, cy - len, cx, cy + len); // vertical
            }
        };
        btnTambahKategori.setPreferredSize(new Dimension(36, 36));
        btnTambahKategori.setMinimumSize(new Dimension(36, 36));
        btnTambahKategori.setMaximumSize(new Dimension(36, 36));
        btnTambahKategori.setOpaque(false);
        btnTambahKategori.setContentAreaFilled(false);
        btnTambahKategori.setBorderPainted(false);
        btnTambahKategori.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnTambahKategori.setToolTipText("Tambah Kategori Baru");
        btnTambahKategori.addActionListener(e -> tambahKategoriBaru());

        // Panel horizontal: ComboBox + Tombol "+"
        JPanel kategoriPanel = new JPanel(new BorderLayout(5, 0));
        kategoriPanel.setBackground(C_SIDEBAR);
        kategoriPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        kategoriPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        kategoriPanel.add(cmbKategori, BorderLayout.CENTER);
        kategoriPanel.add(btnTambahKategori, BorderLayout.EAST);
        panel.add(kategoriPanel);
        panel.add(Box.createVerticalStrut(10));

        // Satuan
        panel.add(makeLabel("Satuan"));
        panel.add(Box.createVerticalStrut(3));
        txtSatuan = makeTextField("Contoh: Unit, Kg, Dus");
        panel.add(txtSatuan);
        panel.add(Box.createVerticalStrut(10));

        // Harga Jual
        panel.add(makeLabel("Harga Jual (Rp)"));
        panel.add(Box.createVerticalStrut(3));
        txtHargaJual = makeTextField("Contoh: 15000");
        panel.add(txtHargaJual);
        panel.add(Box.createVerticalStrut(10));

        // Stok
        panel.add(makeLabel("Stok"));
        panel.add(Box.createVerticalStrut(3));
        txtStok = makeTextField("Contoh: 50");
        panel.add(txtStok);
        panel.add(Box.createVerticalStrut(20));

        // Tombol
        btnTambah = makeButton("Tambah",     C_ACCENT);
        btnEdit   = makeButton("Simpan Edit", C_SUCCESS);
        btnHapus  = makeButton("Hapus",      C_DANGER);
        btnBatal  = makeButton("Batal",      new Color(71, 85, 105));

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
                new String[]{"ID Barang", "Nama Barang", "Kategori", "Satuan", "Harga Jual", "Stok"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        rowSorter = new TableRowSorter<>(tableModel);

        tblBarang = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                
                int modelRow = convertRowIndexToModel(row);
                int stok = (int) tableModel.getValueAt(modelRow, 5);
                
                if (stok <= 5) {
                    if (!isRowSelected(row)) {
                        c.setBackground(new Color(153, 27, 27, 85)); // Soft dark red for dark mode
                        c.setForeground(new Color(254, 226, 226));    // Soft light red text
                    } else {
                        c.setForeground(Color.WHITE);
                    }
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    if (!isRowSelected(row)) {
                        c.setBackground(row % 2 == 0 ? C_CARD : new Color(30, 41, 59));
                        c.setForeground(C_WHITE);
                    } else {
                        c.setForeground(Color.WHITE);
                    }
                }
                return c;
            }
        };
        tblBarang.setRowSorter(rowSorter);
        tblBarang.setBackground(C_CARD);
        tblBarang.setForeground(C_WHITE);
        tblBarang.setFont(new Font("Poppins", Font.PLAIN, 12));
        tblBarang.setRowHeight(30);
        tblBarang.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 12));
        tblBarang.getTableHeader().setBackground(C_SIDEBAR);
        tblBarang.getTableHeader().setForeground(C_WHITE);
        tblBarang.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tblBarang.setSelectionBackground(new Color(59, 130, 246, 80));
        tblBarang.setSelectionForeground(C_WHITE);
        tblBarang.setGridColor(C_BORDER);

        // Lebar kolom
        tblBarang.getColumnModel().getColumn(0).setPreferredWidth(75);
        tblBarang.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblBarang.getColumnModel().getColumn(2).setPreferredWidth(110);
        tblBarang.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblBarang.getColumnModel().getColumn(4).setPreferredWidth(100);
        tblBarang.getColumnModel().getColumn(5).setPreferredWidth(50);

        // Panel Pencarian di atas tabel
        JPanel panelCari = new JPanel(new BorderLayout(8, 0));
        panelCari.setBackground(C_BG);
        panelCari.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblCari = new JLabel("Cari Barang: ");
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

        // Klik baris tabel -> isi form
        tblBarang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = tblBarang.getSelectedRow();
                if (viewRow >= 0) {
                    // Konversi view index ke model index agar benar saat ada pencarian/sorting
                    int modelRow = tblBarang.convertRowIndexToModel(viewRow);

                    txtIdBarang.setText(tableModel.getValueAt(modelRow, 0).toString());
                    txtNamaBarang.setText(tableModel.getValueAt(modelRow, 1).toString());
                    txtSatuan.setText(tableModel.getValueAt(modelRow, 3).toString());
                    txtHargaJual.setText(tableModel.getValueAt(modelRow, 4).toString().replace(",", ""));
                    txtStok.setText(tableModel.getValueAt(modelRow, 5).toString());

                    // Set combobox kategori
                    String namaKat = tableModel.getValueAt(modelRow, 2).toString();
                    for (int i = 0; i < cmbKategori.getItemCount(); i++) {
                        if (cmbKategori.getItemAt(i).equals(namaKat)) {
                            cmbKategori.setSelectedIndex(i);
                            break;
                        }
                    }

                    txtIdBarang.setEditable(false);
                    txtIdBarang.setBackground(C_GRAY);
                    btnEdit.setEnabled(true);
                    btnHapus.setEnabled(true);
                    btnTambah.setEnabled(false);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblBarang);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER));
        scroll.getViewport().setBackground(C_BG);
        scroll.setBackground(C_BG);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ─── Load Kategori ke ComboBox ───────────────────────────────────
    private void loadKategori() {
        cmbKategori.removeAllItems();
        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "SELECT * FROM tb_kategori ORDER BY CASE WHEN nama_kategori = 'Lainnya' THEN 1 ELSE 0 END, id_kategori";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            java.util.List<Integer> ids = new java.util.ArrayList<Integer>();
            while (rs.next()) {
                ids.add(rs.getInt("id_kategori"));
                cmbKategori.addItem(rs.getString("nama_kategori"));
            }
            idKategoriList = new int[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                idKategoriList[i] = ids.get(i);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat kategori: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Load Data Barang ────────────────────────────────────────────
    private void loadData() {
        tableModel.setRowCount(0);
        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "SELECT b.id_barang, b.nama_barang, k.nama_kategori, " +
                         "b.satuan, b.harga_jual, b.stok " +
                         "FROM tb_barang b " +
                         "JOIN tb_kategori k ON b.id_kategori = k.id_kategori " +
                         "ORDER BY b.id_barang";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    rs.getString("nama_kategori"),
                    rs.getString("satuan"),
                    FMT.format(rs.getDouble("harga_jual")),
                    rs.getInt("stok")
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
            String sql = "INSERT INTO tb_barang (id_barang, id_kategori, nama_barang, satuan, harga_jual, stok) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtIdBarang.getText().trim().toUpperCase());
            ps.setInt(2, idKategoriList[cmbKategori.getSelectedIndex()]);
            ps.setString(3, txtNamaBarang.getText().trim());
            ps.setString(4, txtSatuan.getText().trim());
            ps.setDouble(5, Double.parseDouble(txtHargaJual.getText().trim()));
            ps.setInt(6, Integer.parseInt(txtStok.getText().trim()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Barang berhasil ditambahkan!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Edit Data ────────────────────────────────────────────────────
    private void editData() {
        if (!validasiInput()) return;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "UPDATE tb_barang SET id_kategori=?, nama_barang=?, satuan=?, harga_jual=?, stok=? " +
                         "WHERE id_barang=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idKategoriList[cmbKategori.getSelectedIndex()]);
            ps.setString(2, txtNamaBarang.getText().trim());
            ps.setString(3, txtSatuan.getText().trim());
            ps.setDouble(4, Double.parseDouble(txtHargaJual.getText().trim()));
            ps.setInt(5, Integer.parseInt(txtStok.getText().trim()));
            ps.setString(6, txtIdBarang.getText().trim());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data barang berhasil diperbarui!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Hapus Data ───────────────────────────────────────────────────
    private void hapusData() {
        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus barang ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_barang WHERE id_barang = ?");
            ps.setString(1, txtIdBarang.getText().trim());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Barang berhasil dihapus!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadData();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(this, 
                        "Gagal menghapus barang!\nBarang ini tidak bisa dihapus karena sudah pernah digunakan dalam transaksi penjualan.\nHal ini diperlukan untuk menjaga riwayat laporan transaksi Anda.", 
                        "Error Integrasi Data", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Validasi Input ───────────────────────────────────────────────
    private boolean validasiInput() {
        if (txtIdBarang.getText().trim().isEmpty()) {
            warn("ID Barang tidak boleh kosong!"); return false;
        }
        if (txtNamaBarang.getText().trim().isEmpty()) {
            warn("Nama barang tidak boleh kosong!"); return false;
        }
        if (txtSatuan.getText().trim().isEmpty()) {
            warn("Satuan tidak boleh kosong!"); return false;
        }
        try { Double.parseDouble(txtHargaJual.getText().trim()); }
        catch (NumberFormatException ex) { warn("Harga jual harus berupa angka!"); return false; }
        try { Integer.parseInt(txtStok.getText().trim()); }
        catch (NumberFormatException ex) { warn("Stok harus berupa angka bulat!"); return false; }
        return true;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    // ─── Reset Form ───────────────────────────────────────────────────
    private void resetForm() {
        if (txtCari != null) txtCari.setText("");
        txtIdBarang.setText(generateNextId());
        txtNamaBarang.setText("");
        txtSatuan.setText("");
        txtHargaJual.setText("");
        txtStok.setText("");
        cmbKategori.setSelectedIndex(0);
        txtIdBarang.setEditable(false);
        txtIdBarang.setBackground(C_GRAY);
        btnTambah.setEnabled(true);
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
        tblBarang.clearSelection();
        txtNamaBarang.requestFocus();
    }

    // ─── Tambah Kategori Baru (dari tombol "+") ──────────────────────
    private void tambahKategoriBaru() {
        // Panel custom untuk input dialog agar sesuai tema
        String namaKategori = JOptionPane.showInputDialog(this,
                "Masukkan nama kategori baru:",
                "Tambah Kategori Baru",
                JOptionPane.PLAIN_MESSAGE);

        if (namaKategori == null) return; // User cancel
        namaKategori = namaKategori.trim();
        if (namaKategori.isEmpty()) {
            warn("Nama kategori tidak boleh kosong!");
            return;
        }

        Connection conn = null;
        try {
            conn = Database.getConnection();
            // Cek duplikat
            PreparedStatement psCheck = conn.prepareStatement(
                    "SELECT COUNT(*) FROM tb_kategori WHERE LOWER(nama_kategori) = LOWER(?)");
            psCheck.setString(1, namaKategori);
            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                warn("Kategori '" + namaKategori + "' sudah ada!");
                return;
            }

            // Insert kategori baru
            PreparedStatement ps = conn.prepareStatement("INSERT INTO tb_kategori (nama_kategori) VALUES (?)");
            ps.setString(1, namaKategori);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Kategori '" + namaKategori + "' berhasil ditambahkan!",
                    "Berhasil", JOptionPane.INFORMATION_MESSAGE);

            // Reload ComboBox & select kategori baru
            loadKategori();
            for (int i = 0; i < cmbKategori.getItemCount(); i++) {
                if (cmbKategori.getItemAt(i).equalsIgnoreCase(namaKategori)) {
                    cmbKategori.setSelectedIndex(i);
                    break;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menambahkan kategori: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Auto-Generate ID ─────────────────────────────────────────────
    private String generateNextId() {
        Connection conn = null;
        String nextId = "BRG001";
        try {
            conn = Database.getConnection();
            String sql = "SELECT id_barang FROM tb_barang ORDER BY id_barang DESC LIMIT 1";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                String lastId = rs.getString("id_barang");
                if (lastId != null && lastId.startsWith("BRG")) {
                    try {
                        int num = Integer.parseInt(lastId.substring(3));
                        nextId = String.format("BRG%03d", num + 1);
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Gagal generate ID Barang: " + ex.getMessage());
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
