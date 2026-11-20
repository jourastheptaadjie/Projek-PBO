package catatuwang.gui;

import catatuwang.model.AppState;
import catatuwang.util.Format;

import java.time.YearMonth;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

/** Tab "Anggaran": mengatur anggaran bulanan dan menampilkan pemakaiannya. */
public class AnggaranController {

    @FXML
    private TextField tfAnggaran;
    @FXML
    private Label lblBulanIni, lblTerpakai, lblSisa, lblPeringatan;
    @FXML
    private ProgressBar pbAnggaran;

    private AppState state;

    public void init(AppState state) {
        this.state = state;
        if (state.getAnggaran() > 0)
            tfAnggaran.setText(String.valueOf(state.getAnggaran()));
        state.tambahPendengar(this::perbarui);
        perbarui();
    }

    @FXML
    private void simpanAnggaran() {
        long nilai;
        try {
            nilai = Long.parseLong(tfAnggaran.getText().trim());
            if (nilai < 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Pesan.peringatan("Anggaran harus berupa angka (0 atau lebih).");
            return;
        }
        state.setAnggaran(nilai);
    }

    /** Menampilkan pemakaian anggaran untuk bulan berjalan. */
    private void perbarui() {
        YearMonth sekarang = YearMonth.now();
        long anggaran = state.getAnggaran();
        long terpakai = state.total(sekarang, false);

        lblBulanIni.setText("Bulan ini: " + Format.namaBulan(sekarang));
        lblTerpakai.setText("Terpakai: " + Format.rupiah(terpakai) + " dari " + Format.rupiah(anggaran));
        lblPeringatan.setText("");

        if (anggaran <= 0) {
            pbAnggaran.setProgress(0);
            lblSisa.setText("Anggaran belum diatur.");
            return;
        }
        pbAnggaran.setProgress(Math.min(1.0, (double) terpakai / anggaran));
        long sisa = anggaran - terpakai;
        if (sisa >= 0) {
            lblSisa.setText("Sisa anggaran: " + Format.rupiah(sisa));
            if (terpakai * 100 >= anggaran * 80) {
                lblPeringatan.setText("Hati-hati! Pengeluaran sudah lebih dari 80% anggaran.");
            }
        } else {
            lblSisa.setText("Melebihi anggaran: " + Format.rupiah(-sisa));
            lblPeringatan.setText("Anggaran bulan ini sudah terlampaui!");
        }
    }
}
