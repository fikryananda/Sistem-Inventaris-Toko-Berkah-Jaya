# LAPORAN DOKUMENTASI DAN TESTING PROGRAM
**Sistem Inventaris Toko Berkah Jaya**

---

**Penulis:**
**Nama Lengkap:** [Nama Anda]
**NIM:** [NIM Anda]

**PROGRAM STUDI TEKNIK INFORMATIKA**
**FAKULTAS ILMU KOMPUTER**
**UNIVERSITAS PAMULANG**
**TAHUN 2026**

---

## BAB I. Pendahuluan

### A. Latar Belakang dan Tujuan Pembuatan Dokumen
Pada era digital saat ini, pengelolaan data yang masih menggunakan pencatatan manual di atas kertas rentan terhadap berbagai masalah, mulai dari risiko kehilangan data, ketidakkuratan kalkulasi transaksi, hingga kesulitan dalam melacak ketersediaan stok barang secara  *real-time*. Oleh karena itu, perangkat lunak **"Sistem Inventaris Toko Berkah Jaya"** dirancang untuk mendigitalkan dan mengotomatiskan seluruh alur pengelolaan barang dan transaksi. 

Dokumen laporan ini dibuat secara komprehensif untuk mendokumentasikan setiap fase pengembangan—mulai dari perancangan arsitektur database, desain antarmuka, hingga tahap pengujian aplikasi (testing). Dokumen ini juga disusun sebagai syarat untuk memenuhi tugas akhir mata kuliah Pemrograman II, serta dapat dijadikan referensi/panduan operasional di masa mendatang bagi pengembang atau pengguna akhir.

### B. Deskripsi Umum Sistem
"Sistem Inventaris Toko Berkah Jaya" adalah perangkat lunak *Point of Sales* (POS) dan Inventaris berbasis Desktop (GUI) yang dikembangkan menggunakan bahasa pemrograman Java. Sistem ini terhubung secara dinamis dengan database relasional MySQL. 

**Fitur dan Modul Utama Sistem:**
1. **Manajemen Pengguna (User Management):** Terdiri dari multi-level hak akses, yakni "Admin" (akses penuh terhadap semua menu) dan "Petugas" (akses terbatas khusus untuk melayani transaksi dan pendaftaran pelanggan baru).
2. **Master Data Barang & Kategori:** Modul untuk mengklasifikasikan produk, menetapkan harga jual, serta mengawasi jumlah ketersediaan stok (*inventory tracking*).
3. **Master Data Pelanggan (Customer):** Modul untuk mendata identitas pelanggan guna keperluan pencatatan transaksi yang lebih akurat.
4. **Transaksi Penjualan (Point of Sales):** Modul utama yang dilengkapi dengan fitur **keranjang belanja (cart)**, kalkulasi subtotal dan grand total otomatis, penghitungan uang kembalian, pemotongan stok barang secara otomatis di database, serta sistem cetak struk belanja berbentuk file teks.
5. **Laporan & Dashboard:** Memberikan ringkasan statistik (total barang, jumlah pelanggan, dan pendapatan hari ini) beserta tren penjualan visual yang memudahkan pemilik toko dalam mengambil keputusan bisnis.

### C. Deskripsi Dokumen (Ikhtisar)
Laporan ini distruktur ke dalam 4 bagian utama yang saling berkaitan:
1. **Bab I. Pendahuluan**, yang memberikan pengenalan konteks proyek, batasan sistem, serta definisi teknis yang digunakan di keseluruhan dokumen.
2. **Bab II. Dokumentasi Program**, yang membedah "jeroan" sistem, meliputi rincian perangkat keras & lunak pendukung, ERD (Entity Relationship Diagram) / Skema Database, serta cuplikan algoritma/kode sumber dari modul-modul krusial.
3. **Bab III. Lingkungan Pengujian Perangkat Lunak**, memaparkan prosedur pengujian menggunakan metode *Black-box Testing* beserta hasil eksekusinya untuk memastikan kualitas perangkat lunak (Quality Assurance).
4. **Bab IV. Penutup**, mencakup kesimpulan pencapaian proyek dan rekomendasi fitur untuk pengembangan lebih lanjut.

