package catatuwang.gui;

import catatuwang.model.AppState;
import catatuwang.util.Format;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

/** Tab "Grafik": pie chart per kategori dan line chart pemasukan/pengeluaran per hari. */
public class GrafikController {

    private static final String TIPE_KELUAR = "Pengeluaran per Kategori";
    private static final String TIPE_MASUK = "Pemasukan per Kategori";
    private static final String TIPE_HARIAN = "Pemasukan & Pengeluaran per Hari";
    private static final String TIPE_BANDING = "Perbandingan Pemasukan vs Pengeluaran";

    @FXML
    private ComboBox<YearMonth> cbBulanGrafik;
    @FXML
    private ComboBox<String> cbTipeGrafik;
    @FXML
    private PieChart pieChart;
    @FXML
    private LineChart<String, Number> lineChart;
    @FXML
    private Label lblGrafikKosong;

    private AppState state;

    public void init(AppState state) {
        this.state = state;

        pieChart.setLegendSide(Side.BOTTOM);
        lineChart.setAnimated(false);
        lineChart.setLegendSide(Side.BOTTOM);

        cbTipeGrafik.getItems().addAll(TIPE_KELUAR, TIPE_MASUK, TIPE_BANDING, TIPE_HARIAN);
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

        if (TIPE_HARIAN.equals(tipe)) {
            tampilkanHarian(bulan);
            return;
        }

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
        lineChart.setVisible(false);
        lblGrafikKosong.setVisible(false);
    }

    /** Line chart pemasukan dan pengeluaran untuk setiap tanggal pada bulan terpilih. */
    private void tampilkanHarian(YearMonth bulan) {
        long[] masuk = state.totalPerHari(bulan, true);
        long[] keluar = state.totalPerHari(bulan, false);
        String namaBulan = Format.namaBulan(bulan);
        if (Arrays.stream(masuk).sum() + Arrays.stream(keluar).sum() == 0) {
            tampilkanKosong("Tidak ada transaksi pada " + namaBulan);
            return;
        }

        // Daftar tanggal dipasang eksplisit agar sumbu X ikut jumlah hari bulan terpilih
        ObservableList<String> tanggal = FXCollections.observableArrayList();
        for (int i = 1; i <= masuk.length; i++)
            tanggal.add(String.valueOf(i));
        ((CategoryAxis) lineChart.getXAxis()).setCategories(tanggal);

        lineChart.setTitle("Pemasukan & Pengeluaran per Hari (" + namaBulan + ")");
        lineChart.getData().setAll(
                buatSeri("Pemasukan", masuk, namaBulan),
                buatSeri("Pengeluaran", keluar, namaBulan));
        lineChart.setVisible(true);
        pieChart.setVisible(false);
        lblGrafikKosong.setVisible(false);
    }

    private XYChart.Series<String, Number> buatSeri(String nama, long[] perHari, String namaBulan) {
        XYChart.Series<String, Number> seri = new XYChart.Series<>();
        seri.setName(nama);
        for (int i = 0; i < perHari.length; i++) {
            XYChart.Data<String, Number> titik = new XYChart.Data<>(String.valueOf(i + 1), perHari[i]);
            pasangTooltip(titik.nodeProperty(), nama + " " + (i + 1) + " " + namaBulan + ": "
                    + Format.rupiah(perHari[i]));
            seri.getData().add(titik);
        }
        return seri;
    }

    private ObservableList<PieChart.Data> buatIrisan(Map<String, Long> bagian, String awalanTooltip) {
        long total = bagian.values().stream().mapToLong(Long::longValue).sum();
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        bagian.forEach((nama, jumlah) -> {
            String persen = Format.persen(jumlah * 100.0 / total);
            PieChart.Data irisan = new PieChart.Data(nama + " (" + persen + ")", jumlah);
            pasangTooltip(irisan.nodeProperty(), awalanTooltip + nama + ": " + Format.rupiah(jumlah) + " (" + persen + ")");
            data.add(irisan);
        });
        return data;
    }

    private void tampilkanKosong(String pesan) {
        pieChart.setData(FXCollections.observableArrayList());
        lineChart.getData().clear();
        pieChart.setVisible(false);
        lineChart.setVisible(false);
        lblGrafikKosong.setText(pesan);
        lblGrafikKosong.setVisible(true);
    }

    private void pasangTooltip(ReadOnlyObjectProperty<Node> nodeProperty, String info) {
        if (nodeProperty.get() != null) {
            Tooltip.install(nodeProperty.get(), new Tooltip(info));
        } else {
            nodeProperty.addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    Tooltip.install(newVal, new Tooltip(info));
                }
            });
        }
    }
}
