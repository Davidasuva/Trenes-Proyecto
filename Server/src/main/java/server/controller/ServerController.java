package server.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import server.factory.ServerFactory;
import server.model.ServerModel;
import server.model.history.History;
import server.model.observer.Observer;

public class ServerController {

    @FXML private Label    lblStatus;
    @FXML private Button   btnDeploy;
    @FXML private Button   btnIrMenu;
    @FXML private TextArea txtConsola;

    private ServerModel model;

    public void setModel(ServerModel model) {
        this.model = model;
        suscribirseAlHistory(model.getHistory());
    }

    @FXML
    public void handleDeploy() {
        Stage stage = (Stage) btnDeploy.getScene().getWindow();

        stage.setOnCloseRequest(event -> shutdownServer());

        if (model.deploy()) {
            lblStatus.setText("● Running");
            lblStatus.getStyleClass().removeAll("status-stopped", "status-failed");
            lblStatus.getStyleClass().add("status-running");
            btnDeploy.setDisable(true);

            // Pre-crear todas las escenas del menú antes de habilitar el botón
            try {
                ServerFactory.buildMenuScenes(model);
                btnIrMenu.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
                txtConsola.appendText("[WARN] No se pudieron pre-cargar las vistas del menú: "
                        + e.getMessage() + "\n");
                btnIrMenu.setDisable(false); // igual se habilita; el error se verá al navegar
            }
        } else {
            lblStatus.setText("● Failed");
            lblStatus.getStyleClass().removeAll("status-stopped", "status-running");
            lblStatus.getStyleClass().add("status-failed");
        }
    }

    @FXML
    public void handleIrMenu() {
        Stage stage = (Stage) btnIrMenu.getScene().getWindow();
        // Navegar directo a la primera vista del menú: Rutas
        ServerFactory.navigateToRoutes(stage);
    }

    // ── Consola: observer del History ────────────────────────────────────────

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
        if (model != null) model.stop();
    }
}
