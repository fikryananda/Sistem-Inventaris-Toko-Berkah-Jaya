package sisteminventaris;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

/**
 * FormLaporan - Laporan Riwayat Transaksi Penjualan
 * Mata Kuliah : Pemrograman 2
 *
 * Menampilkan riwayat semua transaksi dalam JTable:
 * No | Tanggal | Customer | Barang | Jumlah | Total Bayar | Kasir
 */
public class FormLaporan extends JDialog {

    // Komponen UI
    private JTable            tblLaporan;
    private DefaultTableModel tableModel;
    private JLabel            lblTotalTransaksi, lblTotalPendapatan;
    private JButton           btnRefresh;
    private JTextField        txtCari;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Detail Transaksi
    private JTable            tblDetail;
    private DefaultTableModel detailTableModel;

    // Format
    private static final DecimalFormat FMT = new DecimalFormat("Rp #,###");

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
    public FormLaporan(Frame parent, boolean modal) {
        super(parent, modal);
        initUI();
        loadData();
    }

    // ─── Inisialisasi UI ────────────────────────────────────────────
    private void initUI() {
        setTitle("Laporan Penjualan");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1000, 560);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(C_BG);
        setContentPane(root);

        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildTablePanel(), BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);
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

                // Draw Chart vector icon
                int ix = 24, iy = 18;
                g2.setColor(C_ACCENT);
                g2.fillRect(ix, iy + 14, 6, 8);
                g2.fillRect(ix + 8, iy + 6, 6, 16);
                g2.fillRect(ix + 16, iy, 6, 22);
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(10, 68, 10, 25)); // margin left to 68px to clear icon

        JLabel lbl = new JLabel("Laporan Riwayat Transaksi Penjualan");
        lbl.setFont(new Font("Poppins", Font.BOLD, 18));
        lbl.setForeground(C_WHITE);
        header.add(lbl, BorderLayout.WEST);

        btnRefresh = new JButton("Refresh") {
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
                g2.setColor(hovered ? new Color(255, 255, 255, 60) : new Color(255, 255, 255, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btnRefresh.setFont(new Font("Poppins", Font.BOLD, 12));
        btnRefresh.setForeground(C_WHITE);
        btnRefresh.setOpaque(false);
        btnRefresh.setContentAreaFilled(false);
        btnRefresh.setBorderPainted(false);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadData());

        header.add(btnRefresh, BorderLayout.EAST);

        return header;
    }

    // ─── Panel Tabel ─────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(C_BG);
        panel.setBorder(new EmptyBorder(15, 15, 10, 15));

        // Panel Pencarian di atas tabel
        JPanel panelCari = new JPanel(new BorderLayout(8, 0));
        panelCari.setBackground(C_BG);
        panelCari.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblCari = new JLabel("Cari Transaksi: ");
        lblCari.setFont(new Font("Poppins", Font.BOLD, 12));
        lblCari.setForeground(C_WHITE);

        txtCari = new JTextField();
        txtCari.setFont(new Font("Poppins", Font.PLAIN, 13));
        txtCari.setBackground(new Color(24, 32, 47));
        txtCari.setForeground(Color.WHITE);
        txtCari.setCaretColor(Color.WHITE);
        txtCari.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                new EmptyBorder(5, 9, 5, 9)));
        txtCari.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtCari.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_ACCENT, 2),
                        new EmptyBorder(4, 8, 4, 8)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                txtCari.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(C_BORDER, 1),
                        new EmptyBorder(5, 9, 5, 9)
                ));
            }
        });

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

        // Tabel Master (Transaksi Penjualan)
        tableModel = new DefaultTableModel(
                new String[]{"No", "No. Faktur", "Tanggal", "Customer", "Total Bayar", "Kasir", "ID Jual"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        rowSorter = new TableRowSorter<>(tableModel);

        tblLaporan = new JTable(tableModel) {
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
        tblLaporan.setRowSorter(rowSorter);
        tblLaporan.setBackground(C_CARD);
        tblLaporan.setForeground(C_WHITE);
        tblLaporan.setFont(new Font("Poppins", Font.PLAIN, 13));
        tblLaporan.setRowHeight(32);
        tblLaporan.setShowGrid(true);
        tblLaporan.setGridColor(C_BORDER);
        tblLaporan.setSelectionBackground(new Color(59, 130, 246, 80));
        tblLaporan.setSelectionForeground(C_WHITE);

        // Header tabel
        tblLaporan.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 13));
        tblLaporan.getTableHeader().setBackground(C_SIDEBAR);
        tblLaporan.getTableHeader().setForeground(C_WHITE);
        tblLaporan.getTableHeader().setPreferredSize(new Dimension(0, 36));

        // Lebar kolom
        tblLaporan.getColumnModel().getColumn(0).setPreferredWidth(45);  // No
        tblLaporan.getColumnModel().getColumn(1).setPreferredWidth(140); // No. Faktur
        tblLaporan.getColumnModel().getColumn(2).setPreferredWidth(120); // Tanggal
        tblLaporan.getColumnModel().getColumn(3).setPreferredWidth(180); // Customer
        tblLaporan.getColumnModel().getColumn(4).setPreferredWidth(140); // Total Bayar
        tblLaporan.getColumnModel().getColumn(5).setPreferredWidth(140); // Kasir

        // Rata tengah
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tblLaporan.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblLaporan.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tblLaporan.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Rata kanan
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tblLaporan.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        // Sembunyikan kolom ID Jual dari GUI
        tblLaporan.removeColumn(tblLaporan.getColumnModel().getColumn(6));

        JScrollPane scrollMaster = new JScrollPane(tblLaporan);
        scrollMaster.setBorder(BorderFactory.createLineBorder(C_BORDER));
        scrollMaster.getViewport().setBackground(C_BG);
        scrollMaster.setBackground(C_BG);

        // Panel Pembungkus Master
        JPanel panelMaster = new JPanel(new BorderLayout());
        panelMaster.setBackground(C_BG);
        JLabel lblMasterTitle = new JLabel("Daftar Transaksi (Klik baris untuk melihat detail)");
        lblMasterTitle.setFont(new Font("Poppins", Font.BOLD, 13));
        lblMasterTitle.setForeground(C_TEXT_LIGHT);
        lblMasterTitle.setBorder(new EmptyBorder(0, 0, 5, 0));
        panelMaster.add(lblMasterTitle, BorderLayout.NORTH);
        panelMaster.add(scrollMaster, BorderLayout.CENTER);

        // Tabel Detail (Daftar Item Barang)
        detailTableModel = new DefaultTableModel(
                new String[]{"No", "Kode Barang", "Nama Barang", "Harga Satuan", "Jumlah", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblDetail = new JTable(detailTableModel) {
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
        tblDetail.setBackground(C_CARD);
        tblDetail.setForeground(C_WHITE);
        tblDetail.setFont(new Font("Poppins", Font.PLAIN, 13));
        tblDetail.setRowHeight(32);
        tblDetail.setShowGrid(true);
        tblDetail.setGridColor(C_BORDER);
        tblDetail.setSelectionBackground(new Color(59, 130, 246, 80));
        tblDetail.setSelectionForeground(C_WHITE);

        tblDetail.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 13));
        tblDetail.getTableHeader().setBackground(C_SIDEBAR);
        tblDetail.getTableHeader().setForeground(C_WHITE);
        tblDetail.getTableHeader().setPreferredSize(new Dimension(0, 36));

        // Lebar kolom detail
        tblDetail.getColumnModel().getColumn(0).setPreferredWidth(45);  // No
        tblDetail.getColumnModel().getColumn(1).setPreferredWidth(120); // Kode Barang
        tblDetail.getColumnModel().getColumn(2).setPreferredWidth(200); // Nama Barang
        tblDetail.getColumnModel().getColumn(3).setPreferredWidth(140); // Harga Satuan
        tblDetail.getColumnModel().getColumn(4).setPreferredWidth(80);  // Jumlah
        tblDetail.getColumnModel().getColumn(5).setPreferredWidth(140); // Subtotal

        // Format cell detail
        tblDetail.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblDetail.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tblDetail.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tblDetail.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        tblDetail.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        JScrollPane scrollDetail = new JScrollPane(tblDetail);
        scrollDetail.setBorder(BorderFactory.createLineBorder(C_BORDER));
        scrollDetail.getViewport().setBackground(C_BG);
        scrollDetail.setBackground(C_BG);

        // Panel Pembungkus Detail
        JPanel panelDetail = new JPanel(new BorderLayout());
        panelDetail.setBackground(C_BG);
        JLabel lblDetailTitle = new JLabel("Detail Barang Belanjaan");
        lblDetailTitle.setFont(new Font("Poppins", Font.BOLD, 13));
        lblDetailTitle.setForeground(C_TEXT_LIGHT);
        lblDetailTitle.setBorder(new EmptyBorder(5, 0, 5, 0));
        panelDetail.add(lblDetailTitle, BorderLayout.NORTH);
        panelDetail.add(scrollDetail, BorderLayout.CENTER);

        // JSplitPane untuk Menggabungkan Master & Detail
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelMaster, panelDetail);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.5);
        splitPane.setBackground(C_BG);
        splitPane.setBorder(null);

        panel.add(splitPane, BorderLayout.CENTER);

        // Listener untuk Memuat Detail Transaksi saat baris Master dipilih
        tblLaporan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tblLaporan.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = tblLaporan.convertRowIndexToModel(selectedRow);
                    int idJual = (int) tableModel.getValueAt(modelRow, 6);
                    loadDetail(idJual);
                } else {
                    detailTableModel.setRowCount(0);
                }
            }
        });

        return panel;
    }

    // ─── Footer Ringkasan ────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(C_SIDEBAR);
        footer.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 12));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, C_BORDER));

        JLabel lblTotalLabel = new JLabel("Total Transaksi:");
        lblTotalLabel.setFont(new Font("Poppins", Font.BOLD, 13));
        lblTotalLabel.setForeground(C_TEXT_LIGHT);

        lblTotalTransaksi = new JLabel("0 transaksi");
        lblTotalTransaksi.setFont(new Font("Poppins", Font.BOLD, 13));
        lblTotalTransaksi.setForeground(C_ACCENT);

        JLabel sep = new JLabel("  |  ");
        sep.setForeground(C_BORDER);

        JLabel lblPendapatanLabel = new JLabel("Total Pendapatan:");
        lblPendapatanLabel.setFont(new Font("Poppins", Font.BOLD, 13));
        lblPendapatanLabel.setForeground(C_TEXT_LIGHT);

        lblTotalPendapatan = new JLabel("Rp 0");
        lblTotalPendapatan.setFont(new Font("Poppins", Font.BOLD, 13));
        lblTotalPendapatan.setForeground(C_SUCCESS);

        footer.add(lblTotalLabel);
        footer.add(lblTotalTransaksi);
        footer.add(sep);
        footer.add(lblPendapatanLabel);
        footer.add(lblTotalPendapatan);

        return footer;
    }

    // ─── Load Data Laporan ───────────────────────────────────────────
    private void loadData() {
        if (txtCari != null) txtCari.setText("");
        tableModel.setRowCount(0);
        if (detailTableModel != null) detailTableModel.setRowCount(0);
        Connection conn = null;
        try {
            conn = Database.getConnection();

            // Query JOIN: penjualan + customer + user
            String sql =
                "SELECT p.id_jual, p.no_faktur, p.tgl_transaksi, " +
                "       c.nama_customer, " +
                "       p.total_bayar, " +
                "       u.nama_lengkap AS kasir " +
                "FROM tb_penjualan p " +
                "JOIN tb_customer c ON p.id_customer = c.id_customer " +
                "JOIN tb_user     u ON p.id_user     = u.id_user " +
                "ORDER BY p.id_jual DESC";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            int no = 1;
            double totalPendapatan = 0;

            while (rs.next()) {
                double total = rs.getDouble("total_bayar");
                totalPendapatan += total;

                tableModel.addRow(new Object[]{
                    no++,
                    rs.getString("no_faktur"),
                    rs.getString("tgl_transaksi"),
                    rs.getString("nama_customer"),
                    FMT.format(total),
                    rs.getString("kasir"),
                    rs.getInt("id_jual")
                });
            }

            // Update label ringkasan di footer
            lblTotalTransaksi.setText((no - 1) + " transaksi");
            lblTotalPendapatan.setText(FMT.format(totalPendapatan));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat laporan: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Load Detail Transaksi ───────────────────────────────────────
    private void loadDetail(int idJual) {
        detailTableModel.setRowCount(0);
        Connection conn = null;
        try {
            conn = Database.getConnection();
            String sql =
                "SELECT d.id_barang, b.nama_barang, d.harga_satuan, d.jumlah_beli, d.subtotal " +
                "FROM tb_detail_penjualan d " +
                "JOIN tb_barang b ON d.id_barang = b.id_barang " +
                "WHERE d.id_jual = ? " +
                "ORDER BY d.id_detail ASC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idJual);
            ResultSet rs = ps.executeQuery();

            int no = 1;
            while (rs.next()) {
                detailTableModel.addRow(new Object[]{
                    no++,
                    rs.getString("id_barang"),
                    rs.getString("nama_barang"),
                    FMT.format(rs.getDouble("harga_satuan")),
                    rs.getInt("jumlah_beli"),
                    FMT.format(rs.getDouble("subtotal"))
                });
            }
        } catch (SQLException ex) {
            System.err.println("[ERROR] loadDetail SQL: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat detail transaksi: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Database.closeConnection(conn);
        }
    }
}
