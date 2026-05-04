package client.controller.history;

import client.model.ClientModel;
import edu.uva.model.iterator.Iterator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import server.model.route.Route;
import server.model.ticket.Ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketHistoryController {

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroEstado;

    @FXML private TableView<Ticket> tablaTickets;
    @FXML private TableColumn<Ticket, String> colId, colRuta, colOrigen, colDestino,
            colFechaCompra, colSalida, colLlegada, colCategoria, colEstado, colPrecio;

    @FXML private VBox panelEditarRuta;
    @FXML private Label lblTicketEditar, lblOrigenFijo, lblErrorEditar;
    @FXML private ComboBox<Route> cmbNuevaRuta;
    @FXML private Label lblInfoNuevaRuta;

    private ClientModel model;
    private ObservableList<Ticket> todosTickets = FXCollections.observableArrayList();
    private ObservableList<Ticket> ticketsMostrados = FXCollections.observableArrayList();
    private Ticket ticketEnEdicion = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getId()));
        colRuta.setCellValueFactory(c -> {
            Route r = c.getValue().getRoute();
            return new javafx.beans.property.SimpleStringProperty(r == null ? "—" : r.getName());
        });
        colOrigen.setCellValueFactory(c -> {
            Route r = c.getValue().getRoute();
            return new javafx.beans.property.SimpleStringProperty(r == null ? "—" : r.getOrigin().getName());
        });
        colDestino.setCellValueFactory(c -> {
            Route r = c.getValue().getRoute();
            return new javafx.beans.property.SimpleStringProperty(r == null ? "—" : r.getDestiny().getName());
        });
        colFechaCompra.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDateBuy()));
        colSalida.setCellValueFactory(c -> {
            Route r = c.getValue().getRoute();
            return new javafx.beans.property.SimpleStringProperty(r == null ? "—" : r.getDateTravelStr());
        });
        colLlegada.setCellValueFactory(c -> {
            Route r = c.getValue().getRoute();
            return new javafx.beans.property.SimpleStringProperty(r == null ? "—" : r.getDateArrivalStr());
        });
        colCategoria.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(catLabel(c.getValue().getCategory())));
        colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(estadoTicket(c.getValue())));
        colPrecio.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.format("$ %,d", c.getValue().getPrice())));

        TableColumn<Ticket, Void> colAcciones = new TableColumn<>("Acción");
        colAcciones.setPrefWidth(130);
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Cambiar ruta");
            {
                btnEditar.getStyleClass().add("btn-editar-hist");
                btnEditar.setOnAction(e -> {
                    Ticket t = getTableView().getItems().get(getIndex());
                    abrirEdicionRuta(t);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Ticket t = getTableView().getItems().get(getIndex());
                boolean puedeEditar = t.Status() && puedeEditarRuta(t);
                setGraphic(puedeEditar ? btnEditar : null);
            }
        });
        tablaTickets.getColumns().add(colAcciones);

        cmbFiltroEstado.setItems(FXCollections.observableArrayList(
                "Todos", "Activos", "Pasados/Finalizados"));
        cmbFiltroEstado.setValue("Todos");
        cmbFiltroEstado.setOnAction(e -> aplicarFiltros());

        cmbNuevaRuta.setConverter(new javafx.util.StringConverter<Route>() {
            @Override public String toString(Route r) {
                if (r == null) return "";
                return r.getName() + " → " + r.getDestiny().getName()
                        + " (" + r.getDateTravelStr() + ")";
            }
            @Override public Route fromString(String s) { return null; }
        });
        cmbNuevaRuta.setOnAction(e -> actualizarInfoRuta());

        tablaTickets.setItems(ticketsMostrados);
        ocultarPanelEditar();
    }

    public void setModel(ClientModel model) {
        this.model = model;
        cargarTickets();
    }


    private void cargarTickets() {
        new Thread(() -> {
            var lista = model.getMyTickets();
            List<Ticket> temp = new ArrayList<>();
            if (lista != null) {
                Iterator<Ticket> it = lista.iterator();
                while (it.hasNext()) temp.add(it.next());
            }
            Platform.runLater(() -> {
                todosTickets.setAll(temp);
                aplicarFiltros();
            });
        }).start();
    }


    @FXML
    private void handleBuscar() { aplicarFiltros(); }

    private void aplicarFiltros() {
        String busq   = txtBuscar.getText().toLowerCase().trim();
        String estado = cmbFiltroEstado.getValue();

        ticketsMostrados.clear();
        for (Ticket t : todosTickets) {
            boolean okEstado = true;
            if ("Activos".equals(estado)) {
                okEstado = t.Status() && !esRutaFinalizada(t);
            } else if ("Pasados/Finalizados".equals(estado)) {
                okEstado = !t.Status() || esRutaFinalizada(t);
            }

            boolean okBusq = busq.isEmpty();
            if (!okBusq) {
                okBusq = t.getId().toLowerCase().contains(busq);
                if (!okBusq && t.getRoute() != null) {
                    okBusq = t.getRoute().getName().toLowerCase().contains(busq)
                          || t.getRoute().getOrigin().getName().toLowerCase().contains(busq)
                          || t.getRoute().getDestiny().getName().toLowerCase().contains(busq);
                }
            }

            if (okEstado && okBusq) ticketsMostrados.add(t);
        }
    }


    private void abrirEdicionRuta(Ticket t) {
        ticketEnEdicion = t;
        Route rutaActual = t.getRoute();

        lblTicketEditar.setText("Ticket: " + t.getId());
        lblOrigenFijo.setText("Origen: " + (rutaActual != null ? rutaActual.getOrigin().getName() : "—"));
        lblErrorEditar.setText("");
        lblErrorEditar.setVisible(false);
        cmbNuevaRuta.getItems().clear();
        lblInfoNuevaRuta.setText("");

        new Thread(() -> {
            var rutas = model.getRoutesWithSameOrigin(rutaActual);
            List<Route> listaRutas = new ArrayList<>();
            if (rutas != null) {
                Iterator<Route> it = rutas.iterator();
                while (it.hasNext()) listaRutas.add(it.next());
            }
            Platform.runLater(() -> {
                cmbNuevaRuta.setItems(FXCollections.observableArrayList(listaRutas));
                if (listaRutas.isEmpty()) {
                    lblErrorEditar.setText("No hay rutas disponibles con el mismo origen.");
                    lblErrorEditar.setVisible(true);
                }
                panelEditarRuta.setVisible(true);
                panelEditarRuta.setManaged(true);
            });
        }).start();
    }

    private void actualizarInfoRuta() {
        Route sel = cmbNuevaRuta.getValue();
        if (sel == null) { lblInfoNuevaRuta.setText(""); return; }
        lblInfoNuevaRuta.setText(
            "Destino: " + sel.getDestiny().getName()
            + "  |  Salida: " + sel.getDateTravelStr()
            + "  |  Llegada: " + sel.getDateArrivalStr()
            + "  |  " + String.format("%.0f km", sel.getTotalDistance()));
    }

    @FXML
    private void handleGuardarRuta() {
        if (ticketEnEdicion == null) return;
        Route nuevaRuta = cmbNuevaRuta.getValue();
        if (nuevaRuta == null) {
            mostrarError("Selecciona una ruta de destino.");
            return;
        }
        boolean ok = model.changeTicketRoute(ticketEnEdicion, nuevaRuta);
        if (ok) {
            tablaTickets.refresh();
            ocultarPanelEditar();
        } else {
            mostrarError("No se pudo cambiar la ruta. Intenta de nuevo.");
        }
    }

    @FXML
    private void handleCancelarEdicion() {
        ticketEnEdicion = null;
        ocultarPanelEditar();
    }

    @FXML
    private void handleRefresh() { cargarTickets(); }


    @FXML
    private void handleVolver() {
        try {
            Stage stage = (Stage) tablaTickets.getScene().getWindow();
            client.view.dashboard.DashboardView dash = new client.view.dashboard.DashboardView(model);
            stage.setScene(new Scene(dash.getView(), 960, 640));
            stage.setTitle("trenes — " + model.getCurrentPassenger().getName());
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String catLabel(int cat) {
        return switch (cat) {
            case 0 -> "Premium";
            case 1 -> "Ejecutivo";
            default -> "Estándar";
        };
    }

    private String estadoTicket(Ticket t) {
        if (!t.Status()) return "Cancelado";
        Route r = t.getRoute();
        if (r == null) return "Activo";
        LocalDateTime now = LocalDateTime.now();
        if (r.getDateArrival() != null && now.isAfter(r.getDateArrival())) return "Finalizado";
        if (r.getDateTravel()  != null && now.isAfter(r.getDateTravel()))  return "En curso";
        return "Activo";
    }

    private boolean esRutaFinalizada(Ticket t) {
        Route r = t.getRoute();
        if (r == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return r.getDateArrival() != null && now.isAfter(r.getDateArrival());
    }

    /** Un ticket puede cambiar de ruta solo si la ruta actual aún no ha iniciado */
    private boolean puedeEditarRuta(Ticket t) {
        Route r = t.getRoute();
        if (r == null) return false;
        LocalDateTime now = LocalDateTime.now();
        return r.getDateTravel() == null || now.isBefore(r.getDateTravel());
    }

    private void ocultarPanelEditar() {
        panelEditarRuta.setVisible(false);
        panelEditarRuta.setManaged(false);
    }

    private void mostrarError(String msg) {
        lblErrorEditar.setText(msg);
        lblErrorEditar.setVisible(true);
    }
}