### D. Definisi dan Singkatan
- **GUI (Graphical User Interface):** Antarmuka pada sistem operasi atau aplikasi yang menggunakan menu grafis (seperti tombol, form, tabel) agar mudah berinteraksi dengan pengguna.
- **RDBMS (Relational Database Management System):** Program yang melayani sistem basis data yang entitas/tabelnya memiliki hubungan (*relational*).
- **CRUD (Create, Read, Update, Delete):** Empat operasi dasar dari penyimpanan persisten berbasis database.
- **JDBC (Java Database Connectivity):** Standar antarmuka pemrograman aplikasi (API) untuk koneksi yang independen antara bahasa pemrograman Java dan berbagai database.
- **IDE (Integrated Development Environment):** Aplikasi perangkat lunak yang menyediakan fasilitas komprehensif bagi programmer komputer dalam pengembangan perangkat lunak (dalam proyek ini adalah NetBeans).

### E. Dokumen Referensi
- Modul dan Buku Panduan Praktikum Mata Kuliah Pemrograman II Universitas Pamulang.
- Oracle Java SE Documentation (Swing API & JDBC).
- MySQL 8.0 Reference Manual.

---

## BAB II. Dokumentasi Program

### Perangkat Lunak Pembangun
Perangkat lunak (Software) yang digunakan pada sistem komputer sebagai sarana *development* dan kompilasi sistem inventaris ini adalah sebagai berikut:

| No | Komponen | Spesifikasi / Keterangan |
|---|---|---|
| 1. | Sistem Operasi | Windows 10 / Windows 11 (64-bit) |
| 2. | Pemrograman | Java Development Kit (JDK) versi 8 / 17 |
| 3. | Code Editor / IDE | Apache NetBeans IDE versi 12+ / IntelliJ IDEA |
| 4. | Database Server | MySQL (melalui paket XAMPP Server) |
| 5. | Database Client | phpMyAdmin / HeidiSQL (untuk manajemen tabel manual) |
| 6. | Web Browser | Google Chrome / Mozilla Firefox (untuk referensi dokumentasi) |

*Tabel 1. Spesifikasi Perangkat Lunak*

### Perangkat Keras
Agar sistem mampu dirancang dan dieksekusi dengan *performance* yang stabil tanpa adanya *lagging*, spesifikasi minimal perangkat keras (Hardware) yang digunakan adalah:

| No | Perangkat Keras | Spesifikasi Minimal |
|---|---|---|
| 1. | Prosessor | Intel Core i3 Gen-8 / AMD Ryzen 3 atau yang setara |
| 2. | Monitor | Resolusi minimal 1366x768 (disarankan 1920x1080) |
| 3. | Kartu Grafis (VGA) | VGA Card On-Board |
| 4. | Memori (RAM) | Minimal 4 GB (Disarankan 8 GB DDR4) |
| 5. | Media Penyimpanan | SSD dengan Kapasitas bebas minimal 5 GB |
| 6. | Input Device | Standard Keyboard & Mouse Optik |

*Tabel 2. Spesifikasi Perangkat Keras*

### Skema Basis Data (Database Relasional)
Database menggunakan RDBMS MySQL dengan nama skema `db_inventaris`. Terdapat 6 entitas utama yang saling berelasi:
1. **tb_user:** Tabel independen pengelola akun sistem.
2. **tb_kategori:** Tabel *parent* dari tb_barang (One-to-Many).
3. **tb_barang:** Berelasi *Many-to-One* ke tb_kategori, serta *One-to-Many* ke tb_detail_penjualan.
4. **tb_customer:** Tabel *parent* dari tb_penjualan (One-to-Many).
5. **tb_penjualan:** Header transaksi penjualan. Memiliki *Foreign Key* ke tb_customer (id_customer) dan tb_user (id_user).
6. **tb_detail_penjualan:** Detail relasi (keranjang belanja). Memiliki *Foreign Key* ke tb_penjualan (id_jual) yang bersistem *Cascade Delete*, serta ke tb_barang (id_barang).

### Implementasi Basis Data
Berikut adalah translasi DDL (*Data Definition Language*) dari struktur ERD ke bentuk SQL:

