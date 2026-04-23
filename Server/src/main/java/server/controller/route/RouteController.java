package server.controller.route;

import edu.uva.app.linkedlist.singly.singly.LinkedList;
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
import server.model.route.Route;
import server.model.route.RouteService;
import server.model.route.Station;
import server.model.train.Train;
import server.model.train.TrainService;
import server.model.user.Admin;
import edu.uva.app.queue.list.Queue;

import java.net.URL;
import java.util.ResourceBundle;

public class RouteController implements Initializable {

    // ── Tabla ──
    @FXML private TextField txtBuscar;
    @FXML private TableView<Route> tablaRutas;
    @FXML private TableColumn<Route, String> colId, colNombre, colOrigen, colDestino, colDistancia, colEstado;
    @FXML private TableColumn<Route, String> colAcciones;

    // ── Formulario ──
    @FXML private VBox panelForm;
    @FXML private Label lblTituloForm, lblErrorForm, lblEstaciones;
    @FXML private TextField txtNombre, txtFechaSalida, txtFechaLlegada;
    @FXML private ComboBox<Train> cmbTren;
    @FXML private ComboBox<Station> cmbOrigen, cmbDestino;

    private RouteService routeService;
    private TrainService trainService;
    private ObservableList<Route>   dataRutas   = FXCollections.observableArrayList();
    private ObservableList<Station> estaciones   = FXCollections.observableArrayList();
    private ObservableList<Train>   listaTrenes  = FXCollections.observableArrayList();

