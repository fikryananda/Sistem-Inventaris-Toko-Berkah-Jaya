# 📦 Sistem Inventaris — Pemrograman 2

Form Login Java Swing + MySQL (XAMPP)

---

## 📁 Struktur Project

```
SistemInventaris/
├── build.xml                         ← file build Ant (NetBeans)
├── manifest.mf
├── database/
│   └── db_inventaris.sql             ← script SQL (jalankan di phpMyAdmin)
├── lib/
│   └── mysql-connector-j-8.0.33.jar  ← DOWNLOAD MANUAL (lihat langkah 2)
├── nbproject/
│   ├── project.xml
│   └── project.properties
└── src/
    └── sisteminventaris/
        ├── Main.java                 ← entry point
        ├── Database.java             ← koneksi MySQL
        └── LoginForm.java            ← form login (UI Swing)
```

---

## 🚀 Cara Import ke NetBeans

### Langkah 1 — Buka Project di NetBeans
1. Buka **NetBeans IDE**
2. Klik **File → Open Project...**
3. Navigasi ke folder `SistemInventaris`
4. Klik **Open Project**

### Langkah 2 — Tambah Library MySQL Connector
> ⚠️ File connector **harus didownload sendiri** karena ukurannya besar.

1. Download **mysql-connector-j-8.0.33.jar** dari:
   - https://dev.mysql.com/downloads/connector/j/  
     (pilih "Platform Independent" → file `.zip` → ekstrak `.jar`-nya)
   - **Atau** pakai yang sudah ada di XAMPP:  
     `C:\xampp\phpMyAdmin\vendor\...` *(biasanya tidak ada)*  
   - **Alternatif cepat**: search Google `mysql-connector-j-8.0.33.jar download`
   
2. Buat folder `lib/` di dalam folder project:
   ```
   C:\kulyeahh\Semester 6\Pemrograman 2\SistemInventaris\lib\
   ```
3. Taruh file `.jar` ke dalam folder `lib/`

4. Di NetBeans → klik kanan **Libraries** → **Add JAR/Folder** → pilih file `.jar` tadi

### Langkah 3 — Setup Database MySQL
1. Pastikan **XAMPP** berjalan (Apache + MySQL aktif)
2. Buka **phpMyAdmin** → http://localhost/phpmyadmin
3. Klik **Import** → pilih file `database/db_inventaris.sql` → klik **Go**
4. Selesai! Database `db_inventaris` dan tabel `users` sudah terbuat.

### Langkah 4 — Jalankan Project
1. Di NetBeans, klik kanan **project** → **Run** (atau tekan **F6**)
2. Form login akan muncul
3. Gunakan akun default:

   | Username | Password    | Role     |
   |----------|-------------|----------|
   | admin    | admin123    | admin    |
   | petugas  | petugas123  | petugas  |

---

## 🎨 Tampilan Form Login
- Panel kiri: branding/ilustrasi dengan gradient biru
- Panel kanan: form input username + password
- Fitur: placeholder teks, tampilkan/sembunyikan password, validasi input, notifikasi error/sukses
- Koneksi: langsung ke MySQL via JDBC

---

## 🔧 Konfigurasi Database (jika perlu diubah)
Edit file `src/sisteminventaris/Database.java`:
```java
private static final String HOST     = "localhost";
private static final String PORT     = "3306";
private static final String DB_NAME  = "db_inventaris";
private static final String USERNAME = "root";
private static final String PASSWORD = "";   // kosong = default XAMPP
```

---

## 📌 Catatan Pengembangan Selanjutnya
- [ ] Tambah halaman dashboard utama (`MainForm.java`)
- [ ] Tambah CRUD barang/inventaris
- [ ] Tambah laporan stok
- [ ] Enkripsi password (MD5/BCrypt)
