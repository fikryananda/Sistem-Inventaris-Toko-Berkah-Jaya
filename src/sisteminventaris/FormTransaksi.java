package sisteminventaris;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * FormTransaksi - Proses Penjualan Barang dengan Fitur Keranjang Belanja
 * Mata Kuliah : Pemrograman 2
 */
public class FormTransaksi extends JDialog {

    // Data user yang sedang login
    private final int idUser;

    // Komponen UI
    private JComboBox<String> cmbCustomer, cmbBarang;
    private JTextField        txtNoFaktur, txtTanggal, txtHarga, txtJumlah, txtTotal, txtUangBayar;
    private JButton           btnTambahCart, btnSimpan, btnHapusItem, btnBatal;
    private JLabel            lblStokInfo, lblGrandTotal, lblKembalian;

    private JTable            tblCart;
    private DefaultTableModel cartTableModel;

    // Array untuk menyimpan ID dari ComboBox
    private String[] idCustomerArr;
    private String[] idBarangArr;
    private double[] hargaArr;
    private int[]    stokArr;

    // Format
    private static final DecimalFormat FMT_RUPIAH = new DecimalFormat("Rp #,###");

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
    private static final Color C_WARNING      = new Color(245, 158, 11);  // Amber 500
    private static final Color C_GRAY         = new Color(24, 32, 47);    // Dark input bg

    // Cart Items List
    private static class CartItem {
        String idBarang;
        String namaBarang;
        double hargaJual;
        int jumlah;
        double subtotal;

        CartItem(String idBarang, String namaBarang, double hargaJual, int jumlah) {
            this.idBarang = idBarang;
            this.namaBarang = namaBarang;
            this.hargaJual = hargaJual;
            this.jumlah = jumlah;
            this.subtotal = hargaJual * jumlah;
        }
    }

    private final java.util.List<CartItem> cartList = new java.util.ArrayList<>();

    // ─── Constructor ────────────────────────────────────────────────
    public FormTransaksi(Frame parent, boolean modal, int idUser) {
        super(parent, modal);
        this.idUser = idUser;
        initUI();
        loadCustomer();
        loadBarang();
        setTanggalHariIni();
        setNoFakturBaru();
    }

    // ─── Inisialisasi UI ────────────────────────────────────────────
    private void initUI() {
        setTitle("Form Transaksi Penjualan");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(C_BG);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(0, 0));
        mainContent.setBackground(C_BG);
        
        JScrollPane leftScroll = new JScrollPane(buildLeftInputPanel());
        leftScroll.setBorder(null);
        leftScroll.setPreferredSize(new Dimension(340, 0));
        leftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        leftScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        leftScroll.getViewport().setBackground(C_SIDEBAR);
        leftScroll.getVerticalScrollBar().setUnitIncrement(12);
        
        mainContent.add(leftScroll, BorderLayout.WEST);
        mainContent.add(buildRightCartPanel(), BorderLayout.CENTER);

        root.add(mainContent, BorderLayout.CENTER);
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

