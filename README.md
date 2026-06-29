# 🏪 Sistem Inventaris Toko Berkah Jaya

Halo! Ini adalah project **Sistem Inventaris** yang saya bangun sebagai tugas mata kuliah **Pemrograman 2, Semester 6**. Aplikasi ini adalah sistem manajemen inventaris berbasis desktop untuk toko, dibangun menggunakan **Java Swing** dengan koneksi database **MySQL** melalui JDBC.

---

## 🧑‍💻 Tentang Project Ini

Saya membangun aplikasi ini untuk membantu pengelolaan toko secara digital — mulai dari manajemen barang, data pelanggan, proses transaksi penjualan, hingga laporan pendapatan. Semua tampilan saya desain dengan tema **dark mode (Slate Dark)** menggunakan Java Swing murni, tanpa library UI tambahan.

### ⚙️ Teknologi yang Saya Gunakan
- **Java Swing** — untuk tampilan antarmuka (GUI) desktop
- **MySQL** — database penyimpanan data
- **JDBC** — untuk koneksi Java ke MySQL
- **NetBeans IDE** — IDE yang saya pakai untuk pengembangan
- **XAMPP** — untuk menjalankan server MySQL secara lokal
- **Font Poppins** — custom font yang saya embed langsung ke aplikasi

---

## ✨ Fitur-Fitur yang Ada

| Modul | Deskripsi |
|---|---|
| 🔐 **Login** | Autentikasi pengguna dengan role Admin & Petugas |
| 📊 **Dashboard** | Ringkasan total barang, customer, dan pendapatan hari ini |
| 🗂️ **Kategori** | Kelola kategori barang (tambah, edit, hapus) |
| 📦 **Data Barang** | Manajemen stok, harga, dan detail barang |
| 👥 **Data Customer** | Kelola data pelanggan toko |
| 🛒 **Transaksi** | Proses penjualan dengan fitur keranjang belanja, cetak struk otomatis |
| 📋 **Laporan** | Riwayat transaksi dan laporan pendapatan |
| 👤 **Data Petugas** | Manajemen akun kasir/petugas (khusus Admin) |

---

## 📁 Struktur Project

```
SistemInventaris/
├── build.xml                          ← build script Ant (NetBeans)
├── manifest.mf
├── database/
│   └── db_inventaris.sql              ← script SQL, import ini dulu!
├── lib/
│   └── mysql-connector-j-8.0.33.jar  ← driver koneksi Java-MySQL
├── nbproject/                         ← konfigurasi NetBeans
└── src/sisteminventaris/
    ├── Main.java                      ← entry point aplikasi
    ├── Database.java                  ← konfigurasi koneksi MySQL
    ├── LoginForm.java                 ← halaman login
    ├── MainForm.java                  ← dashboard utama + sidebar
    ├── FormKategori.java              ← manajemen kategori
    ├── FormBarang.java                ← manajemen barang & stok
    ├── FormCustomer.java              ← manajemen customer
    ├── FormTransaksi.java             ← proses penjualan & struk
    ├── FormLaporan.java               ← laporan & riwayat transaksi
    ├── FormPetugas.java               ← manajemen akun petugas
    └── fonts/                         ← font Poppins (embedded)
```

---

## 🚀 Cara Menjalankan Project Ini

### 1. Clone Repository
```bash
git clone https://github.com/fikryananda/Sistem-Inventaris-Toko-Berkah-Jaya.git
```

### 2. Buka di NetBeans
1. Buka **NetBeans IDE**
2. Klik **File → Open Project...**
3. Arahkan ke folder hasil clone
4. Klik **Open Project**

