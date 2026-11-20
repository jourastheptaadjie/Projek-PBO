package catatuwang.gui;

import javafx.scene.control.Alert;

/** Dialog pesan sederhana untuk pengguna. */
public final class Pesan {

    private Pesan() {
    }

    public static void peringatan(String teks) {
        new Alert(Alert.AlertType.WARNING, teks).showAndWait();
    }
}
