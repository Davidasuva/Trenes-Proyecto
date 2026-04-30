package server.controller.ticket;

import edu.uva.model.iterator.Iterator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.factory.ServerFactory;
import server.model.ServerModel;
import server.model.ticket.Ticket;
import server.model.route.Route;
import server.model.route.RouteService;
import server.model.ticket.TicketService;
import server.model.user.AbstractUser;
import server.model.user.UserService;

import java.net.URL;
import java.util.ResourceBundle;

public class TicketController implements Initializable {

    // ── Tabla ──
    @FXML private ComboBox<String>       cmbFiltroRuta;
    @FXML private ComboBox<String>       cmbFiltroPasajero;
    @FXML private TableView<Ticket>      tablaTickets;
    @FXML private TableColumn<Ticket, String> colId, colPasajero, colRuta,
            colCategoria, colEstado, colFechaCompra;
    @FXML private TableColumn<Ticket, String> colAcciones;

    // ── Panel edición ──
    @FXML private VBox    panelForm;
    @FXML private Label   lblTituloForm, lblErrorForm;
    @FXML private Label   lblId, lblPasajero, lblRuta, lblFechaCompra, lblCategoria;
    @FXML private ComboBox<String> cmbEstado;

    private TicketService ticketService;
    private RouteService  routeService;
    private UserService   userService;