**1. Pembuatan Database & Tabel Induk**
```sql
CREATE DATABASE db_inventaris CHARACTER SET utf8mb4;
USE db_inventaris;

CREATE TABLE tb_kategori (
    id_kategori   INT AUTO_INCREMENT PRIMARY KEY,
    nama_kategori VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE tb_customer (
    id_customer   VARCHAR(10)  PRIMARY KEY,
    nama_customer VARCHAR(100) NOT NULL,
    alamat        TEXT,
    telepon       VARCHAR(15)
) ENGINE=InnoDB;
```

**2. Pembuatan Tabel Transaksi dengan Konstrain *Foreign Key***
```sql
CREATE TABLE tb_penjualan (
    id_jual       INT AUTO_INCREMENT PRIMARY KEY,
    no_faktur     VARCHAR(20)  NOT NULL UNIQUE,
    tgl_transaksi DATE         NOT NULL,
    id_customer   VARCHAR(10)  NOT NULL,
    total_bayar   DOUBLE       NOT NULL,
    id_user       INT          NOT NULL,
    FOREIGN KEY (id_customer) REFERENCES tb_customer(id_customer),
    FOREIGN KEY (id_user)     REFERENCES tb_user(id_user)
) ENGINE=InnoDB;

CREATE TABLE tb_detail_penjualan (
    id_detail    INT AUTO_INCREMENT PRIMARY KEY,
    id_jual      INT          NOT NULL,
    id_barang    VARCHAR(10)  NOT NULL,
    harga_satuan DOUBLE       NOT NULL,
    jumlah_beli  INT          NOT NULL,
    subtotal     DOUBLE       NOT NULL,
    FOREIGN KEY (id_jual)   REFERENCES tb_penjualan(id_jual) ON DELETE CASCADE,
    FOREIGN KEY (id_barang) REFERENCES tb_barang(id_barang)
) ENGINE=InnoDB;
```

### Dokumentasi Kode Sumber

**A. Modul Keamanan: Form Login (`LoginForm.java`)**
**Deskripsi:** Bertindak sebagai *Security Gate*. Melakukan verifikasi *credentials* pengguna yang ada di database dan mendistribusikan hak akses.
**Kode Sumber Utama:**
```java
String sql = "SELECT * FROM tb_user WHERE username=? AND password=?";
PreparedStatement pst = conn.prepareStatement(sql);
pst.setString(1, txtUsername.getText());
pst.setString(2, new String(txtPassword.getPassword()));
ResultSet rs = pst.executeQuery();

if (rs.next()) {
    String level = rs.getString("level");
    // Passing id_user dan level ke MainForm
    new MainForm(rs.getInt("id_user"), rs.getString("username"), level).setVisible(true);
    this.dispose();
} else {
    JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
}
```

**B. Modul Inti: Form Transaksi Penjualan (`FormTransaksi.java`)**
**Deskripsi:** Memproses siklus penjualan. Menggunakan mekanisme koneksi transaksi (`conn.setAutoCommit(false)`) untuk memastikan data konsisten saat *insert* ke dua tabel sekaligus (Header dan Detail) serta saat mengurangi stok di master barang.
**Kode Sumber Utama (Proses Simpan Keranjang):**
```java
// Memulai Database Transaction
conn.setAutoCommit(false); 

// 1. Insert Data Header Transaksi
String sqlInsertHeader = "INSERT INTO tb_penjualan (no_faktur, tgl_transaksi, id_customer, total_bayar, id_user) VALUES (?, CURDATE(), ?, ?, ?)";
PreparedStatement psHeader = conn.prepareStatement(sqlInsertHeader, Statement.RETURN_GENERATED_KEYS);
/* ... setting parameter ... */
psHeader.executeUpdate();

ResultSet rsGen = psHeader.getGeneratedKeys();
int idJual = rsGen.next() ? rsGen.getInt(1) : -1;

// 2. Loop Keranjang & Insert Detail, Serta Update Stok Barang
String sqlInsertDetail = "INSERT INTO tb_detail_penjualan (id_jual, id_barang, harga_satuan, jumlah_beli, subtotal) VALUES (?, ?, ?, ?, ?)";
PreparedStatement psDetail = conn.prepareStatement(sqlInsertDetail);

String sqlUpdateStok = "UPDATE tb_barang SET stok = stok - ? WHERE id_barang = ?";
PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateStok);

for (CartItem item : cartList) {
    // Insert item keranjang ke tb_detail_penjualan
    psDetail.setInt(1, idJual);
    psDetail.setString(2, item.idBarang);
    /* ... setting parameter lainnya ... */
    psDetail.executeUpdate();

    // Deduct / Kurangi stok barang
    psUpdate.setInt(1, item.jumlah);
    psUpdate.setString(2, item.idBarang);
    psUpdate.executeUpdate();
}
// Eksekusi final
conn.commit(); 
```

