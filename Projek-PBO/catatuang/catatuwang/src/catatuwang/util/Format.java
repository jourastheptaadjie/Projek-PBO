package catatuwang.util;

import java.time.YearMonth;

/** Fungsi bantu untuk format tampilan (Rupiah, nama bulan, persen). */
public final class Format {

    private static final String[] NAMA_BULAN = { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli",
            "Agustus", "September", "Oktober", "November", "Desember" };

    private Format() {
    }

    public static String rupiah(long nilai) {
        return "Rp " + String.format("%,d", nilai).replace(',', '.');
    }

    public static String namaBulan(YearMonth ym) {
        return NAMA_BULAN[ym.getMonthValue() - 1] + " " + ym.getYear();
    }

    public static String persen(double nilai) {
        return String.format("%.1f%%", nilai);
    }
}
