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
 * FormPetugas - Manajemen Data Kasir / Petugas
 * Mata Kuliah : Pemrograman 2
 */
public class FormPetugas extends JDialog {

    private final int currentLoggedInId;
    private boolean passwordManuallyEdited = false;

    // Komponen UI
    private JTextField     txtIdUser, txtNamaLengkap, txtUsername, txtPassword;
    private JComboBox<String> cmbLevel;
    private JButton        btnTambah, btnEdit, btnHapus, btnBatal;
    private JTable         tblPetugas;
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
    private static final Color C_PINK         = new Color(236, 72, 153);  // Pink 500
    private static final Color C_GRAY         = new Color(24, 32, 47);    // Dark input bg

    public FormPetugas(Frame parent, boolean modal, int currentLoggedInId) {
        super(parent, modal);
        this.currentLoggedInId = currentLoggedInId;
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("Manajemen Data Petugas / Kasir");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(850, 560);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(C_BG);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        JScrollPane scrollForm = new JScrollPane(buildFormPanel());
        scrollForm.setBorder(null);
        scrollForm.getVerticalScrollBar().setUnitIncrement(16);
        scrollForm.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        root.add(scrollForm, BorderLayout.WEST);
        root.add(buildTablePanel(), BorderLayout.CENTER);
    }

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
                g2.setColor(C_PINK);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);

                // Draw Key vector icon
                int ix = 24, iy = 15, r = 13;
                g2.setColor(C_PINK);
                g2.setStroke(new BasicStroke(2f));
                int headR = r * 10 / 20;
                int headY = iy + r/2;
                g2.drawOval(ix + 12 - headR, headY - headR, headR * 2, headR * 2);
                g2.drawLine(ix + 12, headY + headR, ix + 12, iy + r * 2 - 2);
                g2.drawLine(ix + 12, iy + r * 2 - 2, ix + 18, iy + r * 2 - 2);
                g2.drawLine(ix + 12, iy + r * 2 - 7, ix + 18, iy + r * 2 - 7);
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(10, 68, 10, 25)); // margin left to 68px to clear icon

        JLabel lbl = new JLabel("Data Petugas & Kasir");
        lbl.setFont(new Font("Poppins", Font.BOLD, 18));
        lbl.setForeground(C_WHITE);
        header.add(lbl, BorderLayout.WEST);

        JLabel lblSub = new JLabel("Toko Berkah Jaya");
        lblSub.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblSub.setForeground(C_TEXT_DIM);
        header.add(lblSub, BorderLayout.EAST);

        return header;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(C_SIDEBAR);
        panel.setPreferredSize(new Dimension(255, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(18, 15, 18, 15));

        // ID User
        panel.add(makeLabel("ID User"));
        panel.add(Box.createVerticalStrut(3));
        txtIdUser = makeTextField("Otomatis");
        txtIdUser.setEditable(false);
        txtIdUser.setBackground(C_BG);
        txtIdUser.setForeground(C_TEXT_DIM);
        panel.add(txtIdUser);
        panel.add(Box.createVerticalStrut(10));

        // Nama Lengkap
        panel.add(makeLabel("Nama Lengkap"));
        panel.add(Box.createVerticalStrut(3));
        txtNamaLengkap = makeTextField("Masukkan nama lengkap");
        panel.add(txtNamaLengkap);
        panel.add(Box.createVerticalStrut(10));

        // Username
        panel.add(makeLabel("Username"));
        panel.add(Box.createVerticalStrut(3));
        txtUsername = makeTextField("Masukkan username");
        panel.add(txtUsername);
        panel.add(Box.createVerticalStrut(10));

        // Password
        panel.add(makeLabel("Password"));
        panel.add(Box.createVerticalStrut(3));
        txtPassword = makeTextField("Otomatis: [username]123");
        txtPassword.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { checkManual(); }
            @Override public void removeUpdate(DocumentEvent e) { checkManual(); }
            @Override public void changedUpdate(DocumentEvent e) { checkManual(); }
            private void checkManual() {
                if (txtPassword.hasFocus()) {
                    passwordManuallyEdited = true;
                }
            }
        });
        panel.add(txtPassword);
        panel.add(Box.createVerticalStrut(10));

        // Level
        panel.add(makeLabel("Hak Akses / Level"));
        panel.add(Box.createVerticalStrut(3));
        cmbLevel = new JComboBox<>(new String[]{"Petugas", "Admin"});
        cmbLevel.setFont(new Font("Poppins", Font.PLAIN, 13));
        cmbLevel.setBackground(C_GRAY);
        cmbLevel.setForeground(C_WHITE);
        cmbLevel.setBorder(BorderFactory.createLineBorder(C_BORDER));
        cmbLevel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbLevel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbLevel.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? C_PINK : C_GRAY);
                setForeground(Color.WHITE);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        panel.add(cmbLevel);
        panel.add(Box.createVerticalStrut(20));

        // Tombol
        btnTambah = makeButton("Tambah",      C_PINK);
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

        // ─── Document Listener untuk password otomatis ───
        txtUsername.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updatePass(); }
            @Override public void removeUpdate(DocumentEvent e) { updatePass(); }
            @Override public void changedUpdate(DocumentEvent e) { updatePass(); }
            
            private void updatePass() {
                if (btnTambah.isEnabled() && !passwordManuallyEdited) {
                    String userText = txtUsername.getText().trim();
                    if (!userText.isEmpty()) {
                        txtPassword.setText(userText.toLowerCase() + "123");
                    } else {
                        txtPassword.setText("");
                    }
                }
            }
        });

        // Event tombol
        btnTambah.addActionListener(e -> tambahData());
        btnEdit.addActionListener(e -> editData());
        btnHapus.addActionListener(e -> hapusData());
        btnBatal.addActionListener(e -> resetForm());

        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(C_BG);
        panel.setBorder(new EmptyBorder(15, 5, 15, 15));

        tableModel = new DefaultTableModel(
                new String[]{"ID User", "Nama Lengkap", "Username", "Password", "Level"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        rowSorter = new TableRowSorter<>(tableModel);

        tblPetugas = new JTable(tableModel) {
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
        tblPetugas.setRowSorter(rowSorter);
        tblPetugas.setBackground(C_CARD);
        tblPetugas.setForeground(C_WHITE);
        tblPetugas.setFont(new Font("Poppins", Font.PLAIN, 12));
        tblPetugas.setRowHeight(32);
        tblPetugas.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 12));
        tblPetugas.getTableHeader().setBackground(C_SIDEBAR);
        tblPetugas.getTableHeader().setForeground(C_WHITE);
        tblPetugas.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tblPetugas.setSelectionBackground(new Color(236, 72, 153, 80)); // pink selection
        tblPetugas.setSelectionForeground(C_WHITE);
        tblPetugas.setGridColor(C_BORDER);

        // Lebar kolom
        tblPetugas.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        tblPetugas.getColumnModel().getColumn(1).setPreferredWidth(160); // Nama Lengkap
        tblPetugas.getColumnModel().getColumn(2).setPreferredWidth(100); // Username
        tblPetugas.getColumnModel().getColumn(3).setPreferredWidth(100); // Password (disamarkan/Plain)
        tblPetugas.getColumnModel().getColumn(4).setPreferredWidth(80);  // Level

        // Panel Pencarian di atas tabel
        JPanel panelCari = new JPanel(new BorderLayout(8, 0));
        panelCari.setBackground(C_BG);
        panelCari.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblCari = new JLabel("Cari Petugas: ");
        lblCari.setFont(new Font("Poppins", Font.BOLD, 12));
        lblCari.setForeground(C_WHITE);

        txtCari = makeTextField("Cari...");

        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }

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
        tblPetugas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = tblPetugas.getSelectedRow();
                if (viewRow >= 0) {
                    int row = tblPetugas.convertRowIndexToModel(viewRow);
                    passwordManuallyEdited = false;
                    txtIdUser.setText(tableModel.getValueAt(row, 0).toString());
                    txtNamaLengkap.setText(tableModel.getValueAt(row, 1).toString());
                    txtUsername.setText(tableModel.getValueAt(row, 2).toString());
                    txtPassword.setText(tableModel.getValueAt(row, 3).toString());
                    
                    String lvl = tableModel.getValueAt(row, 4).toString();
                    cmbLevel.setSelectedItem(lvl);

                    btnEdit.setEnabled(true);
                    btnHapus.setEnabled(true);
                    btnTambah.setEnabled(false);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblPetugas);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER));
        scroll.getViewport().setBackground(C_BG);
        scroll.setBackground(C_BG);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "SELECT id_user, nama_lengkap, username, password, level FROM tb_user ORDER BY id_user";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id_user"),
                    rs.getString("nama_lengkap"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("level")
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

    private void tambahData() {
        if (!validasiInput()) return;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "INSERT INTO tb_user (username, password, nama_lengkap, level) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtUsername.getText().trim());
            ps.setString(2, txtPassword.getText().trim());
            ps.setString(3, txtNamaLengkap.getText().trim());
            ps.setString(4, cmbLevel.getSelectedItem().toString());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Petugas berhasil ditambahkan!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }

    private void editData() {
        if (!validasiInput()) return;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql = "UPDATE tb_user SET username=?, password=?, nama_lengkap=?, level=? WHERE id_user=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, txtUsername.getText().trim());
            ps.setString(2, txtPassword.getText().trim());
            ps.setString(3, txtNamaLengkap.getText().trim());
            ps.setString(4, cmbLevel.getSelectedItem().toString());
            ps.setInt(5, Integer.parseInt(txtIdUser.getText().trim()));
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Data petugas berhasil diperbarui!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }

    private void hapusData() {
        int targetId = Integer.parseInt(txtIdUser.getText().trim());
        if (targetId == currentLoggedInId) {
            JOptionPane.showMessageDialog(this,
                    "Akses Ditolak!\nAnda tidak dapat menghapus akun Anda sendiri yang sedang digunakan.",
                    "Gagal Hapus", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus petugas ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM tb_user WHERE id_user = ?");
            ps.setInt(1, targetId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Petugas berhasil dihapus!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadData();
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(this, 
                        "Gagal menghapus petugas!\nPetugas ini tidak bisa dihapus karena pernah melakukan transaksi penjualan.\nHal ini diperlukan untuk mencatat penanggung jawab transaksi tersebut.", 
                        "Error Integrasi Data", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            Database.closeConnection(conn);
        }
    }

    private boolean validasiInput() {
        if (txtNamaLengkap.getText().trim().isEmpty()) {
            warn("Nama lengkap tidak boleh kosong!"); return false;
        }
        if (txtUsername.getText().trim().isEmpty()) {
            warn("Username tidak boleh kosong!"); return false;
        }
        if (txtPassword.getText().trim().isEmpty()) {
            warn("Password tidak boleh kosong!"); return false;
        }
        return true;
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private void resetForm() {
        passwordManuallyEdited = false;
        if (txtCari != null) txtCari.setText("");
        txtIdUser.setText("Otomatis");
        txtNamaLengkap.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cmbLevel.setSelectedIndex(0);

        btnTambah.setEnabled(true);
        btnEdit.setEnabled(false);
        btnHapus.setEnabled(false);
        tblPetugas.clearSelection();
        txtNamaLengkap.requestFocus();
    }

    // ─── Helpers ───
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
                        BorderFactory.createLineBorder(C_PINK, 2),
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
