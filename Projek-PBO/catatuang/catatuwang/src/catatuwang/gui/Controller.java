package catatuwang.gui;

import catatuwang.model.AppState;

import javafx.fxml.FXML;

/**
 * Controller utama: membuat data bersama (AppState) lalu menyerahkannya ke
 * controller tiap tab. Logika tiap tab ada di class masing-masing.
 */
public class Controller {

    // Diinjeksi otomatis oleh <fx:include fx:id="..."> pada catatuwang.fxml
    @FXML
    private TransaksiController transaksiTabController;
    @FXML
    private AnggaranController anggaranTabController;
    @FXML
    private RekapController rekapTabController;
    @FXML
    private GrafikController grafikTabController;

    @FXML
    public void initialize() {
        AppState state = new AppState();
        transaksiTabController.init(state);
        anggaranTabController.init(state);
        rekapTabController.init(state);
        grafikTabController.init(state);
    }
}
