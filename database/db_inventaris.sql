DROP DATABASE IF EXISTS db_inventaris;
CREATE DATABASE db_inventaris
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_inventaris;

CREATE TABLE tb_user (
    id_user      INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    nama_lengkap VARCHAR(100),
    level        ENUM('Admin', 'Petugas') NOT NULL DEFAULT 'Petugas'
) ENGINE=InnoDB;

-- Data awal user
INSERT INTO tb_user (username, password, nama_lengkap, level) VALUES
('admin',   'admin123',   'Administrator',  'Admin'),
('petugas', 'petugas123', 'Petugas Gudang', 'Petugas');

CREATE TABLE tb_kategori (
    id_kategori   INT AUTO_INCREMENT PRIMARY KEY,
    nama_kategori VARCHAR(50) NOT NULL
) ENGINE=InnoDB;

-- Data awal kategori
INSERT INTO tb_kategori (nama_kategori) VALUES
('Elektronik'),
('Makanan & Minuman'),
('Peralatan Rumah'),
('Pakaian'),
('Lainnya');

CREATE TABLE tb_barang (
    id_barang   VARCHAR(10)  PRIMARY KEY,
    id_kategori INT          NOT NULL,
    nama_barang VARCHAR(100) NOT NULL,
    satuan      VARCHAR(20)  NOT NULL,
    harga_jual  DOUBLE       NOT NULL DEFAULT 0,
    stok        INT          NOT NULL DEFAULT 0,
    FOREIGN KEY (id_kategori) REFERENCES tb_kategori(id_kategori)
) ENGINE=InnoDB;


INSERT INTO tb_barang VALUES
('BRG001', 1, 'Laptop Asus',      'Unit', 8500000, 10),
('BRG002', 1, 'Mouse Wireless',   'Unit',  125000, 50),
('BRG003', 2, 'Mie Instan Goreng','Dus',    85000, 100),
('BRG004', 3, 'Sapu Lantai',      'Buah',   35000, 30),
('BRG005', 1, 'Keyboard Mechanical', 'Unit',  450000, 25),
('BRG006', 1, 'Monitor LED 24 Inch', 'Unit', 1750000, 15),
('BRG007', 1, 'Speaker Bluetooth',  'Unit',  350000, 40),
('BRG008', 2, 'Kopi Kemasan',        'Pack',  15000, 200),
('BRG009', 2, 'Biskuit Kaleng',     'Kaleng', 45000, 50),
('BRG010', 2, 'Air Mineral 600ml',  'Dus',   50000, 80),
('BRG011', 3, 'Lampu LED 12W',       'Buah',  45000, 150),
('BRG012', 3, 'Kipas Angin Dinding', 'Unit', 250000, 20),
('BRG013', 3, 'Setrika Listrik',     'Unit', 180000, 15),
('BRG014', 4, 'Kaos Polos Hitam',    'Pcs',   65000, 100),
('BRG015', 4, 'Kemeja Flanel',       'Pcs',  135000, 45),
('BRG016', 4, 'Celana Jeans Panjang','Pcs',  185000, 35),
('BRG017', 5, 'Buku Tulis Kotak',    'Pack',  38000, 60),
('BRG018', 5, 'Pulpen Gel Hitam',    'Kotak', 24000, 120),
('BRG019', 5, 'Masker Medis 3-Ply',  'Box',   15000, 150);

CREATE TABLE tb_customer (
    id_customer   VARCHAR(10)  PRIMARY KEY,
    nama_customer VARCHAR(100) NOT NULL,
    alamat        TEXT,
    telepon       VARCHAR(15)
) ENGINE=InnoDB;

INSERT INTO tb_customer VALUES
('CUST001', 'Budi Santoso',  'Jl. Merdeka No. 10, Jakarta',  '08123456789'),
('CUST002', 'Siti Rahayu',   'Jl. Sudirman No. 5, Tangerang', '08234567890'),
('CUST003', 'Ahmad Fauzi',   'Jl. Gatot Subroto No. 3, Depok','08345678901');

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

