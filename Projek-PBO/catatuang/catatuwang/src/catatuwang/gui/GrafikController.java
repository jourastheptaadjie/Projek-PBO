package catatuwang.gui;

import catatuwang.model.AppState;
import catatuwang.util.Format;

import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

/** Tab "Grafik": diagram lingkaran pengeluaran/pemasukan per bulan. */
public class GrafikController {

    private static final String TIPE_KELUAR = "Pengeluaran per Kategori";
    private static final String TIPE_MASUK = "Pemasukan per Kategori";
    private static final String TIPE_BANDING = "Perbandingan Pemasukan vs Pengeluaran";

    @FXML
    private ComboBox<YearMonth> cbBulanGrafik;
    @FXML
    private ComboBox<String> cbTipeGrafik;
    @FXML
    private PieChart pieChart;
    @FXML
    private Label lblGrafikKosong;

    private AppState state;

    public void init(AppState state) {
        this.state = state;

        pieChart.setLegendSide(Side.BOTTOM);

        cbTipeGrafik.getItems().addAll(TIPE_KELUAR, TIPE_MASUK, TIPE_BANDING);
        cbTipeGrafik.setValue(TIPE_KELUAR);
        cbTipeGrafik.setOnAction(e -> perbarui());

        PilihanBulan.pasangKonverter(cbBulanGrafik);
        state.tambahPendengar(this::perbaruiPilihanBulan);
        perbaruiPilihanBulan();
    }

    private void perbaruiPilihanBulan() {
        PilihanBulan.isiUlang(cbBulanGrafik, state.bulanTersedia(), this::perbarui);
    }

    private void perbarui() {
        YearMonth bulan = cbBulanGrafik.getValue();
        String tipe = cbTipeGrafik.getValue();
        if (bulan == null || tipe == null)
            return;

        String namaBulan = Format.namaBulan(bulan);
        Map<String, Long> bagian;
        String judul;
        String awalanTooltip = "";

        if (TIPE_KELUAR.equals(tipe)) {
            bagian = state.totalPerKategori(bulan, false);
            if (bagian.isEmpty()) {
                tampilkanKosong("Tidak ada catatan pengeluaran pada " + namaBulan);
                return;
            }
            judul = "Distribusi Pengeluaran (" + namaBulan + ")";
        } else if (TIPE_MASUK.equals(tipe)) {
            bagian = state.totalPerKategori(bulan, true);
            if (bagian.isEmpty()) {
                tampilkanKosong("Tidak ada catatan pemasukan pada " + namaBulan);
                return;
            }
            judul = "Distribusi Pemasukan (" + namaBulan + ")";
        } else {
            long masuk = state.total(bulan, true);
            long keluar = state.total(bulan, false);
            if (masuk + keluar == 0) {
                tampilkanKosong("Tidak ada transaksi pada " + namaBulan);
                return;
            }
            bagian = new LinkedHashMap<>();
            if (masuk > 0)
                bagian.put("Pemasukan", masuk);
            if (keluar > 0)
                bagian.put("Pengeluaran", keluar);
            judul = "Perbandingan Pemasukan vs Pengeluaran (" + namaBulan + ")";
            awalanTooltip = "Total ";
        }

        pieChart.setTitle(judul);
        pieChart.setData(buatIrisan(bagian, awalanTooltip));
        pieChart.setVisible(true);
        lblGrafikKosong.setVisible(false);
    }

    private ObservableList<PieChart.Data> buatIrisan(Map<String, Long> bagian, String awalanTooltip) {
        long total = bagian.values().stream().mapToLong(Long::longValue).sum();
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        bagian.forEach((nama, jumlah) -> {
            String persen = Format.persen(jumlah * 100.0 / total);
            PieChart.Data irisan = new PieChart.Data(nama + " (" + persen + ")", jumlah);
            pasangTooltip(irisan, awalanTooltip + nama + ": " + Format.rupiah(jumlah) + " (" + persen + ")");
            data.add(irisan);
        });
        return data;
    }

    private void tampilkanKosong(String pesan) {
        pieChart.setData(FXCollections.observableArrayList());
        pieChart.setVisible(false);
        lblGrafikKosong.setText(pesan);
        lblGrafikKosong.setVisible(true);
    }

    private void pasangTooltip(PieChart.Data data, String info) {
        if (data.getNode() != null) {
            Tooltip.install(data.getNode(), new Tooltip(info));
        } else {
            data.nodeProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    Tooltip.install(newVal, new Tooltip(info));
                }
            });
        }
    }
}