                // Draw Cart vector icon
                int ix = 24, iy = 15;
                g2.setColor(C_ACCENT);
                g2.setStroke(new BasicStroke(2f));
                // Cart handle
                g2.drawLine(ix, iy + 2, ix + 4, iy + 2);
                g2.drawLine(ix + 4, iy + 2, ix + 8, iy + 14);
                g2.drawLine(ix + 8, iy + 14, ix + 22, iy + 14);
                // Basket
                g2.drawRect(ix + 6, iy + 4, 18, 8);
                // Wheels
                g2.setStroke(new BasicStroke(1.5f));
                g2.fillOval(ix + 8, iy + 16, 4, 4);
                g2.fillOval(ix + 18, iy + 16, 4, 4);
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(10, 68, 10, 25)); // margin left to 68px to clear icon

        JLabel lbl = new JLabel("Transaksi Penjualan Barang");
        lbl.setFont(new Font("Poppins", Font.BOLD, 18));
        lbl.setForeground(C_WHITE);
        header.add(lbl, BorderLayout.WEST);

        JLabel lblSub = new JLabel("Toko Berkah Jaya");
        lblSub.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblSub.setForeground(C_TEXT_DIM);
        header.add(lblSub, BorderLayout.EAST);

        return header;
    }

    // ─── Left Input Panel ───────────────────────────────────────────
    private JPanel buildLeftInputPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(C_SIDEBAR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(18, 20, 18, 20));

        // No. Faktur
        panel.add(makeLabel("No. Faktur"));
        panel.add(Box.createVerticalStrut(4));
        txtNoFaktur = makeTextField("");
        txtNoFaktur.setEditable(false);
        txtNoFaktur.setBackground(new Color(15, 23, 42));
        txtNoFaktur.setForeground(C_TEXT_DIM);
        panel.add(txtNoFaktur);
        panel.add(Box.createVerticalStrut(10));

        // Tanggal Transaksi
        panel.add(makeLabel("Tanggal Transaksi"));
        panel.add(Box.createVerticalStrut(4));
        txtTanggal = makeTextField("");
        txtTanggal.setEditable(false);
        txtTanggal.setBackground(new Color(15, 23, 42));
        txtTanggal.setForeground(C_TEXT_DIM);
        panel.add(txtTanggal);
        panel.add(Box.createVerticalStrut(10));

        // Customer
        panel.add(makeLabel("Customer / Pelanggan"));
        panel.add(Box.createVerticalStrut(4));
        cmbCustomer = new JComboBox<>();
        cmbCustomer.setFont(new Font("Poppins", Font.PLAIN, 13));
        cmbCustomer.setBackground(C_GRAY);
        cmbCustomer.setForeground(Color.WHITE);
        cmbCustomer.setBorder(BorderFactory.createLineBorder(C_BORDER));
        cmbCustomer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbCustomer.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbCustomer.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? C_ACCENT : C_GRAY);
                setForeground(Color.WHITE);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        panel.add(cmbCustomer);
        panel.add(Box.createVerticalStrut(10));

        // Barang
        panel.add(makeLabel("Pilih Barang"));
        panel.add(Box.createVerticalStrut(4));
        cmbBarang = new JComboBox<>();
        cmbBarang.setFont(new Font("Poppins", Font.PLAIN, 13));
        cmbBarang.setBackground(C_GRAY);
        cmbBarang.setForeground(Color.WHITE);
        cmbBarang.setBorder(BorderFactory.createLineBorder(C_BORDER));
        cmbBarang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbBarang.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbBarang.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? C_ACCENT : C_GRAY);
                setForeground(Color.WHITE);
                setBorder(new EmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        panel.add(cmbBarang);
        
        // Info stok
        lblStokInfo = new JLabel("Stok tersedia: -");
        lblStokInfo.setFont(new Font("Poppins", Font.ITALIC, 11));
        lblStokInfo.setForeground(C_SUCCESS);
        lblStokInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(Box.createVerticalStrut(3));
        panel.add(lblStokInfo);
        panel.add(Box.createVerticalStrut(10));

        // Harga Jual
        panel.add(makeLabel("Harga Jual"));
        panel.add(Box.createVerticalStrut(4));
        txtHarga = makeTextField("Otomatis");
        txtHarga.setEditable(false);
        txtHarga.setBackground(new Color(15, 23, 42));
        txtHarga.setForeground(C_TEXT_DIM);
        panel.add(txtHarga);
        panel.add(Box.createVerticalStrut(10));

        // Jumlah Beli
        panel.add(makeLabel("Jumlah Beli"));
        panel.add(Box.createVerticalStrut(4));
        txtJumlah = makeTextField("Masukkan jumlah");
        panel.add(txtJumlah);
        panel.add(Box.createVerticalStrut(10));

        // Subtotal
        panel.add(makeLabel("Subtotal Item"));
        panel.add(Box.createVerticalStrut(4));
        txtTotal = makeTextField("Otomatis");
        txtTotal.setEditable(false);
        txtTotal.setBackground(new Color(15, 23, 42));
        txtTotal.setForeground(C_WARNING);
        txtTotal.setFont(new Font("Poppins", Font.BOLD, 15));
        panel.add(txtTotal);
        panel.add(Box.createVerticalStrut(18));

        // Tombol Tambah ke Keranjang
        btnTambahCart = makeButton("Tambah ke Keranjang", C_ACCENT);
        btnTambahCart.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(btnTambahCart);

        // ── Event Listener ──
        cmbBarang.addActionListener(e -> {
            int idx = cmbBarang.getSelectedIndex();
            if (idx >= 0 && hargaArr != null && idx < hargaArr.length) {
                txtHarga.setText(FMT_RUPIAH.format(hargaArr[idx]));
                int stok = stokArr[idx];
                lblStokInfo.setText("Stok tersedia: " + stok + " unit");
                lblStokInfo.setForeground(stok > 5 ? C_SUCCESS : (stok > 0 ? C_WARNING : C_DANGER));
                hitungSubtotal();
            }
        });

        txtJumlah.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                hitungSubtotal();
            }
        });

        btnTambahCart.addActionListener(e -> tambahKeKeranjang());

        return panel;
    }

    // ─── Right Cart Panel ───────────────────────────────────────────
    private JPanel buildRightCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(C_BG);
        panel.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Judul daftar
        JLabel lblCartTitle = new JLabel("Keranjang Belanja");
        lblCartTitle.setFont(new Font("Poppins", Font.BOLD, 14));
        lblCartTitle.setForeground(C_WHITE);
        lblCartTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblCartTitle, BorderLayout.NORTH);

        // Tabel Keranjang
        cartTableModel = new DefaultTableModel(
                new String[]{"No", "Kode", "Nama Barang", "Harga", "Qty", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblCart = new JTable(cartTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? C_CARD : C_BG);
                    c.setForeground(C_WHITE);
                } else {
                    c.setForeground(C_WHITE);
                }
                return c;
            }
        };

        tblCart.setBackground(C_CARD);
        tblCart.setForeground(C_WHITE);
        tblCart.setFont(new Font("Poppins", Font.PLAIN, 12));
        tblCart.setRowHeight(32);
        tblCart.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 12));
        tblCart.getTableHeader().setBackground(C_SIDEBAR);
        tblCart.getTableHeader().setForeground(C_WHITE);
        tblCart.getTableHeader().setPreferredSize(new Dimension(0, 34));
        tblCart.setSelectionBackground(new Color(59, 130, 246, 80));
        tblCart.setSelectionForeground(C_WHITE);
        tblCart.setGridColor(C_BORDER);

        // Lebar kolom
        tblCart.getColumnModel().getColumn(0).setPreferredWidth(35);
        tblCart.getColumnModel().getColumn(1).setPreferredWidth(70);
        tblCart.getColumnModel().getColumn(2).setPreferredWidth(170);
        tblCart.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblCart.getColumnModel().getColumn(4).setPreferredWidth(50);
        tblCart.getColumnModel().getColumn(5).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(tblCart);
        scroll.setBorder(BorderFactory.createLineBorder(C_BORDER));
        scroll.getViewport().setBackground(C_BG);
        scroll.setBackground(C_BG);
        panel.add(scroll, BorderLayout.CENTER);

        // Panel Bawah: Grand Total, Pembayaran & Aksi
        JPanel panelBawah = new JPanel();
        panelBawah.setLayout(new BoxLayout(panelBawah, BoxLayout.Y_AXIS));
        panelBawah.setOpaque(false);
        panelBawah.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Grand Total Label
        lblGrandTotal = new JLabel("TOTAL BAYAR: Rp 0");
        lblGrandTotal.setFont(new Font("Poppins", Font.BOLD, 20));
        lblGrandTotal.setForeground(C_WARNING);
        lblGrandTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblGrandTotal.setBorder(new EmptyBorder(0, 0, 12, 0));
        panelBawah.add(lblGrandTotal);

        // ── Section Pembayaran ──
        JPanel paymentPanel = new JPanel();
        paymentPanel.setLayout(new BoxLayout(paymentPanel, BoxLayout.Y_AXIS));
        paymentPanel.setOpaque(false);
        paymentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, C_BORDER),
                new EmptyBorder(12, 0, 12, 0)
        ));
        paymentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label Uang Dibayar
        JLabel lblUangBayar = new JLabel("Uang Dibayar (Rp)");
        lblUangBayar.setFont(new Font("Poppins", Font.BOLD, 13));
        lblUangBayar.setForeground(C_TEXT_LIGHT);
        lblUangBayar.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentPanel.add(lblUangBayar);
        paymentPanel.add(Box.createVerticalStrut(4));

        // TextField Uang Dibayar
        txtUangBayar = makeTextField("Masukkan nominal uang");
        txtUangBayar.setFont(new Font("Poppins", Font.BOLD, 16));
        txtUangBayar.setForeground(C_WHITE);
        txtUangBayar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtUangBayar.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentPanel.add(txtUangBayar);
        paymentPanel.add(Box.createVerticalStrut(8));

        // Label Kembalian
        JLabel lblKembalianTitle = new JLabel("Kembalian");
        lblKembalianTitle.setFont(new Font("Poppins", Font.BOLD, 13));
        lblKembalianTitle.setForeground(C_TEXT_LIGHT);
        lblKembalianTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentPanel.add(lblKembalianTitle);
        paymentPanel.add(Box.createVerticalStrut(4));

        lblKembalian = new JLabel("Rp 0");
        lblKembalian.setFont(new Font("Poppins", Font.BOLD, 20));
        lblKembalian.setForeground(C_SUCCESS);
        lblKembalian.setAlignmentX(Component.LEFT_ALIGNMENT);
        paymentPanel.add(lblKembalian);

        panelBawah.add(paymentPanel);
        panelBawah.add(Box.createVerticalStrut(12));

        // Event: hitung kembalian otomatis saat input uang dibayar
        txtUangBayar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                hitungKembalian();
            }
        });

        // Tombol Aksi
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        btnHapusItem = makeButton("Hapus Item", C_DANGER);
        btnSimpan    = makeButton("Bayar & Simpan", C_SUCCESS);
        btnBatal     = makeButton("Batal / Reset", new Color(71, 85, 105));

        btnPanel.add(btnHapusItem);
        btnPanel.add(btnBatal);
        btnPanel.add(btnSimpan);
        panelBawah.add(btnPanel);

        panel.add(panelBawah, BorderLayout.SOUTH);

        // ── Event Listener Bawah ──
        btnHapusItem.addActionListener(e -> hapusItemDariKeranjang());
        btnBatal.addActionListener(e -> resetForm());
        btnSimpan.addActionListener(e -> simpanTransaksi());

        return panel;
    }

    // ─── Hitung Kembalian ─────────────────────────────────────────────
    private void hitungKembalian() {
        try {
            String uangText = txtUangBayar.getText().trim().replace(".", "").replace(",", "");
            if (uangText.isEmpty()) {
                lblKembalian.setText("Rp 0");
                lblKembalian.setForeground(C_SUCCESS);
                return;
            }
            double uangBayar = Double.parseDouble(uangText);
            double grandTotal = getGrandTotal();
            double kembalian = uangBayar - grandTotal;

            if (kembalian >= 0) {
                lblKembalian.setText(FMT_RUPIAH.format(kembalian));
                lblKembalian.setForeground(C_SUCCESS);
            } else {
                lblKembalian.setText("Kurang " + FMT_RUPIAH.format(Math.abs(kembalian)));
                lblKembalian.setForeground(C_DANGER);
            }
        } catch (NumberFormatException ex) {
            lblKembalian.setText("Input tidak valid");
            lblKembalian.setForeground(C_DANGER);
        }
    }

    // ─── Get Grand Total dari keranjang ───────────────────────────────
    private double getGrandTotal() {
        double total = 0;
        for (CartItem item : cartList) {
            total += item.subtotal;
        }
        return total;
    }

    // ─── Hitung Subtotal ─────────────────────────────────────────────
    private void hitungSubtotal() {
        try {
            int idx = cmbBarang.getSelectedIndex();
            if (idx < 0 || hargaArr == null || idx >= hargaArr.length) return;

            double harga  = hargaArr[idx];
            int    jumlah = Integer.parseInt(txtJumlah.getText().trim());
            double total  = harga * jumlah;

            txtTotal.setText(FMT_RUPIAH.format(total));
        } catch (NumberFormatException ex) {
            txtTotal.setText("");
        }
    }

    // ─── Tambah ke Keranjang ──────────────────────────────────────────
    private void tambahKeKeranjang() {
        if (cmbBarang.getItemCount() == 0) {
            warn("Tidak ada data barang!"); return;
        }
        if (txtJumlah.getText().trim().isEmpty()) {
            warn("Jumlah beli tidak boleh kosong!"); return;
        }

        int jumlahBeli;
        try {
            jumlahBeli = Integer.parseInt(txtJumlah.getText().trim());
            if (jumlahBeli <= 0) {
                warn("Jumlah beli harus lebih dari 0!"); return;
            }
        } catch (NumberFormatException ex) {
            warn("Jumlah beli harus berupa angka!"); return;
        }

        int    idxBarang   = cmbBarang.getSelectedIndex();
        int    stokTersedia = stokArr[idxBarang];
        String idBarang    = idBarangArr[idxBarang];
        String namaBarang  = cmbBarang.getSelectedItem().toString().split(" - ", 2)[1];
        double harga       = hargaArr[idxBarang];

        // Hitung total di keranjang untuk barang ini
        int jumlahDiKeranjang = 0;
        CartItem existingItem = null;
        for (CartItem item : cartList) {
            if (item.idBarang.equals(idBarang)) {
                existingItem = item;
                jumlahDiKeranjang = item.jumlah;
                break;
            }
        }

        if (jumlahBeli + jumlahDiKeranjang > stokTersedia) {
            JOptionPane.showMessageDialog(this,
                    "Stok barang tidak mencukupi!\n" +
                    "Stok tersedia : " + stokTersedia + " unit\n" +
                    "Di Keranjang  : " + jumlahDiKeranjang + " unit\n" +
                    "Tambahan baru : " + jumlahBeli + " unit",
                    "Stok Tidak Cukup", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (existingItem != null) {
            existingItem.jumlah += jumlahBeli;
            existingItem.subtotal = existingItem.hargaJual * existingItem.jumlah;
        } else {
            cartList.add(new CartItem(idBarang, namaBarang, harga, jumlahBeli));
        }

        updateCartTable();
        cmbCustomer.setEnabled(false); // Kunci pilihan customer saat keranjang terisi
        txtJumlah.setText("");
        txtTotal.setText("");
        txtJumlah.requestFocus();
    }

    // ─── Hapus Item Dari Keranjang ────────────────────────────────────
    private void hapusItemDariKeranjang() {
        int selectedRow = tblCart.getSelectedRow();
        if (selectedRow >= 0) {
            cartList.remove(selectedRow);
            updateCartTable();
            if (cartList.isEmpty()) {
                cmbCustomer.setEnabled(true); // Lepas kunci customer jika keranjang kosong
            }
        } else {
            warn("Pilih item di keranjang yang ingin dihapus!");
        }
    }

    // ─── Update Tampilan Tabel Keranjang ──────────────────────────────
    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        double grandTotal = 0;
        int no = 1;
        for (CartItem item : cartList) {
            cartTableModel.addRow(new Object[]{
                no++,
                item.idBarang,
                item.namaBarang,
                FMT_RUPIAH.format(item.hargaJual),
                item.jumlah,
                FMT_RUPIAH.format(item.subtotal)
            });
            grandTotal += item.subtotal;
        }
        lblGrandTotal.setText("TOTAL BAYAR: " + FMT_RUPIAH.format(grandTotal));
    }

    // ─── Simpan Transaksi (Batch Checkout) ───────────────────────────
    private void simpanTransaksi() {
        if (cartList.isEmpty()) {
            warn("Keranjang belanja masih kosong!"); return;
        }

        // Validasi uang dibayar
        String uangText = txtUangBayar.getText().trim().replace(".", "").replace(",", "");
        if (uangText.isEmpty()) {
            warn("Masukkan nominal uang yang dibayarkan customer!");
            txtUangBayar.requestFocus();
            return;
        }

        double uangBayar;
        try {
            uangBayar = Double.parseDouble(uangText);
        } catch (NumberFormatException ex) {
            warn("Nominal uang bayar harus berupa angka!");
            txtUangBayar.requestFocus();
            return;
        }

        double grandTotal = getGrandTotal();
        if (uangBayar < grandTotal) {
            warn("Uang yang dibayarkan kurang!\n" +
                 "Total belanja : " + FMT_RUPIAH.format(grandTotal) + "\n" +
                 "Uang dibayar  : " + FMT_RUPIAH.format(uangBayar) + "\n" +
                 "Kekurangan    : " + FMT_RUPIAH.format(grandTotal - uangBayar));
            txtUangBayar.requestFocus();
            return;
        }

        double kembalian = uangBayar - grandTotal;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Konfirmasi Pembayaran:\n" +
                "─────────────────────────\n" +
                "Total item    : " + cartList.size() + " jenis barang\n" +
                "Pelanggan     : " + cmbCustomer.getSelectedItem() + "\n" +
                "Total belanja : " + FMT_RUPIAH.format(grandTotal) + "\n" +
                "Uang dibayar  : " + FMT_RUPIAH.format(uangBayar) + "\n" +
                "Kembalian     : " + FMT_RUPIAH.format(kembalian) + "\n" +
                "─────────────────────────\n" +
                "Lanjutkan pembayaran?",
                "Konfirmasi Pembayaran", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert ke tb_penjualan (Header)
            String sqlInsertHeader = "INSERT INTO tb_penjualan " +
                                     "(no_faktur, tgl_transaksi, id_customer, total_bayar, id_user) " +
                                     "VALUES (?, CURDATE(), ?, ?, ?)";
            PreparedStatement psHeader = conn.prepareStatement(sqlInsertHeader, Statement.RETURN_GENERATED_KEYS);
            psHeader.setString(1, txtNoFaktur.getText().trim());
            psHeader.setString(2, idCustomerArr[cmbCustomer.getSelectedIndex()]);
            psHeader.setDouble(3, grandTotal);
            psHeader.setInt(4, idUser);
            psHeader.executeUpdate();

            ResultSet rsGen = psHeader.getGeneratedKeys();
            int idJual = -1;
            if (rsGen.next()) {
                idJual = rsGen.getInt(1);
            } else {
                throw new SQLException("Gagal mendapatkan ID Penjualan.");
            }

            // 2. Insert ke tb_detail_penjualan dan kurangi stok
            String sqlInsertDetail = "INSERT INTO tb_detail_penjualan " +
                                     "(id_jual, id_barang, harga_satuan, jumlah_beli, subtotal) " +
                                     "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psDetail = conn.prepareStatement(sqlInsertDetail);

            String sqlUpdate = "UPDATE tb_barang SET stok = stok - ? WHERE id_barang = ?";
            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);

            for (CartItem item : cartList) {
                // Insert detail
                psDetail.setInt(1, idJual);
                psDetail.setString(2, item.idBarang);
                psDetail.setDouble(3, item.hargaJual);
                psDetail.setInt(4, item.jumlah);
                psDetail.setDouble(5, item.subtotal);
                psDetail.executeUpdate();

                // Kurangi stok barang
                psUpdate.setInt(1, item.jumlah);
                psUpdate.setString(2, item.idBarang);
                psUpdate.executeUpdate();
            }

            // 3. Ambil nama lengkap kasir
            String namaKasir = "Petugas";
            try {
                String sqlUser = "SELECT nama_lengkap FROM tb_user WHERE id_user = ?";
                PreparedStatement psUser = conn.prepareStatement(sqlUser);
                psUser.setInt(1, idUser);
                ResultSet rsUser = psUser.executeQuery();
                if (rsUser.next()) {
                    namaKasir = rsUser.getString("nama_lengkap");
                }
            } catch (SQLException ex) {
                // ignore
            }

            conn.commit(); // Commit transaction

            // Otomatis cetak struk
            cetakStruk(txtNoFaktur.getText().trim(),
                    cmbCustomer.getSelectedItem().toString(),
                    namaKasir,
                    grandTotal,
                    uangBayar,
                    kembalian,
                    new java.util.ArrayList<>(cartList));
            
            cartList.clear();
            updateCartTable();
            resetForm();
            loadBarang(); // Reload barang untuk memperbarui info stok di combobox
        } catch (SQLException ex) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException re) { /* ignore */ }
            JOptionPane.showMessageDialog(this, "Gagal memproses transaksi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) { /* ignore */ }
            Database.closeConnection(conn);
        }
    }

    // ─── Cetak Struk Belanja ke File Teks ─────────────────────────────
    private void cetakStruk(String noFaktur, String namaCustomer, String namaKasir, double totalBayar, double uangBayar, double kembalian, java.util.List<CartItem> items) {
        try {
            // Membuat direktori "struk" jika belum ada
            java.io.File folder = new java.io.File("struk");
            if (!folder.exists()) {
                folder.mkdir();
            }

            java.io.File file = new java.io.File(folder, "struk_" + noFaktur + ".txt");
            java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file));

            DecimalFormat df = new DecimalFormat("#,###");

            writer.println("==========================================");
            writer.println("             TOKO BERKAH JAYA             ");
            writer.println("==========================================");
            writer.println("No. Faktur : " + noFaktur);
            writer.println("Tanggal    : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            writer.println("Pelanggan  : " + namaCustomer);
            writer.println("Kasir      : " + namaKasir);
            writer.println("==========================================");
            writer.println(String.format("%-18s %-3s %-8s %-9s", "Item", "Qty", "Harga", "Subtotal"));
            writer.println("------------------------------------------");

            for (CartItem item : items) {
                String nama = item.namaBarang;
                if (nama.length() > 18) {
                    nama = nama.substring(0, 15) + "...";
                }
                writer.println(String.format("%-18s %-3d %-8s %-9s",
                        nama,
                        item.jumlah,
                        df.format(item.hargaJual),
                        df.format(item.subtotal)));
            }
            writer.println("------------------------------------------");
            writer.println(String.format("%-22s Rp %s", "Total Belanja :", df.format(totalBayar)));
            writer.println(String.format("%-22s Rp %s", "Uang Bayar    :", df.format(uangBayar)));
            writer.println(String.format("%-22s Rp %s", "Kembalian      :", df.format(kembalian)));
            writer.println("==========================================");
            writer.println("    Terima Kasih atas Kunjungan Anda!     ");
            writer.println("==========================================");
            
            writer.close();

            // Tampilkan dialog sukses dengan preview struk
            StringBuilder sb = new StringBuilder();
            sb.append("Transaksi berhasil disimpan!\n")
              .append("Struk dicetak ke: ").append(file.getAbsolutePath())
              .append("\n\n━━━━━ PREVIEW STRUK ━━━━━\n\n");
            
            sb.append("No. Faktur : ").append(noFaktur).append("\n")
              .append("Pelanggan  : ").append(namaCustomer).append("\n")
              .append("─────────────────────────\n");
            for (CartItem item : items) {
                sb.append(item.namaBarang).append(" x ").append(item.jumlah)
                  .append(" = Rp ").append(df.format(item.subtotal)).append("\n");
            }
            sb.append("─────────────────────────\n")
              .append("Total      : Rp ").append(df.format(totalBayar)).append("\n")
              .append("Dibayar    : Rp ").append(df.format(uangBayar)).append("\n")
              .append("Kembalian  : Rp ").append(df.format(kembalian)).append("\n")
              .append("─────────────────────────\n")
              .append("Terima kasih!");

            JOptionPane.showMessageDialog(this, sb.toString(), "Transaksi Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak struk: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─── Load Customer ke ComboBox ───────────────────────────────────
    private void loadCustomer() {
        cmbCustomer.removeAllItems();
        Connection conn = null;
        try {
            conn = Database.getConnection();
            
            // Check if CUST000 exists
            PreparedStatement psCheck = conn.prepareStatement("SELECT COUNT(*) FROM tb_customer WHERE id_customer = ?");
            psCheck.setString(1, "CUST000");
            ResultSet rsCheck = psCheck.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                PreparedStatement psInsert = conn.prepareStatement(
                    "INSERT INTO tb_customer (id_customer, nama_customer, alamat, telepon) VALUES (?, ?, ?, ?)");
                psInsert.setString(1, "CUST000");
                psInsert.setString(2, "No Name");
                psInsert.setString(3, "-");
                psInsert.setString(4, "-");
                psInsert.executeUpdate();
            }

            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM tb_customer ORDER BY id_customer");

            java.util.List<String> ids = new java.util.ArrayList<>();
            while (rs.next()) {
                String idCust = rs.getString("id_customer");
                ids.add(idCust);
                if (idCust.equals("CUST000")) {
                    cmbCustomer.addItem(idCust + " - " + rs.getString("nama_customer") + " (Pelanggan Umum)");
                } else {
                    cmbCustomer.addItem(idCust + " - " + rs.getString("nama_customer"));
                }
            }
            idCustomerArr = ids.toArray(new String[0]);
        } catch (SQLException ex) {
            warn("Gagal memuat customer: " + ex.getMessage());
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Load Barang ke ComboBox ─────────────────────────────────────
    private void loadBarang() {
        cmbBarang.removeAllItems();
        Connection conn = null;
        try {
            conn = Database.getConnection();
            ResultSet rs = conn.createStatement()
                    .executeQuery("SELECT * FROM tb_barang ORDER BY id_barang");

            java.util.List<String>  ids    = new java.util.ArrayList<>();
            java.util.List<Double>  harga  = new java.util.ArrayList<>();
            java.util.List<Integer> stok   = new java.util.ArrayList<>();

            while (rs.next()) {
                ids.add(rs.getString("id_barang"));
                harga.add(rs.getDouble("harga_jual"));
                stok.add(rs.getInt("stok"));
                cmbBarang.addItem(rs.getString("id_barang") + " - " + rs.getString("nama_barang"));
            }

            idBarangArr = ids.toArray(new String[0]);
            hargaArr    = new double[harga.size()];
            stokArr     = new int[stok.size()];
            for (int i = 0; i < harga.size(); i++) {
                hargaArr[i] = harga.get(i);
                stokArr[i]  = stok.get(i);
            }

            if (cmbBarang.getItemCount() > 0) {
                cmbBarang.setSelectedIndex(0);
            }
        } catch (SQLException ex) {
            warn("Gagal memuat barang: " + ex.getMessage());
        } finally {
            Database.closeConnection(conn);
        }
    }

    // ─── Set Tanggal Hari Ini ────────────────────────────────────────
    private void setTanggalHariIni() {
        String tanggal = new SimpleDateFormat("dd MMMM yyyy").format(new Date());
        txtTanggal.setText(tanggal);
    }

    // ─── Set No Faktur Baru ──────────────────────────────────────────
    private void setNoFakturBaru() {
        txtNoFaktur.setText(generateNoFaktur());
    }

    private String generateNoFaktur() {
        String tgl = new SimpleDateFormat("yyyyMMdd").format(new Date());
        long rand = (long)(Math.random() * 9000L) + 1000L; // 4 digit angka acak
        return "INV-" + tgl + "-" + rand;
    }

    // ─── Reset Form ──────────────────────────────────────────────────
    private void resetForm() {
        cartList.clear();
        updateCartTable();
        cmbCustomer.setEnabled(true);
        txtJumlah.setText("");
        txtTotal.setText("");
        if (cmbCustomer.getItemCount() > 0) cmbCustomer.setSelectedIndex(0);
        if (cmbBarang.getItemCount() > 0)   cmbBarang.setSelectedIndex(0);
        setNoFakturBaru();
        txtUangBayar.setText("");
        lblKembalian.setText("Rp 0");
        lblKembalian.setForeground(C_SUCCESS);
        txtJumlah.requestFocus();
    }

    // ─── Helper ───────────────────────────────────────────────────────
    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Poppins", Font.BOLD, 13));
        lbl.setForeground(C_TEXT_LIGHT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField makeTextField(String hint) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Poppins", Font.PLAIN, 13));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setBackground(C_GRAY);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C_BORDER),
                new EmptyBorder(6, 10, 6, 10)));
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
        btn.setFont(new Font("Poppins", Font.BOLD, 13));
        btn.setForeground(C_WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
