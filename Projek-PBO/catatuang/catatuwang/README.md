# CatatUwang - Personal Finance Tracker 💰

**CatatUwang** adalah aplikasi pencatatan dan manajemen keuangan pribadi (*personal finance tracker*) berbasis desktop yang dibangun menggunakan bahasa pemrograman **Java** dan antarmuka **JavaFX**. Proyek ini dikembangkan sebagai bagian dari pemenuhan tugas **Pemrograman Berorientasi Objek (PBO)** dengan mengadopsi prinsip-prinsip OOP.

# Dikembangkan oleh:
- Ilham Arya Pratama - L0125015
- Mahendra Gian Kurniawan - L0125051
- Jourast Hepta Adjie - L0125104

---

## 🌟 Fitur Utama

### 1. Pencatatan Transaksi
- **Pemasukan & Pengeluaran**: Input jenis transaksi, kategori, nominal (Rupiah), tanggal (via `DatePicker`), dan catatan tambahan.
- **Tabel Riwayat Transaksi**: Menampilkan daftar transaksi yang diurutkan secara otomatis dari tanggal terbaru.
- **Hapus Transaksi**: Kemudahan menghapus transaksi yang dipilih langsung dari tabel.

### 2. Manajemen Anggaran (Budgeting)
- **Target Anggaran Bulanan**: Tetapkan batas maksimal pengeluaran setiap bulannya.
- **Indikator Visual (`ProgressBar`)**: Memantau persentase pemakaian anggaran secara langsung.
- **Peringatan Otomatis**:
  - Peringatan waspada saat pengeluaran telah mencapai $\ge 80\%$ dari anggaran.
  - Peringatan jika pengeluaran melebihi batas anggaran yang ditentukan.

### 3. Rekapitulasi Keuangan Bulanan
- **Filter Berdasarkan Bulan**: Memilih periode bulan transaksi yang ingin dianalisis.
- **Ringkasan Finansial**: Menampilkan total pemasukan, total pengeluaran, saldo bersih (*cash flow*), dan sisa anggaran.
- **Tabel Rincian Kategori**: Rincian total pengeluaran beserta proporsi persentase untuk setiap kategori.

### 4. Visualisasi Statistik (Pie Chart) 📊
- **Diagram Lingkaran Interaktif**: Mempermudah pembacaan distribusi keuangan bulanan secara visual.
- **3 Mode Tampilan Grafik**:
  1. *Distribusi Pengeluaran per Kategori*
  2. *Distribusi Pemasukan per Kategori*
  3. *Perbandingan Total Pemasukan vs Pengeluaran*
- **Fitur Interaktif**:
  - Label irisan grafik dilengkapi dengan persentase.
  - *Tooltip* informatif yang muncul saat kursor diarahkan ke irisan diagram (menampilkan nama kategori, nominal Rupiah, dan persentase).
  - Penanganan status kosong (*empty state*) jika belum ada transaksi pada bulan yang dipilih.

### 5. Persistensi Data Otomatis
- Data transaksi dan anggaran disimpan secara lokal ke dalam file `data.txt`.
- Data tersimpan secara otomatis setiap kali ada penambahan atau penghapusan transaksi sehingga tidak hilang saat aplikasi ditutup.

---

## 🏗️ Struktur Arsitektur & Penerapan Konsep PBO

Aplikasi ini dirancang dengan pendekatan modular berbasis arsitektur **MVC (Model-View-Controller)**:

```
src/catatuwang/
├── Main.java          # Entry point aplikasi JavaFX, pengaturan Stage & Icon
├── Controller.java    # Logika antarmuka, event handler, kalkulasi rekap, dan visualisasi grafik
├── Transaksi.java     # Model representasi entitas data transaksi
├── DataStore.java     # Pengelolaan penyimpanan & pemuatan file lokal (data.txt)
├── catatuwang.fxml    # Definisi tata letak UI (XML berbasis JavaFX)
└── icon.png           # Ikon aplikasi
```

### Konsep OOP yang Diimplementasikan:
1. **Encapsulation (Enkapsulasi)**:
   - Kelas `Transaksi` menyembunyikan atribut (`private`) dan menyediakan akses terkontrol melalui *getter*.
2. **Separation of Concerns (Pemisahan Tanggung Jawab)**:
   - Logika I/O file dikapsulasi pada kelas `DataStore`, terpisah dari logika antarmuka di `Controller`.
3. **Data Binding & Collections**:
   - Pemanfaatan `ObservableList`, `SimpleStringProperty`, dan `TableView` JavaFX untuk sinkronisasi otomatis antara model dan tampilan UI.

---

## 📋 Prasyarat Sistem

- **Java Development Kit (JDK)**: Java 21 atau yang lebih baru (disarankan JDK 23 / 25).
- **JavaFX SDK**: Kompatibel dengan versi JDK yang digunakan (modul `javafx.controls` dan `javafx.fxml`).
- **IDE**: Visual Studio Code (dengan ekstensi *Extension Pack for Java*) atau IDE Java lainnya (IntelliJ IDEA / Eclipse).

---

## 🚀 Cara Menjalankan Proyek

### Menjalankan melalui Visual Studio Code:
1. Buka folder proyek di VS Code.
2. Pastikan file [.vscode/launch.json](file:///.vscode/launch.json) telah mengarahkan `--module-path` ke folder library JavaFX Anda:
   ```json
   "vmArgs": "--module-path \"C:\\javafx\\lib\" --add-modules javafx.controls,javafx.fxml"
   ```
3. Tekan **F5** atau klik menu **Run > Start Debugging**.

### Menjalankan melalui Terminal:
Kompilasi kode program:
```powershell
javac --module-path "C:\javafx\lib" --add-modules javafx.controls,javafx.fxml -d bin src\catatuwang\*.java
```
Jalankan aplikasi:
```powershell
java --module-path "C:\javafx\lib" --add-modules javafx.controls,javafx.fxml -cp bin catatuwang.Main
```
*(Sesuaikan path `"C:\javafx\lib"` dengan direktori instalasi JavaFX di komputer Anda).*

---

## 📝 Format Penyimpanan Data (`data.txt`)

Penyimpanan lokal menggunakan format teks terstruktur sederhana:
- **Baris 1**: Nilai total anggaran bulanan (contoh: `250000`).
- **Baris 2 dst**: Catatan transaksi dengan pemisah `|`:
  ```text
  <Tanggal>|<Jenis>|<Kategori>|<Jumlah>|<Catatan>
  ```
  *Contoh:*
  ```text
  250000
  2026-11-16|Pemasukan|Uang Saku|250000|
  2026-11-16|Pengeluaran|Makanan & Minuman|25000|Makan siang
  2026-11-16|Pengeluaran|Transportasi|30000|Bensin motor
  ```
