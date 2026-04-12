package server.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import server.model.ServerModel;
import server.model.history.History;
import server.model.observer.Observer;

public class ServerController {

    @FXML private Label    lblStatus;
    @FXML private Button   btnDeploy;
    @FXML private TextArea txtConsola;

    private ServerModel model;

    public void setModel(ServerModel model) {
        this.model = model;
        suscribirseAlHistory(model.getHistory());
    }

    @FXML
    public void handleDeploy() {
        Stage stage = (Stage) btnDeploy.getScene().getWindow();

        // Registrar cierre limpio al cerrar la ventana
        stage.setOnCloseRequest(event -> {
            shutdownServer();
            // System.exit(0) lo maneja Main.stop(), no hace falta aquí
        });

        if (model.deploy()) {
            lblStatus.setText("Status: Running");
            btnDeploy.setDisable(true);
        } else {
            lblStatus.setText("Status: Failed");
        }
    }

    // ──────────────────────────────────────────────────────────
    // Consola: observer del History
    // ──────────────────────────────────────────────────────────

    private void suscribirseAlHistory(History history) {
        new Observer(history) {
            @Override
            public void update() {
                String linea = history.getLastAction();
                Platform.runLater(() -> {
                    txtConsola.appendText(linea + "\n");
                    txtConsola.setScrollTop(Double.MAX_VALUE);
                });
            }
        };
    }

    private void shutdownServer() {
        if (model != null) {
            model.stop();
        }
    }
}