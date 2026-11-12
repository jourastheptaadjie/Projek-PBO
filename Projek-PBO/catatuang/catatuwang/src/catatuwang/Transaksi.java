package catatuwang;

import java.time.LocalDate;

/** Satu catatan pemasukan atau pengeluaran. */
public class Transaksi {

    private final LocalDate tanggal;
    private final String jenis;      // "Pemasukan" atau "Pengeluaran"
    private final String kategori;
    private final long jumlah;       // dalam Rupiah
    private final String catatan;

    public Transaksi(LocalDate tanggal, String jenis, String kategori, long jumlah, String catatan) {
        this.tanggal = tanggal;
        this.jenis = jenis;
        this.kategori = kategori;
        this.jumlah = jumlah;
        this.catatan = catatan;
    }

    public LocalDate getTanggal() { return tanggal; }
    public String getJenis() { return jenis; }
    public String getKategori() { return kategori; }
    public long getJumlah() { return jumlah; }
    public String getCatatan() { return catatan; }

    public boolean isPemasukan() { return jenis.equals("Pemasukan"); }
}