    /** Ruta que está siendo editada; null = modo creación */
    private Route rutaEnEdicion = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ── Columnas ──
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colOrigen.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrigin().getName()));
        colDestino.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDestiny().getName()));
        colDistancia.setCellValueFactory(c -> new SimpleStringProperty(
                String.format("%.0f km", c.getValue().getTotalDistance())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().isActive() ? "Activa" : "Inactiva"));

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("Editar");
            private final Button btnDesact   = new Button("Desactivar");
            private final Button btnAct      = new Button("Activar");
            private final Button btnPublicar = new Button("Publicar");
            private final Button btnAbordaje = new Button("Abordaje");
            private final HBox box = new HBox(4, btnEditar, btnAct, btnDesact, btnPublicar, btnAbordaje);
            {
                btnEditar.getStyleClass().add("btn-editar");
                btnAct.getStyleClass().add("btn-editar");
                btnDesact.getStyleClass().add("btn-peligro");
                btnPublicar.getStyleClass().add("btn-primario");
                btnAbordaje.getStyleClass().add("btn-primario");

                btnEditar.setOnAction(e -> {
                    Route r = getTableView().getItems().get(getIndex());
                    abrirEdicion(r);
                });
                btnAct.setOnAction(e -> {
                    Route r = getTableView().getItems().get(getIndex());
                    try { routeService.activateRoute(r.getId(), adminFicticio()); tablaRutas.refresh(); }
                    catch (Exception ex) { ex.printStackTrace(); }
                });
                btnDesact.setOnAction(e -> {
                    Route r = getTableView().getItems().get(getIndex());
                    try { routeService.deactivateRoute(r.getId(), adminFicticio()); tablaRutas.refresh(); }
                    catch (Exception ex) { ex.printStackTrace(); }
                });
                btnPublicar.setOnAction(e -> {
                    Route r = getTableView().getItems().get(getIndex());
                    try {
                        routeService.publicateRoute(r.getId(), adminFicticio());
                        tablaRutas.refresh();
                        mostrarInfo("Ruta '" + r.getName() + "' publicada correctamente.");
                    } catch (Exception ex) { ex.printStackTrace(); }
                });
                btnAbordaje.setOnAction(e -> {
                    Route r = getTableView().getItems().get(getIndex());
                    mostrarAbordaje(r);
                });
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // ── Converters ComboBox ──
        cmbOrigen.setConverter(new javafx.util.StringConverter<Station>() {
            @Override public String toString(Station s) { return s == null ? "" : s.getName(); }
            @Override public Station fromString(String str) { return null; }
        });
        cmbDestino.setConverter(new javafx.util.StringConverter<Station>() {
            @Override public String toString(Station s) { return s == null ? "" : s.getName(); }
            @Override public Station fromString(String str) { return null; }
        });
        cmbTren.setConverter(new javafx.util.StringConverter<Train>() {
            @Override public String toString(Train t) { return t == null ? "" : t.getId() + " — " + t.getName(); }
            @Override public Train fromString(String str) { return null; }
        });

        tablaRutas.setItems(dataRutas);
        cmbTren.setItems(listaTrenes);  // lista persistente — se rellena en cargarTrenes()

        tablaRutas.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs2, oldWin, newWin) -> {
                    if (newWin != null) {
                        newWin.focusedProperty().addListener((obs3, wasFocused, isNowFocused) -> {
                            if (isNowFocused) cargarTrenes();
                        });
                    }
                });
            }
        });
    }

    public void setModel(ServerModel model) {
        this.routeService = model.getRouteService();
        this.trainService = model.getTrainService();
        cargarEstaciones();
        cargarTrenes();
        cargarTabla();
    }

    private void cargarEstaciones() {
        estaciones.clear();
        try {
            Iterator<Station> it = routeService.getStations().iterator();
            while (it.hasNext()) estaciones.add(it.next());
        } catch (Exception e) { e.printStackTrace(); }
        cmbOrigen.setItems(estaciones);
        cmbDestino.setItems(FXCollections.observableArrayList(estaciones));
    }

    private void cargarTrenes() {
        if (trainService == null) return;
        listaTrenes.clear();
        try {
            Iterator<Train> it = trainService.getTrains().iterator();
            while (it.hasNext()) listaTrenes.add(it.next());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarTabla() {
        dataRutas.clear();
        try {
            Iterator<Route> it = routeService.getRoutes().iterator();
            while (it.hasNext()) dataRutas.add(it.next());
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Abrir modo edición de una ruta existente ──────────────────────────────

    private void abrirEdicion(Route r) {
        // Solo se permite editar si la ruta no está activa (no ha iniciado)
        if (r.isActive()) {
            mostrarError("Solo se pueden editar rutas inactivas (desactívala primero).");
            return;
        }
        rutaEnEdicion = r;
        lblTituloForm.setText("Editar Ruta");
        txtNombre.setText(r.getName());
        txtFechaSalida.setText(r.getDateTravel());
        txtFechaLlegada.setText(r.getDateArrival());

        // Seleccionar estación origen
        for (Station s : estaciones) {
            if (s.equals(r.getOrigin())) { cmbOrigen.setValue(s); break; }
        }
        // Seleccionar estación destino
        for (Station s : estaciones) {
            if (s.equals(r.getDestiny())) { cmbDestino.setValue(s); break; }
        }
        // Seleccionar tren
        cargarTrenes();
        try {
            Queue<Train> cola = routeService.seeTrainsPerRoute(r.getId());
            if (cola != null) {
                // El primer tren de la cola es el asignado
                Iterator<Train> it = trainService.getTrains().iterator();
                while (it.hasNext()) {
                    Train t = it.next();
                    // Buscamos el tren cuyo id coincida con el de la ruta
                    if (!cola.isEmpty() && t.equals(cola.peek())) {
                        cmbTren.setValue(t);
                        break;
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        actualizarEstacionesLabel();
        ocultarError();
        mostrarPanel(true);
    }

    // ── Handlers ──────────────────────────────────────────────────────────────

    @FXML private void handleBuscar() {
        String q = txtBuscar.getText().toLowerCase().trim();
        if (q.isEmpty()) { tablaRutas.setItems(dataRutas); return; }
        ObservableList<Route> filtrado = FXCollections.observableArrayList();
        for (Route r : dataRutas)
            if (r.getName().toLowerCase().contains(q)
                    || r.getOrigin().getName().toLowerCase().contains(q)
                    || r.getDestiny().getName().toLowerCase().contains(q))
                filtrado.add(r);
        tablaRutas.setItems(filtrado);
    }

    @FXML private void handleNuevaRuta() {
        rutaEnEdicion = null;
        lblTituloForm.setText("Nueva Ruta");
        cargarTrenes();
        limpiarFormulario();
        mostrarPanel(true);
    }

    @FXML private void handleOrigenCambio()  {
        actualizarEstacionesLabel(); }
    @FXML private void handleDestinoCambio() {
        actualizarEstacionesLabel(); }

    private void actualizarEstacionesLabel() {
        Station org = cmbOrigen.getValue();
        Station dst = cmbDestino.getValue();
        if (org == null || dst == null) { lblEstaciones.setText("Selecciona origen y destino"); return; }
        if (org.equals(dst)) { lblEstaciones.setText("Origen y destino deben ser distintos."); return; }
        try {
            boolean existe = routeService.existsPath(org, dst);
            if (!existe) { lblEstaciones.setText("No hay camino entre estas estaciones."); return; }
            StringBuilder sb = new StringBuilder();
            Iterator<Station> it = routeService.getShortestPath(org, dst).iterator();
            int i = 0;
            while (it.hasNext()) {
                if (i > 0) sb.append(" -> ");
                sb.append(it.next().getName());
                i++;
            }
            lblEstaciones.setText(sb.toString());
        } catch (Exception e) { lblEstaciones.setText("Error calculando ruta."); }
    }

    @FXML private void handleGuardar() {
        String nombre    = txtNombre.getText().trim();
        Station org      = cmbOrigen.getValue();
        Station dst      = cmbDestino.getValue();
        Train tren       = cmbTren.getValue();
        String fechaSal  = txtFechaSalida.getText().trim();
        String fechaLleg = txtFechaLlegada.getText().trim();

        if (nombre.isEmpty())     { mostrarError("El nombre es obligatorio."); return; }
        if (org == null)          { mostrarError("Selecciona la estación de origen."); return; }
        if (dst == null)          { mostrarError("Selecciona la estación de destino."); return; }
        if (org.equals(dst))      { mostrarError("Origen y destino deben ser distintos."); return; }
        if (tren == null)         { mostrarError("Selecciona un tren."); return; }
        if (fechaSal.isEmpty())   { mostrarError("Ingresa la fecha de salida."); return; }
        if (fechaLleg.isEmpty())  { mostrarError("Ingresa la fecha de llegada."); return; }
        if (routeService == null) { mostrarError("Servidor no disponible."); return; }

        try {
            if (rutaEnEdicion == null) {
                // ── Validar que el tren no esté ya en una ruta activa ──
                String rutaUsando = routeService.getRouteNameUsingTrain(tren);
                if (rutaUsando != null) {
                    mostrarError("El tren ya está asignado a la ruta activa: \"" + rutaUsando + "\".");
                    return;
                }
                // ── Crear nueva ruta ──
                Queue<Train> cola = new Queue<>();
                cola.insert(tren);
                int nuevoId = dataRutas.size() + 1;
                Route r = routeService.createRoute(nuevoId, nombre, cola,
                        fechaSal, fechaLleg, org, dst, adminFicticio());
                dataRutas.add(r);
            } else {
                // ── Validar tren al editar (ignorar si es el mismo tren que ya tenía) ──
                String rutaUsando = routeService.getRouteNameUsingTrain(tren);
                if (rutaUsando != null && !rutaUsando.equals(rutaEnEdicion.getName())) {
                    mostrarError("El tren ya está asignado a la ruta activa: \"" + rutaUsando + "\".");
                    return;
                }
                // ── Editar ruta existente ──
                rutaEnEdicion.setName(nombre);
                rutaEnEdicion.setDateTravel(fechaSal);
                rutaEnEdicion.setDateArrival(fechaLleg);
                rutaEnEdicion.setOrigin(org);
                rutaEnEdicion.setDestiny(dst);
                while (!rutaEnEdicion.getTrains().isEmpty()) {
                    rutaEnEdicion.removeTrain();
                }
                rutaEnEdicion.addTrain(tren);
            }
            tablaRutas.refresh();
            mostrarPanel(false);
        } catch (Exception ex) { mostrarError(ex.getMessage()); }
    }

    // ── Panel de abordaje ──
    @FXML private VBox panelAbordaje;
    @FXML private Label lblTituloAbordaje;
    @FXML private TextArea txtOrdenAbordaje;

    /** Muestra el orden de abordaje de una ruta en el panel lateral */
    private void mostrarAbordaje(Route r) {
        mostrarPanel(false);
        try {
            LinkedList<String> orden = routeService.getBoardingOrder(r.getId());
            StringBuilder sb = new StringBuilder();
            Iterator<String> iterador=orden.iterator();
            while(iterador.hasNext()){
                sb.append(iterador.next()).append("\n");
            }

            lblTituloAbordaje.setText("Abordaje: " + r.getName());
            txtOrdenAbordaje.setText(sb.toString());
        } catch (Exception e) {
            txtOrdenAbordaje.setText("Error generando orden: " + e.getMessage());
        }
        panelAbordaje.setVisible(true);
        panelAbordaje.setManaged(true);
    }

    @FXML private void handleCerrarAbordaje() {
        panelAbordaje.setVisible(false);
        panelAbordaje.setManaged(false);
    }

    @FXML private void handleCancelar() {
        rutaEnEdicion = null;
        mostrarPanel(false);
    }

    // ── Navegación ──
    @FXML private void irTrenes()       {
        ServerFactory.navigateToTrains ((Stage) tablaRutas.getScene().getWindow()); }
    @FXML private void irUsuarios()     {
        ServerFactory.navigateToUsers   ((Stage) tablaRutas.getScene().getWindow()); }
    @FXML private void irTrabajadores() {
        ServerFactory.navigateToWorkers ((Stage) tablaRutas.getScene().getWindow()); }

    // ── Helpers ──
    private Admin adminFicticio() {
        return new Admin("0","Admin@project.com","Admin123","Admin","1234","C.C","cr 29#92-49",3);
    }
    private void mostrarPanel(boolean v) { panelForm.setVisible(v); panelForm.setManaged(v); }
    private void limpiarFormulario() {
        txtNombre.clear(); txtFechaSalida.clear(); txtFechaLlegada.clear();
        cmbOrigen.getSelectionModel().clearSelection();
        cmbDestino.getSelectionModel().clearSelection();
        cmbTren.getSelectionModel().clearSelection();
        lblEstaciones.setText("Selecciona origen y destino");
        ocultarError();
    }
    private void mostrarError(String msg) {
        lblErrorForm.setText(msg);
        lblErrorForm.setStyle("-fx-text-fill: #e53e3e;");
        lblErrorForm.setVisible(true); lblErrorForm.setManaged(true);
    }
    private void mostrarInfo(String msg) {
        lblErrorForm.setText(msg);
        lblErrorForm.setStyle("-fx-text-fill: #38a169;");
        lblErrorForm.setVisible(true); lblErrorForm.setManaged(true);
    }
    private void ocultarError() { lblErrorForm.setVisible(false); lblErrorForm.setManaged(false); }

    public void refreshRoutes(){
        cargarTabla();
        cargarEstaciones();
        cargarTrenes();
    }
}