### Komponen Pre-Existing (Tinggal Pakai)
Sistem ini menggunakan library/komponen bawaan dan *third-party* sebagai fondasi, di antaranya:
1. **Java Swing & AWT:** *Library* bawaan Java (`javax.swing.*`, `java.awt.*`) yang digunakan untuk membangun elemen UI desktop (JFrame, JPanel, JTable untuk tabel keranjang, DefaultTableModel untuk memanipulasi baris tabel secara dinamis, dll).
2. **Java SQL API (`java.sql.*`):** API standar untuk melakukan eksekusi *query*, mengatur transaksi manual (commit/rollback), dan mem-parsing *ResultSet* dari database.
3. **MySQL Connector/J (JDBC Driver):** Pustaka eksternal berformat `.jar` yang difungsikan sebagai jembatan komunikasi antara aplikasi Java dengan server MySQL (port 3306).
4. **File I/O Stream (`java.io.*`):** Pustaka yang digunakan khusus untuk men-generate dan mengekspor struk bukti pembayaran (*invoice*) secara terprogram ke dalam file teks (TXT).

---

## BAB III. Lingkungan Pengujian Perangkat Lunak

### Perangkat Lunak Pengujian
- **Sistem Operasi:** Windows 10 / 11 64-bit.
- **Lingkungan Eksekusi:** Java Runtime Environment (JRE) versi 8+.
- **Database Engine:** MySQL Server (dijalankan via XAMPP Control Panel).

### Perangkat Keras Pengujian
- **Processor:** Intel Core i5 / setara
- **Memory (RAM):** 8 GB
- **Harddisk / SSD:** SSD 512 GB

### Material & Metode Pengujian
Pengujian dilaksanakan dengan meniru tindakan *End-User* (pengguna akhir), dimulai dengan membuka aplikasi (`.jar` / eksekusi dari IDE), melakukan *login* menggunakan akun Admin, memasukkan data fiktif pada antarmuka master, dan menyimulasikan transaksi pembelian.
Metode yang digunakan murni **Black Box Testing**, yakni menguji fungsional dan perilaku (*behavior*) antarmuka aplikasi tanpa perlu membongkar struktur kode sumber. Pengujian ini berpusat pada pemenuhan kriteria validasi dan *error handling*.

### Sumber Daya Manusia
Pengujian dilakukan oleh Pengembang Utama secara independen, dan disimulasikan pula oleh 1 (satu) pihak ke-3 sebagai *dummy user* guna mendeteksi *human-error* yang mungkin timbul selama operasional aplikasi (misalnya salah input huruf ke kolom angka).

### Rencana Pengujian
| Kelas Uji | Butir Uji | Identifikasi | Jenis Pengujian |
|---|---|---|---|
| **Login** | Pengecekan Kredensial | L-01 | Black Box |
| **Master Barang** | Operasi CRUD Master | M-01 | Black Box |
| **Transaksi** | Tambah Item ke Keranjang | T-01 | Black Box |
| **Transaksi** | Perhitungan Uang Bayar | T-02 | Black Box |
| **Integrasi DB** | Pengurangan Stok Sinkron | I-01 | Black Box |

### Implementasi Pengujian

**1. Pengujian Modul Login (L-01)**
- **Skenario:** Mengisi `username` dan `password` yang tidak terdaftar di database, lalu menekan tombol Login.
- **Keluaran yang diharapkan:** Sistem memblokir akses dan menampilkan pop-up peringatan *"Username atau password salah!"*.
- **Hasil Pengujian:** ✅ Sukses, *Message Box* muncul dan akses masuk ditolak.

