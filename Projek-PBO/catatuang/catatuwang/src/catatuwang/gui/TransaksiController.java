package catatuwang.gui;

import catatuwang.model.AppState;
import catatuwang.model.Kategori;
import catatuwang.model.Transaksi;
import catatuwang.util.Format;

import java.time.LocalDate;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/** Tab "Transaksi": form tambah transaksi dan tabel riwayat. */
public class TransaksiController {

    @FXML
    private ComboBox<String> cbJenis;
    @FXML
    private ComboBox<String> cbKategori;
    @FXML
    private TextField tfJumlah;
    @FXML
    private DatePicker dpTanggal;
    @FXML
    private TextField tfCatatan;
    @FXML
    private TableView<Transaksi> tabelTransaksi;
    @FXML
    private TableColumn<Transaksi, String> colTanggal, colJenis, colKategori, colJumlah, colCatatan;

    private AppState state;

    public void init(AppState state) {
        this.state = state;

        cbJenis.getItems().addAll(Transaksi.PEMASUKAN, Transaksi.PENGELUARAN);
        cbJenis.setOnAction(e -> isiKategori());
        cbJenis.setValue(Transaksi.PENGELUARAN);
        isiKategori();
        dpTanggal.setValue(LocalDate.now());

        colTanggal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTanggal().toString()));
        colJenis.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getJenis()));
        colKategori.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKategori()));
        colJumlah.setCellValueFactory(c -> new SimpleStringProperty(Format.rupiah(c.getValue().getJumlah())));
        colCatatan.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCatatan()));
        tabelTransaksi.setItems(state.getDaftar());
        tabelTransaksi.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void isiKategori() {
        cbKategori.getItems().setAll(Kategori.untuk(cbJenis.getValue()));
        cbKategori.getSelectionModel().selectFirst();
    }

    @FXML
    private void tambahTransaksi() {
        long jumlah;
        try {
            jumlah = Long.parseLong(tfJumlah.getText().trim());
        } catch (NumberFormatException e) {
            Pesan.peringatan("Jumlah harus berupa angka bulat, contoh: 25000");
            return;
        }
        if (jumlah <= 0) {
            Pesan.peringatan("Jumlah harus lebih dari 0.");
            return;
        }
        if (dpTanggal.getValue() == null) {
            Pesan.peringatan("Tanggal belum dipilih.");
            return;
        }

        tfJumlah.clear();
        String catatan = tfCatatan.getText().trim();
        tfCatatan.clear();
        state.tambah(new Transaksi(dpTanggal.getValue(), cbJenis.getValue(), cbKategori.getValue(),
                jumlah, catatan));
    }

    @FXML
    private void hapusTransaksi() {
        Transaksi dipilih = tabelTransaksi.getSelectionModel().getSelectedItem();
        if (dipilih == null) {
            Pesan.peringatan("Pilih dulu transaksi yang ingin dihapus.");
            return;
        }
        state.hapus(dipilih);
    }
}
