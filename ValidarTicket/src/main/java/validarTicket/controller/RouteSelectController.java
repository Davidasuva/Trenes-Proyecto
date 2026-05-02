package validarTicket.controller;

import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import server.model.route.Route;
import validarTicket.factory.ValidarFactory;
import validarTicket.model.ValidarTicketModel;

import java.net.URL;
import java.util.ResourceBundle;

public class RouteSelectController implements Initializable {

    @FXML private ComboBox<Route> cmbRutas;
    @FXML private Button btnIniciar;
    @FXML private Label lblEstado;
    @FXML private Label lblConectando;

    private ValidarTicketModel model;
    private ObservableList<Route> rutas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbRutas.setItems(rutas);
        cmbRutas.setConverter(new javafx.util.StringConverter<Route>() {
            @Override public String toString(Route r) {
                if (r == null) return "";
                return r.getName() + "  ·  " + r.getOrigin().getName()
                        + " → " + r.getDestiny().getName()
                        + "  ·  Salida: " + r.getDateTravelStr();
            }
            @Override public Route fromString(String s) { return null; }
        });
        cmbRutas.getSelectionModel().selectedItemProperty().addListener((obs, old, nueva) ->
                btnIniciar.setDisable(nueva == null));
        btnIniciar.setDisable(true);
    }

    public void setModel(ValidarTicketModel model) {
        this.model = model;
        lblConectando.setVisible(true);
        new Thread(() -> {
            boolean ok = model.connect();
            Platform.runLater(() -> {
                lblConectando.setVisible(false);
                if (ok) {
                    cargarRutas();
                } else {
                    lblEstado.setText("❌  No se pudo conectar al servidor.");
                    lblEstado.setVisible(true);
                }
            });
        }).start();
    }

    private void cargarRutas() {
        rutas.clear();
        LinkedList<Route> lista = model.getAvailableRoutes();
        Iterator<Route> it = lista.iterator();
        while (it.hasNext()) rutas.add(it.next());
        if (rutas.isEmpty()) {
            lblEstado.setText("No hay rutas activas en este momento.");
            lblEstado.setVisible(true);
        } else {
            lblEstado.setVisible(false);
        }
    }

    @FXML private void handleIniciar() {
        Route sel = cmbRutas.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        model.setRutaSeleccionada(sel);
        Stage stage = (Stage) cmbRutas.getScene().getWindow();
        ValidarFactory.showValidarPasajero(stage, model);
    }

    @FXML private void handleRefrescar() { cargarRutas(); }
}