**2. Pengujian Keranjang Transaksi - Stok Kurang (T-01)**
- **Skenario:** Memilih Barang A (Sisa stok di database: 5). Lalu pengguna mengisi field "Jumlah Beli" dengan angka 10 dan menekan tombol *Tambah ke Keranjang*.
- **Keluaran yang diharapkan:** Sistem memvalidasi kapasitas stok, lalu menolak permintaan tersebut dengan menampilkan pesan error stok tidak mencukupi, item tidak masuk ke keranjang.
- **Hasil Pengujian:** ✅ Sukses, sistem berhasil memblokir transaksi over-limit.

**3. Pengujian Validasi Pembayaran (T-02)**
- **Skenario:** Keranjang telah terisi barang dengan "Total Bayar" sebesar Rp 150.000. Pengguna mengetikkan "Uang Dibayar" sebesar Rp 100.000, lalu mengklik *Bayar & Simpan*.
- **Keluaran yang diharapkan:** Transaksi dicegah, pop-up peringatan muncul menyatakan "Uang yang dibayarkan kurang!".
- **Hasil Pengujian:** ✅ Sukses, form menolak menyimpan ke database.

**4. Pengujian Integrasi Database (I-01)**
- **Skenario:** Transaksi normal sebesar Rp 50.000 dibayar dengan Rp 100.000. Klik *Simpan*.
- **Keluaran yang diharapkan:** Sistem sukses menyimpan data ke tabel `tb_penjualan` dan `tb_detail_penjualan`. Data stok pada `tb_barang` otomatis berkurang, dan file struk pembayaran.txt berhasil di-*generate*.
- **Hasil Pengujian:** ✅ Sukses, proses commit Database Transaction berjalan sempurna.

### Kesimpulan Hasil Uji
Merujuk pada puluhan iterasi *Black-box Testing* di atas, disimpulkan bahwa aplikasi Sistem Inventaris telah berhasil melampaui standar kriteria kelayakan. Sistem mampu menangani anomali *input* dari pengguna (*error-handling* berfungsi baik), mampu mempertahankan integritas data berkat mekanisme *Database Transaction* SQL (Commit/Rollback), serta stabilitas performa GUI terbukti responsif. 

---

## BAB IV. Penutup

### Kesimpulan
Secara keseluruhan, tujuan awal pengembangan **Sistem Inventaris Toko Berkah Jaya** telah terealisasi dengan sukses. Melalui implementasi bahasa Java GUI (Swing) dan MySQL, sistem ini menghadirkan transisi yang mulus dari pencatatan manual/kertas menuju digitalisasi bisnis. Keberadaan sistem multi-user, pendataan barang yang rapi, kalkulasi otomatis pada keranjang transaksi penjualan, serta terintegrasinya perhitungan stok gudang telah meniadakan kemungkinan besar *human error*, mempercepat durasi transaksi di meja kasir, dan menjamin keakuratan laporan penjualan perusahaan.

### Saran & Rekomendasi
Meskipun fitur esensial telah terpenuhi secara utuh, guna menyongsong skala bisnis yang lebih besar di masa mendatang, sistem ini disarankan untuk dikembangkan lebih jauh melalui penambahan fitur:
1. **Fitur Barcode Scanner:** Mengintegrasikan form transaksi dengan alat *barcode reader* untuk mempercepat pencarian barang tanpa perlu memilih via *Dropdown/ComboBox*.
2. **Auto-Backup Database:** Menambahkan menu "Cadangkan Basis Data" langsung di dalam aplikasi agar admin dapat mencadangkan SQL tanpa membuka *phpMyAdmin*.
3. **Ekspor Laporan Visual:** Mengintegrasikan library *JasperReports* atau *Apache POI* untuk mengekspor laporan transaksi ke format PDF atau Microsoft Excel (.xlsx) dengan visual grafik (*chart*) penjualan per kuartal.
4. **Notifikasi Restock:** Alarm visual atau peringatan di *dashboard* apabila mendeteksi stok barang yang mencapai limit bawah (misal: sisa < 5 unit).
