package catatuwang.model;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Data aplikasi yang dipakai bersama semua tab: daftar transaksi dan anggaran
 * bulanan. Setiap perubahan disimpan ke file lalu diberitahukan ke pendengar
 * (tab) agar tampilannya diperbarui.
 */
public class AppState {

    private final ObservableList<Transaksi> daftar = FXCollections.observableArrayList();
    private final List<Runnable> pendengar = new ArrayList<>();
    private long anggaran;

    public AppState() {
        anggaran = DataStore.muat(daftar);
    }

    // ----- akses data -----

    public ObservableList<Transaksi> getDaftar() {
        return daftar;
    }

    public long getAnggaran() {
        return anggaran;
    }

    public void tambahPendengar(Runnable aksi) {
        pendengar.add(aksi);
    }

    // ----- perubahan data -----

    public void tambah(Transaksi t) {
        daftar.add(t);
        daftar.sort(Comparator.comparing(Transaksi::getTanggal).reversed());
        simpanDanBeritahu();
    }

    public void hapus(Transaksi t) {
        daftar.remove(t);
        simpanDanBeritahu();
    }

    public void setAnggaran(long nilai) {
        anggaran = nilai;
        simpanDanBeritahu();
    }

    private void simpanDanBeritahu() {
        DataStore.simpan(anggaran, daftar);
        for (Runnable r : pendengar)
            r.run();
    }

    // ----- perhitungan -----

    /** Semua bulan yang punya transaksi, ditambah bulan ini (terbaru di atas). */
    public TreeSet<YearMonth> bulanTersedia() {
        TreeSet<YearMonth> bulan = new TreeSet<>(Comparator.reverseOrder());
        bulan.add(YearMonth.now());
        for (Transaksi t : daftar)
            bulan.add(YearMonth.from(t.getTanggal()));
        return bulan;
    }

    public long total(YearMonth bulan, boolean pemasukan) {
        long total = 0;
        for (Transaksi t : daftar) {
            if (t.isPemasukan() == pemasukan && YearMonth.from(t.getTanggal()).equals(bulan))
                total += t.getJumlah();
        }
        return total;
    }

    /** Total pemasukan atau pengeluaran tiap tanggal pada bulan tertentu (indeks 0 = tanggal 1). */
    public long[] totalPerHari(YearMonth bulan, boolean pemasukan) {
        long[] hasil = new long[bulan.lengthOfMonth()];
        for (Transaksi t : daftar) {
            if (t.isPemasukan() == pemasukan && YearMonth.from(t.getTanggal()).equals(bulan))
                hasil[t.getTanggal().getDayOfMonth() - 1] += t.getJumlah();
        }
        return hasil;
    }

    /** Total per kategori pada bulan tertentu, diurutkan dari yang terbesar. */
    public Map<String, Long> totalPerKategori(YearMonth bulan, boolean pemasukan) {
        Map<String, Long> hasil = new LinkedHashMap<>();
        for (Transaksi t : daftar) {
            if (t.isPemasukan() == pemasukan && YearMonth.from(t.getTanggal()).equals(bulan))
                hasil.merge(t.getKategori(), t.getJumlah(), Long::sum);
        }
        Map<String, Long> terurut = new LinkedHashMap<>();
        hasil.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> terurut.put(e.getKey(), e.getValue()));
        return terurut;
    }
}