    private ObservableList<Ticket> todosTickets   = FXCollections.observableArrayList();
    private ObservableList<Ticket> ticketsMostrados = FXCollections.observableArrayList();
    private Ticket ticketEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ── Columnas ──
        colId.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getId()));
        colPasajero.setCellValueFactory(c -> {
            var p = c.getValue().getPassenger();
            return new SimpleStringProperty(p == null ? "—" :
                    p.getName() + " " + p.getLastName());
        });
        colRuta.setCellValueFactory(c -> {
            var r = c.getValue().getRoute();
            return new SimpleStringProperty(r == null ? "—" : r.getName());
        });
        colCategoria.setCellValueFactory(c ->
                new SimpleStringProperty(categoriaLabel(c.getValue().getCategory())));
        colEstado.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().Status() ? "Activo" : "Inactivo"));
        colFechaCompra.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDateBuy()));

        // ── Columna acciones ──
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final HBox   box       = new HBox(btnEditar);
            {
                btnEditar.getStyleClass().add("btn-editar");
                btnEditar.setOnAction(e -> {
                    Ticket t = getTableView().getItems().get(getIndex());
                    abrirEdicion(t);
                });
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // ── Estado del ticket en el formulario ──
        cmbEstado.getItems().addAll("Activo", "Inactivo");

        tablaTickets.setItems(ticketsMostrados);
    }

    public void setModel(ServerModel model) {
        this.ticketService = model.getTicketService();
        this.routeService  = model.getRouteService();
        this.userService   = model.getUserService();
        cargarTabla();
        poblarFiltros();
    }

    // ── Carga ────────────────────────────────────────────────────────────────

    private void cargarTabla() {
        todosTickets.clear();
        if (ticketService == null) return;
        try {
            Iterator<Ticket> it = ticketService.getTickets().iterator();
            while (it.hasNext()) todosTickets.add(it.next());
        } catch (Exception e) { e.printStackTrace(); }
        aplicarFiltros();
    }

    private void poblarFiltros() {
        // Rutas únicas
        ObservableList<String> rutas = FXCollections.observableArrayList();
        rutas.add("Todas las rutas");
        try {
            Iterator<Route> it = routeService.getRoutes().iterator();
            while (it.hasNext()) rutas.add(it.next().getName());
        } catch (Exception e) { e.printStackTrace(); }
        cmbFiltroRuta.setItems(rutas);
        cmbFiltroRuta.getSelectionModel().selectFirst();

        // Pasajeros únicos
        ObservableList<String> pasajeros = FXCollections.observableArrayList();
        pasajeros.add("Todos los pasajeros");
        try {
            Iterator<AbstractUser> it = userService.seeUserPerCategory(1).iterator();
            while (it.hasNext()) {
                AbstractUser u = it.next();
                pasajeros.add(u.getName() + " " + u.getLastName());
            }
        } catch (Exception e) { e.printStackTrace(); }
        cmbFiltroPasajero.setItems(pasajeros);
        cmbFiltroPasajero.getSelectionModel().selectFirst();
    }

    // ── Filtros ───────────────────────────────────────────────────────────────

    @FXML private void handleFiltrarRuta()     { aplicarFiltros(); }
    @FXML private void handleFiltrarPasajero() { aplicarFiltros(); }

    private void aplicarFiltros() {
        String filtroRuta      = cmbFiltroRuta.getValue();
        String filtroPasajero  = cmbFiltroPasajero.getValue();

        ticketsMostrados.clear();
        for (Ticket t : todosTickets) {
            boolean okRuta = filtroRuta == null
                    || filtroRuta.equals("Todas las rutas")
                    || (t.getRoute() != null && t.getRoute().getName().equals(filtroRuta));

            String nombrePasajero = t.getPassenger() == null ? "" :
                    t.getPassenger().getName() + " " + t.getPassenger().getLastName();
            boolean okPasajero = filtroPasajero == null
                    || filtroPasajero.equals("Todos los pasajeros")
                    || nombrePasajero.equals(filtroPasajero);

            if (okRuta && okPasajero) ticketsMostrados.add(t);
        }
    }

    // ── Edición ──────────────────────────────────────────────────────────────

    private void abrirEdicion(Ticket t) {
        ticketEnEdicion = t;
        lblId.setText(t.getId());
        lblPasajero.setText(t.getPassenger() == null ? "—" :
                t.getPassenger().getName() + " " + t.getPassenger().getLastName());
        lblRuta.setText(t.getRoute() == null ? "—" : t.getRoute().getName());
        lblFechaCompra.setText(t.getDateBuy() == null ? "—" : t.getDateBuy());
        lblCategoria.setText(categoriaLabel(t.getCategory()));
        cmbEstado.setValue(t.Status() ? "Activo" : "Inactivo");
        ocultarError();
        mostrarPanel(true);
    }

    @FXML private void handleGuardar() {
        if (ticketEnEdicion == null) return;
        String estadoSel = cmbEstado.getValue();
        if (estadoSel == null) { mostrarError("Selecciona un estado."); return; }

        boolean nuevoEstado = estadoSel.equals("Activo");
        try {
            ticketService.setTicketStatus(ticketEnEdicion.getId(), nuevoEstado);
            tablaTickets.refresh();
            mostrarPanel(false);
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML private void handleCancelar() {
        ticketEnEdicion = null;
        mostrarPanel(false);
    }

    // ── Navegación ────────────────────────────────────────────────────────────
    @FXML private void irRutas()    { ServerFactory.navigateToRoutes  ((Stage) tablaTickets.getScene().getWindow()); }
    @FXML private void irTrenes()   { ServerFactory.navigateToTrains  ((Stage) tablaTickets.getScene().getWindow()); }
    @FXML private void irUsuarios() { ServerFactory.navigateToUsers   ((Stage) tablaTickets.getScene().getWindow()); }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String categoriaLabel(int cat) {
        return switch (cat) {
            case 0 -> "Premium";
            case 1 -> "Ejecutivo";
            case 2 -> "Estándar";
            default -> "Desconocido";
        };
    }

    private void mostrarPanel(boolean v) { panelForm.setVisible(v); panelForm.setManaged(v); }
    private void mostrarError(String msg) {
        lblErrorForm.setText(msg); lblErrorForm.setVisible(true); lblErrorForm.setManaged(true);
    }
    private void ocultarError() { lblErrorForm.setVisible(false); lblErrorForm.setManaged(false); }

    public void refreshTickets() { cargarTabla(); poblarFiltros(); }
}
