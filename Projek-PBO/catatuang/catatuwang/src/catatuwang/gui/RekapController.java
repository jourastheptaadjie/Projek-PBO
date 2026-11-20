package catatuwang.gui;

import catatuwang.model.AppState;
import catatuwang.util.Format;

import java.time.YearMonth;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/** Tab "Rekap": ringkasan pemasukan/pengeluaran per bulan. */
public class RekapController {

    @FXML
    private ComboBox<YearMonth> cbBulan;
    @FXML
    private Label lblPemasukan, lblPengeluaran, lblSaldo, lblAnggaranRekap;
    @FXML
    private TableView<String[]> tabelRekap;
    @FXML
    private TableColumn<String[], String> colRekapKategori, colRekapTotal, colRekapPersen;

    private final ObservableList<String[]> barisRekap = FXCollections.observableArrayList();
    private AppState state;

    public void init(AppState state) {
        this.state = state;

        colRekapKategori.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[0]));
        colRekapTotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[1]));
        colRekapPersen.setCellValueFactory(c -> new SimpleStringProperty(c.getValue()[2]));
        tabelRekap.setItems(barisRekap);
        tabelRekap.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        PilihanBulan.pasangKonverter(cbBulan);
        state.tambahPendengar(this::perbaruiPilihanBulan);
        perbaruiPilihanBulan();
    }

    private void perbaruiPilihanBulan() {
        PilihanBulan.isiUlang(cbBulan, state.bulanTersedia(), this::hitungRekap);
    }

    private void hitungRekap() {
        YearMonth bulan = cbBulan.getValue();
        if (bulan == null)
            return;

        long masuk = state.total(bulan, true);
        long keluar = state.total(bulan, false);
        long anggaran = state.getAnggaran();

        lblPemasukan.setText(Format.rupiah(masuk));
        lblPengeluaran.setText(Format.rupiah(keluar));
        lblSaldo.setText(Format.rupiah(masuk - keluar));
        lblAnggaranRekap.setText(anggaran > 0
                ? Format.rupiah(anggaran)
                        + (keluar > anggaran ? "  (terlampaui)" : "  (sisa " + Format.rupiah(anggaran - keluar) + ")")
                : "belum diatur");

        barisRekap.clear();
        state.totalPerKategori(bulan, false).forEach((kategori, jumlah) -> barisRekap.add(new String[] {
                kategori, Format.rupiah(jumlah), Format.persen(jumlah * 100.0 / keluar) }));
    }
}
