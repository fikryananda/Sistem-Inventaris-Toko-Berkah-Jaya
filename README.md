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
