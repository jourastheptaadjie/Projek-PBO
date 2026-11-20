package catatuwang.gui;

import catatuwang.util.Format;

import java.time.YearMonth;
import java.util.Collection;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

/** Pengaturan ComboBox bulan yang dipakai oleh tab Rekap dan Grafik. */
public final class PilihanBulan {

    private PilihanBulan() {
    }

    /** Menampilkan item YearMonth sebagai nama bulan (mis. "Maret 2025"). */
    public static void pasangKonverter(ComboBox<YearMonth> cb) {
        cb.setConverter(new StringConverter<>() {
            @Override
            public String toString(YearMonth ym) {
                return ym != null ? Format.namaBulan(ym) : "";
            }

            @Override
            public YearMonth fromString(String s) {
                return null;
            }
        });
    }

    /**
     * Mengisi ulang pilihan bulan tanpa memicu aksi, mempertahankan pilihan
     * sebelumnya bila masih ada (jika tidak, bulan ini), lalu menjalankan aksi
     * satu kali.
     */
    public static void isiUlang(ComboBox<YearMonth> cb, Collection<YearMonth> bulan, Runnable aksi) {
        YearMonth dipilih = cb.getValue() != null ? cb.getValue() : YearMonth.now();
        cb.setOnAction(null);
        cb.getItems().setAll(bulan);
        cb.setValue(bulan.contains(dipilih) ? dipilih : YearMonth.now());
        cb.setOnAction(e -> aksi.run());
        aksi.run();
    }
}