### 3. Setup Database
1. Pastikan **XAMPP** berjalan — aktifkan **Apache** dan **MySQL**
2. Buka **phpMyAdmin** → [http://localhost/phpmyadmin](http://localhost/phpmyadmin)
3. Buat database baru bernama `db_inventaris`
4. Klik tab **Import** → pilih file `database/db_inventaris.sql` → klik **Go**

### 4. Pastikan Library MySQL Connector Ada
Library sudah saya sertakan di folder `lib/`. Tapi kalau NetBeans tidak mengenali, tambahkan manual:
1. Di NetBeans → klik kanan **Libraries** → **Add JAR/Folder**
2. Pilih file `lib/mysql-connector-j-8.0.33.jar`

### 5. Jalankan
- Klik kanan project → **Run** atau tekan **F6**
- Form login akan muncul

---

## 🔑 Akun Default

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | Admin (akses penuh) |
| `petugas` | `petugas123` | Petugas/Kasir |

---

## 🔧 Konfigurasi Koneksi Database

Jika perlu mengubah konfigurasi database, edit file `src/sisteminventaris/Database.java`:

```java
private static final String HOST     = "localhost";
private static final String PORT     = "3306";
private static final String DB_NAME  = "db_inventaris";
private static final String USERNAME = "root";
private static final String PASSWORD = "";   // kosong = default XAMPP
```

---

## 🗂️ Penjelasan File Koding

Berikut penjelasan dari setiap file Java yang ada di dalam folder `src/sisteminventaris/`:

---

### 📄 `Main.java`
File ini adalah **titik masuk (entry point)** dari seluruh aplikasi. Tugasnya ada dua:
1. **Mendaftarkan font Poppins** (Regular, Medium, Bold) ke dalam sistem grafis Java agar bisa dipakai di semua form
2. **Menjalankan `LoginForm`** di dalam *Event Dispatch Thread* (EDT) — ini adalah cara yang benar untuk menjalankan aplikasi Java Swing agar thread-safe

Intinya, file ini yang pertama kali dieksekusi saat aplikasi dibuka.

---

### 📄 `Database.java`
File ini bertanggung jawab untuk **mengelola koneksi ke database MySQL** menggunakan JDBC. Di sinilah saya menyimpan semua konfigurasi koneksi (host, port, nama database, username, password).

Terdiri dari dua method utama:
- `getConnection()` — membuka dan mengembalikan objek koneksi ke MySQL
- `closeConnection()` — menutup koneksi dengan aman setelah selesai digunakan

Semua form lain memanggil `Database.getConnection()` setiap kali perlu mengakses data dari database.

---

### 📄 `LoginForm.java`
File ini adalah **tampilan form login** yang pertama kali muncul saat aplikasi dibuka. Saya desain tampilannya dengan dua panel — panel kiri berisi branding/logo dengan gradient biru gelap, dan panel kanan berisi form input.

Fitur yang saya implementasikan:
- Input username dan password dengan validasi
- Checkbox "Tampilkan Password" untuk show/hide password
- Autentikasi langsung ke tabel `users` di database
- Membedakan role `admin` dan `petugas` — jika login berhasil, akan langsung membuka `MainForm` dan meneruskan data user yang login
- Tampilan error/sukses dengan label status berwarna

---

### 📄 `MainForm.java`
File ini adalah **dashboard utama** setelah login berhasil. Ini adalah jendela terbesar yang menjadi "rumah" dari semua menu.

Yang saya buat di sini:
- **Sidebar navigasi** di kiri dengan 6 menu (Kategori, Barang, Customer, Transaksi, Laporan, Petugas), masing-masing punya warna aksen berbeda
- **Kartu statistik** di bagian atas yang menampilkan total barang, total customer, dan pendapatan hari ini secara real-time dari database
- **Sidebar bisa disembunyikan/ditampilkan** dengan tombol toggle
- Setiap klik menu akan membuka form yang sesuai sebagai dialog

---

### 📄 `FormKategori.java`
File ini menangani **manajemen data kategori barang**. Kategori digunakan sebagai pengelompokan barang (misalnya: Makanan, Minuman, Elektronik, dll.).

Fitur CRUD yang saya buat:
- **Tambah** kategori baru dengan ID otomatis
- **Edit** data kategori yang sudah ada
- **Hapus** kategori (dengan konfirmasi)
- **Batal** untuk reset form
- Tabel menampilkan semua data kategori dari database

---

### 📄 `FormBarang.java`
File ini adalah **manajemen data barang** — salah satu form terpenting dalam sistem. Di sini pengguna bisa mengelola semua produk yang dijual di toko.

Fitur yang ada:
- CRUD lengkap (Tambah, Edit, Hapus, Batal)
- Dropdown **kategori** yang datanya diambil dari tabel kategori (relasi antar tabel)
- Input **harga jual** dengan format Rupiah otomatis
- Input **stok** barang
- **Kolom pencarian real-time** — saat mengetik di kolom cari, tabel langsung filter otomatis
- Bisa langsung **tambah kategori baru** dari form ini tanpa harus keluar

---

### 📄 `FormCustomer.java`
File ini menangani **manajemen data pelanggan/customer** toko. Data customer dibutuhkan saat proses transaksi.

Fitur yang saya buat:
- CRUD data customer: nama, nomor telepon, dan alamat
- ID customer di-generate otomatis
- **Kolom pencarian real-time** untuk cari customer dengan cepat
- Klik baris di tabel akan otomatis mengisi form input (untuk keperluan edit)

---

### 📄 `FormTransaksi.java`
File ini adalah **inti dari proses penjualan**. Ini form paling kompleks yang saya buat, dengan fitur keranjang belanja layaknya aplikasi kasir.

Alur yang saya rancang:
1. Pilih **customer** dari dropdown
2. Pilih **barang** dari dropdown — harga dan stok akan otomatis tampil
3. Masukkan **jumlah** beli → klik **Tambah ke Keranjang**
4. Barang masuk ke **tabel keranjang**, grand total dihitung otomatis
5. Masukkan **uang bayar** → kembalian dihitung otomatis
6. Klik **Simpan Transaksi** → data tersimpan ke database, stok barang berkurang otomatis
7. **Struk transaksi** otomatis dibuat dan disimpan sebagai file `.txt` di folder `struk/`

Nomor faktur di-generate otomatis dengan format `INV-YYYYMMDD-XXXX`.

---

### 📄 `FormLaporan.java`
File ini menampilkan **laporan riwayat seluruh transaksi penjualan**. Saya buat ini agar admin/petugas bisa memantau rekap penjualan.

Yang bisa dilihat di sini:
- Tabel riwayat transaksi dengan kolom: No Faktur, Tanggal, Customer, Total Bayar, Kasir
- **Klik baris transaksi** → tampil detail item barang yang dibeli dalam transaksi tersebut di panel bawah
- Ringkasan di bagian atas: total jumlah transaksi dan total pendapatan keseluruhan
- **Pencarian real-time** berdasarkan nomor faktur atau nama customer
- Tombol **Refresh** untuk memuat ulang data terbaru

---

### 📄 `FormPetugas.java`
File ini adalah **manajemen akun pengguna** (kasir/petugas). Hanya bisa diakses oleh role **Admin**.

Fitur yang saya implementasikan:
- CRUD akun petugas: nama lengkap, username, password, role (admin/petugas)
- **Proteksi** — akun yang sedang login tidak bisa menghapus dirinya sendiri
- Pencarian real-time berdasarkan nama atau username
- Role dapat diatur melalui dropdown (Admin atau Petugas)

---

## 📸 Tampilan Aplikasi

Aplikasi ini menggunakan tema **dark mode** dengan warna dominan **Slate Dark** dan aksen biru. Beberapa highlight tampilan:
- Sidebar navigasi dengan ikon dan deskripsi menu
- Dashboard dengan kartu statistik (total barang, customer, pendapatan)
- Form transaksi dengan sistem keranjang belanja dan cetak struk otomatis ke file `.txt`
- Semua form menggunakan font **Poppins** untuk tampilan yang lebih modern

---

## 📌 Catatan

- Aplikasi ini dikembangkan untuk keperluan akademik (tugas kuliah)
- Database menggunakan MySQL lokal via XAMPP, pastikan service MySQL aktif sebelum menjalankan aplikasi
- Struk transaksi tersimpan otomatis di folder `struk/` dalam direktori project

---

*Dibuat oleh **Fikry Ananda** — Pemrograman 2, Semester 6*
