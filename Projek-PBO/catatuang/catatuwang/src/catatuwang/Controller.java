package catatuwang;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Controller {

    private static final String[] KATEGORI_PENGELUARAN =
            {"Makanan & Minuman", "Transportasi", "Kuliah & Buku", "Hiburan", "Kos & Tagihan", "Lainnya"};
    private static final String[] KATEGORI_PEMASUKAN =
            {"Uang Saku", "Beasiswa", "Gaji / Kerja Sampingan", "Lainnya"};

    // ----- komponen tab Transaksi -----
    @FXML private ComboBox<String> cbJenis;
    @FXML private ComboBox<String> cbKategori;
    @FXML private TextField tfJumlah;
    @FXML private DatePicker dpTanggal;
    @FXML private TextField tfCatatan;
    @FXML private TableView<Transaksi> tabelTransaksi;
    @FXML private TableColumn<Transaksi, String> colTanggal, colJenis, colKategori, colJumlah, colCatatan;

    // ----- komponen tab Anggaran -----
    @FXML private TextField tfAnggaran;
    @FXML private Label lblBulanIni, lblTerpakai, lblSisa, lblPeringatan;
    @FXML private ProgressBar pbAnggaran;

    // ----- komponen tab Rekap -----
    @FXML private ComboBox<YearMonth> cbBulan;
    @FXML private Label lblPemasukan, lblPengeluaran, lblSaldo, lblAnggaranRekap;
    @FXML private TableView<String[]> tabelRekap;
    @FXML private TableColumn<String[], String> colRekapKategori, colRekapTotal, colRekapPersen;

    // ----- data -----
    private final ObservableList<Transaksi> daftar = FXCollections.observableArrayList();
    private final ObservableList<String[]> barisRekap = FXCollections.observableArrayList();
    private long anggaran = 0;

    @FXML
    public void initialize() {
        anggaran = DataStore.muat(daftar);

        // form transaksi
        cbJenis.getItems().addAll("Pemasukan", "Pengeluaran");
        cbJenis.setOnAction(e -> isiKategori());
        cbJenis.setValue("Pengeluaran");
        isiKategori();
        dpTanggal.setValue(LocalDate.now());

        // tabel transaksi
        colTanggal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTanggal().toString()));
        colJenis.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getJenis()));
        colKategori.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKategori()));
        colJumlah.setCellValueFactory(c -> new SimpleStringProperty(rupiah(c.getValue().getJumlah())));
        colCatatan.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCatatan()));
        tabelTransaksi.setItems(daftar);
        tabelTransaksi.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // tabel rekap
        colRekapKategori.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[0]));
        colRekapTotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[1]));
        colRekapPersen.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[2]));
        tabelRekap.setItems(barisRekap);
        tabelRekap.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // anggaran
        if (anggaran > 0) tfAnggaran.setText(String.valueOf(anggaran));

        // rekap: pilihan bulan
        cbBulan.setOnAction(e -> hitungRekap());
        perbaruiPilihanBulan(YearMonth.now());
        perbaruiAnggaran();
    }

    // ===================== TRANSAKSI =====================

    private void isiKategori() {
        cbKategori.getItems().setAll(cbJenis.getValue().equals("Pemasukan")
                ? KATEGORI_PEMASUKAN : KATEGORI_PENGELUARAN);
        cbKategori.getSelectionModel().selectFirst();
    }

    @FXML
    private void tambahTransaksi() {
        long jumlah;
        try {
            jumlah = Long.parseLong(tfJumlah.getText().trim());
        } catch (NumberFormatException e) {
            pesan("Jumlah harus berupa angka bulat, contoh: 25000");
            return;
        }
        if (jumlah <= 0) {
            pesan("Jumlah harus lebih dari 0.");
            return;
        }
        if (dpTanggal.getValue() == null) {
            pesan("Tanggal belum dipilih.");
            return;
        }

        daftar.add(new Transaksi(dpTanggal.getValue(), cbJenis.getValue(), cbKategori.getValue(),
                jumlah, tfCatatan.getText().trim()));
        daftar.sort(Comparator.comparing(Transaksi::getTanggal).reversed());
        tfJumlah.clear();
        tfCatatan.clear();
        simpanDanSegarkan();
    }

    @FXML
    private void hapusTransaksi() {
        Transaksi dipilih = tabelTransaksi.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            pesan("Pilih dulu transaksi yang ingin dihapus.");
            return;
        }
        daftar.remove(dipilih);
        simpanDanSegarkan();
    }

    // ===================== ANGGARAN =====================

    @FXML
    private void simpanAnggaran() {
        try {
            long nilai = Long.parseLong(tfAnggaran.getText().trim());
            if (nilai < 0) throw new NumberFormatException();
            anggaran = nilai;
        } catch (NumberFormatException e) {
            pesan("Anggaran harus berupa angka (0 atau lebih).");
            return;
        }
        simpanDanSegarkan();
    }

    /** Menampilkan pemakaian anggaran untuk bulan berjalan. */
    private void perbaruiAnggaran() {
        YearMonth sekarang = YearMonth.now();
        long terpakai = totalPengeluaran(sekarang);

        lblBulanIni.setText("Bulan ini: " + namaBulan(sekarang));
        lblTerpakai.setText("Terpakai: " + rupiah(terpakai) + " dari " + rupiah(anggaran));
        lblPeringatan.setText("");

        if (anggaran <= 0) {
            pbAnggaran.setProgress(0);
            lblSisa.setText("Anggaran belum diatur.");
            return;
        }
        pbAnggaran.setProgress(Math.min(1.0, (double) terpakai / anggaran));
        long sisa = anggaran - terpakai;
        if (sisa >= 0) {
            lblSisa.setText("Sisa anggaran: " + rupiah(sisa));
            if (terpakai * 100 >= anggaran * 80) {
                lblPeringatan.setText("Hati-hati! Pengeluaran sudah lebih dari 80% anggaran.");
            }
        } else {
            lblSisa.setText("Melebihi anggaran: " + rupiah(-sisa));
            lblPeringatan.setText("Anggaran bulan ini sudah terlampaui!");
        }
    }

    // ===================== REKAP =====================

    private void perbaruiPilihanBulan(YearMonth pilihanBaru) {
        TreeSet<YearMonth> bulan = new TreeSet<>(Comparator.reverseOrder());
        bulan.add(YearMonth.now());
        for (Transaksi t : daftar) bulan.add(YearMonth.from(t.getTanggal()));

        YearMonth dipilih = cbBulan.getValue() != null ? cbBulan.getValue() : pilihanBaru;
        cbBulan.setOnAction(null);
        cbBulan.getItems().setAll(bulan);
        cbBulan.setValue(bulan.contains(dipilih) ? dipilih : YearMonth.now());
        cbBulan.setOnAction(e -> hitungRekap());
        hitungRekap();
    }

    private void hitungRekap() {
        YearMonth bulan = cbBulan.getValue();
        if (bulan == null) return;

        long masuk = 0, keluar = 0;
        Map<String, Long> perKategori = new LinkedHashMap<>();
        for (Transaksi t : daftar) {
            if (!YearMonth.from(t.getTanggal()).equals(bulan)) continue;
            if (t.isPemasukan()) {
                masuk += t.getJumlah();
            } else {
                keluar += t.getJumlah();
                perKategori.merge(t.getKategori(), t.getJumlah(), Long::sum);
            }
        }

        lblPemasukan.setText(rupiah(masuk));
        lblPengeluaran.setText(rupiah(keluar));
        lblSaldo.setText(rupiah(masuk - keluar));
        lblAnggaranRekap.setText(anggaran > 0
                ? rupiah(anggaran) + (keluar > anggaran ? "  (terlampaui)" : "  (sisa " + rupiah(anggaran - keluar) + ")")
                : "belum diatur");

        barisRekap.clear();
        final long totalKeluar = keluar;
        perKategori.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> barisRekap.add(new String[]{
                        e.getKey(), rupiah(e.getValue()),
                        String.format("%.1f%%", e.getValue() * 100.0 / totalKeluar)}));
    }

    // ===================== UTILITAS =====================

    private void simpanDanSegarkan() {
        DataStore.simpan(anggaran, daftar);
        perbaruiPilihanBulan(cbBulan.getValue());
        perbaruiAnggaran();
    }

    private long totalPengeluaran(YearMonth bulan) {
        long total = 0;
        for (Transaksi t : daftar) {
            if (!t.isPemasukan() && YearMonth.from(t.getTanggal()).equals(bulan)) total += t.getJumlah();
        }
        return total;
    }

    private static String rupiah(long nilai) {
        return "Rp " + String.format("%,d", nilai).replace(',', '.');
    }

    private static String namaBulan(YearMonth ym) {
        String[] nama = {"Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli",
                "Agustus", "September", "Oktober", "November", "Desember"};
        return nama[ym.getMonthValue() - 1] + " " + ym.getYear();
    }

    private void pesan(String teks) {
        new Alert(Alert.AlertType.WARNING, teks).showAndWait();
    }
}
