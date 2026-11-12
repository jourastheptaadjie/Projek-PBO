package catatuwang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Menyimpan data ke file teks sederhana (data.txt) agar data tidak hilang saat aplikasi ditutup.
 * Format: baris pertama = anggaran bulanan, baris berikutnya = satu transaksi (dipisah tanda '|').
 */
public class DataStore {

    private static final Path FILE = Path.of("data.txt");

    public static void simpan(long anggaran, List<Transaksi> daftar) {
        List<String> baris = new ArrayList<>();
        baris.add(String.valueOf(anggaran));
        for (Transaksi t : daftar) {
            baris.add(t.getTanggal() + "|" + t.getJenis() + "|" + t.getKategori() + "|"
                    + t.getJumlah() + "|" + t.getCatatan().replace("|", " "));
        }
        try {
            Files.write(FILE, baris);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan data: " + e.getMessage());
        }
    }

    /** Mengisi daftar dengan transaksi dari file dan mengembalikan anggaran bulanan (0 jika belum ada). */
    public static long muat(List<Transaksi> daftar) {
        if (!Files.exists(FILE)) return 0;
        try {
            List<String> baris = Files.readAllLines(FILE);
            long anggaran = Long.parseLong(baris.get(0).trim());
            for (int i = 1; i < baris.size(); i++) {
                String[] p = baris.get(i).split("\\|", 5);
                daftar.add(new Transaksi(LocalDate.parse(p[0]), p[1], p[2], Long.parseLong(p[3]),
                        p.length > 4 ? p[4] : ""));
            }
            return anggaran;
        } catch (Exception e) {
            System.err.println("Gagal membaca data: " + e.getMessage());
            return 0;
        }
    }
}
