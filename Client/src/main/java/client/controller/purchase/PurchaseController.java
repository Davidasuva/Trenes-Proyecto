package client.controller.purchase;

import client.model.ClientModel;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.model.luggage.Luggage;
import server.model.route.Route;
import server.model.route.Station;
import server.model.ticket.Ticket;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;

import java.util.ArrayList;
import java.util.List;

public class PurchaseController {

    @FXML private Label lblUsuario;
    @FXML private Label lblRutaNombre;
    @FXML private Label lblOrigen;
    @FXML private Label lblDestino;
    @FXML private Label lblDistancia;
    @FXML private Label lblSalida;
    @FXML private Label lblLlegada;
    @FXML private Label lblEstaciones;
    @FXML private Label lblPrecio;

    @FXML private ToggleButton btnEco, btnEjec, btnPrimera;

    @FXML private CheckBox chkMaleta1, chkMaleta2;
    @FXML private VBox panelMaleta1, panelMaleta2;
    @FXML private TextField txtPesoMaleta1, txtPesoMaleta2;
    @FXML private Label lblValidMaleta1, lblValidMaleta2;

    @FXML private TextField txtContactNombre;
    @FXML private TextField txtContactApellido;
    @FXML private TextField txtContactTelefono;
    @FXML private Label lblErrorCompra;
    @FXML private Button btnConfirmarCompra;

    private ClientModel model;
    private Route ruta;
    private ToggleGroup groupCategoria;

    public void init(ClientModel model, Route ruta) {
        this.model = model;
        this.ruta  = ruta;

        // Navbar
        lblUsuario.setText(model.getCurrentPassenger().getName()
                + "  •  " + model.getCurrentPassenger().getMail());

        // Info ruta
        lblRutaNombre.setText(ruta.getName());
        lblOrigen.setText(ruta.getOrigin().getName());
        lblDestino.setText(ruta.getDestiny().getName());
        lblDistancia.setText(String.format("%.0f km", ruta.getTotalDistance()));
        lblSalida.setText(ruta.getDateTravelStr());
        lblLlegada.setText(ruta.getDateArrivalStr());

        // Estaciones intermedias via RouteGraph
        try {
            server.model.route.RouteGraph graph = new server.model.route.RouteGraph();
            LinkedList<Station> path = graph.getShortestPath(ruta.getOrigin(), ruta.getDestiny());
            StringBuilder sb = new StringBuilder();
            Iterator<Station> it = path.iterator();
            boolean first = true;
            while (it.hasNext()) {
                Station s = it.next();
                if (!first) sb.append("  →  ");
                sb.append(s.getName());
                first = false;
            }
            lblEstaciones.setText(sb.toString());
        } catch (Exception e) {
            lblEstaciones.setText(ruta.getOrigin().getName() + "  →  " + ruta.getDestiny().getName());
        }

        // ToggleGroup categoría
        groupCategoria = new ToggleGroup();
        btnEco.setToggleGroup(groupCategoria);
        btnEjec.setToggleGroup(groupCategoria);
        btnPrimera.setToggleGroup(groupCategoria);
        btnEco.setSelected(true);

        // Mostrar precio inicial (categoría estándar = 2)
        actualizarPrecio();

        // Actualizar precio cuando cambia la selección de categoría
        groupCategoria.selectedToggleProperty().addListener((obs, oldT, newT) -> actualizarPrecio());

        // Validación en tiempo real de pesos
        txtPesoMaleta1.textProperty().addListener((obs, o, n) -> validarPeso(n, lblValidMaleta1));
        txtPesoMaleta2.textProperty().addListener((obs, o, n) -> validarPeso(n, lblValidMaleta2));
    }

    @FXML public void toggleMaleta1() {
        boolean show = chkMaleta1.isSelected();
        panelMaleta1.setVisible(show); panelMaleta1.setManaged(show);
        if (!show) { txtPesoMaleta1.clear(); lblValidMaleta1.setText(""); }
    }

    @FXML public void toggleMaleta2() {
        boolean show = chkMaleta2.isSelected();
        panelMaleta2.setVisible(show); panelMaleta2.setManaged(show);
        if (!show) { txtPesoMaleta2.clear(); lblValidMaleta2.setText(""); }
    }

