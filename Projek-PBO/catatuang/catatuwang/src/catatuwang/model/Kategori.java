package catatuwang.model;

/** Daftar kategori yang tersedia untuk tiap jenis transaksi. */
public final class Kategori {

    public static final String[] PENGELUARAN = { "Makanan & Minuman", "Transportasi", "Kuliah & Buku",
            "Hiburan", "Kos & Tagihan", "Lainnya" };
    public static final String[] PEMASUKAN = { "Uang Saku", "Beasiswa", "Gaji / Kerja Sampingan", "Lainnya" };

    private Kategori() {
    }

    public static String[] untuk(String jenis) {
        return Transaksi.PEMASUKAN.equals(jenis) ? PEMASUKAN : PENGELUARAN;
    }
}