    private void validarPeso(String val, Label lbl) {
        try {
            int w = Integer.parseInt(val.trim());
            if (w > 0 && w <= 80) {
                lbl.setText("✔ OK");
                lbl.setStyle("-fx-text-fill: #065F46;");
            } else {
                lbl.setText("Máx. 80 kg");
                lbl.setStyle("-fx-text-fill: #B91C1C;");
            }
        } catch (NumberFormatException e) {
            lbl.setText(val.isEmpty() ? "" : "Solo números");
            lbl.setStyle("-fx-text-fill: #B91C1C;");
        }
    }

    @FXML public void actualizarPrecio() {
        if (lblPrecio == null || ruta == null || model == null) return;
        ToggleButton sel = (ToggleButton) groupCategoria.getSelectedToggle();
        int cat = 2; // estándar por defecto
        if (sel != null) {
            try { cat = Integer.parseInt(sel.getUserData().toString()); } catch (Exception ignored) {}
        }
        int precio = model.calcularPrecio(ruta, cat);
        lblPrecio.setText(String.format("$ %,d COP", precio));
    }

    @FXML public void handleConfirmar() {
        lblErrorCompra.setVisible(false);

        // Categoría
        ToggleButton sel = (ToggleButton) groupCategoria.getSelectedToggle();
        if (sel == null) { mostrarError("Selecciona una categoría."); return; }
        int categoria = Integer.parseInt(sel.getUserData().toString());

        // Persona de contacto
        String contNombre   = txtContactNombre   != null ? txtContactNombre.getText().trim()   : "";
        String contApellido = txtContactApellido != null ? txtContactApellido.getText().trim() : "";
        String contTelefono = txtContactTelefono != null ? txtContactTelefono.getText().trim() : "";
        if (contNombre.isEmpty() || contApellido.isEmpty() || contTelefono.isEmpty()) {
            mostrarError("Completa los datos de la persona de contacto.");
            return;
        }

        // Maletas a registrar
        LinkedList<Luggage> maletas = new LinkedList<>();
        if (chkMaleta1.isSelected()) {
            try {
                int w = Integer.parseInt(txtPesoMaleta1.getText().trim());
                if (w <= 0 || w > 80) { mostrarError("Maleta 1: el peso debe ser entre 1 y 80 kg."); return; }
                maletas.add(new Luggage(1, w));
            } catch (NumberFormatException e) { mostrarError("Maleta 1: ingresa un peso válido."); return; }
        }
        if (chkMaleta2.isSelected()) {
            try {
                int w = Integer.parseInt(txtPesoMaleta2.getText().trim());
                if (w <= 0 || w > 80) { mostrarError("Maleta 2: el peso debe ser entre 1 y 80 kg."); return; }
                maletas.add(new Luggage(2, w));
            } catch (NumberFormatException e) { mostrarError("Maleta 2: ingresa un peso válido."); return; }
        }

        btnConfirmarCompra.setDisable(true);
        btnConfirmarCompra.setText("Procesando...");

        final String _contNombre = contNombre;
        final String _contApellido = contApellido;
        final String _contTelefono = contTelefono;
        new Thread(() -> {
            Ticket ticket = model.buyTicket(ruta, categoria, maletas);
            if (ticket != null) {
                ticket.setContactName(_contNombre);
                ticket.setContactLastName(_contApellido);
                ticket.setContactPhone(_contTelefono);
            }
            javafx.application.Platform.runLater(() -> {
                btnConfirmarCompra.setDisable(false);
                btnConfirmarCompra.setText("Confirmar compra");
                if (ticket != null) {
                    mostrarConfirmacion(ticket);
                } else {
                    mostrarError("No se pudo completar la compra. Intenta de nuevo.");
                }
            });
        }).start();
    }

    private void mostrarConfirmacion(Ticket ticket) {
        try {
            Stage stage = (Stage) btnConfirmarCompra.getScene().getWindow();
            TicketConfirmController ctrl = new TicketConfirmController(model);
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/client/view/purchase/TicketConfirmView.fxml"));
            loader.setControllerFactory(c -> ctrl);
            javafx.scene.Parent root = loader.load();
            ctrl.setTicket(ticket);
            stage.setScene(new Scene(root, 680, 720));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al mostrar confirmación: " + e.getMessage());
        }
    }

    @FXML public void handleVolver() {
        try {
            Stage stage = (Stage) btnConfirmarCompra.getScene().getWindow();
            client.view.dashboard.DashboardView dash = new client.view.dashboard.DashboardView(model);
            stage.setScene(new Scene(dash.getView(), 960, 640));
            stage.setTitle("trenes — " + model.getCurrentPassenger().getName());
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarError(String msg) {
        lblErrorCompra.setText(msg);
        lblErrorCompra.setVisible(true);
    }
}
