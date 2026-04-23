package server.controller.train;
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
import server.model.train.Train;
import server.model.train.TrainService;
import server.model.user.Admin;

import java.net.URL;
import java.util.ResourceBundle;

public class TrainController implements Initializable {

    // ── Tabla ──
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbFiltroFabricante;
    @FXML private TableView<Train> tablaTrenes;
    @FXML private TableColumn<Train, String> colId, colNombre, colFabricante, colTipo, colCapacidad, colKilometraje;
    @FXML private TableColumn<Train, String> colAcciones;

    // ── Formulario ──
    @FXML private VBox panelForm;
    @FXML private Label lblTituloForm, lblErrorForm, lblLimite;
    @FXML private TextField txtId, txtNombre, txtVagonesPasajeros, txtVagonesCarga;
    @FXML private ComboBox<String> cmbFabricante, cmbTipo;

    private TrainService trainService;
    private ObservableList<Train> dataTrenes = FXCollections.observableArrayList();
    private Train trainEnEdicion = null;

    private static final String[] TIPOS_MERCEDES = {
            "Mercedes-Benz Coradia", "Mercedes-Benz Talent", "Mercedes-Benz Regio"
    };
    private static final String[] TIPOS_ARNOLD = {
            "Arnold BR 101", "Arnold ICE 3", "Arnold RegioShuttle"
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbFiltroFabricante.getItems().addAll("Todos", "Mercedes-Benz", "Arnold");
        cmbFiltroFabricante.getSelectionModel().selectFirst();
        cmbFabricante.getItems().addAll("Mercedes-Benz", "Arnold");
        cmbFabricante.setOnAction(e -> { actualizarTipos(); actualizarLimite(); });

        // Actualizar indicador de límite al escribir vagones
        txtVagonesPasajeros.textProperty().addListener((o, ov, nv) -> actualizarLimite());
        txtVagonesCarga.textProperty().addListener((o, ov, nv) -> actualizarLimite());

        // Columnas
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colFabricante.setCellValueFactory(c -> new SimpleStringProperty(fabricanteDesdeNombre(c.getValue().getType())));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        colCapacidad.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getCapacity() + "P / " + c.getValue().getCargoWagons() + "C"));
        colKilometraje.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMileage() + " km"));
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox box = new HBox(6, btnEditar, btnEliminar);
            {
                btnEditar.getStyleClass().add("btn-editar");
                btnEliminar.getStyleClass().add("btn-peligro");
                btnEditar.setOnAction(e -> abrirEdicion(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> eliminarTren(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
        tablaTrenes.setItems(dataTrenes);
    }

    public void setModel(ServerModel model) {
        this.trainService = model.getTrainService();
        cargarTabla();
    }

    private void cargarTabla() {
        if (trainService == null) return;
        dataTrenes.clear();
        try {
            Iterator<Train> it = trainService.getTrains().iterator();
            while (it.hasNext()) dataTrenes.add(it.next());
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void handleBuscar() {
        String q = txtBuscar.getText().toLowerCase().trim();
        if (q.isEmpty()) { tablaTrenes.setItems(dataTrenes); return; }
        ObservableList<Train> filtrado = FXCollections.observableArrayList();
        for (Train t : dataTrenes)
            if (String.valueOf(t.getId()).contains(q) || t.getName().toLowerCase().contains(q))
                filtrado.add(t);
        tablaTrenes.setItems(filtrado);
    }

    @FXML private void handleFiltroFabricante() {
        String fab = cmbFiltroFabricante.getValue();
        if (fab == null || fab.equals("Todos")) { tablaTrenes.setItems(dataTrenes); return; }
        ObservableList<Train> filtrado = FXCollections.observableArrayList();
        for (Train t : dataTrenes)
            if (fabricanteDesdeNombre(t.getType()).equals(fab)) filtrado.add(t);
        tablaTrenes.setItems(filtrado);
    }

    @FXML private void handleNuevoTren() {
        trainEnEdicion = null;
        lblTituloForm.setText("Nuevo Tren");
        limpiarFormulario();
        mostrarPanel(true);
    }

    private void abrirEdicion(Train t) {
        trainEnEdicion = t;
        lblTituloForm.setText("Editar Tren");
        txtId.setText(String.valueOf(t.getId()));
        txtId.setDisable(true);
        txtNombre.setText(t.getName());
        txtVagonesPasajeros.setText(String.valueOf(t.getCapacity()));
        txtVagonesCarga.setText(String.valueOf(t.getCargoWagons()));
        String fab = fabricanteDesdeNombre(t.getType());
        cmbFabricante.setValue(fab);
        actualizarTipos();
        cmbTipo.setValue(t.getType());
        actualizarLimite();
        ocultarError();
        mostrarPanel(true);
    }

    private void eliminarTren(Train t) {
        if (trainService == null) return;
        try {
            trainService.removeTrain(t, adminFicticio());
            dataTrenes.remove(t);
            tablaTrenes.refresh();
        } catch (Exception ex) { mostrarError("Error al eliminar: " + ex.getMessage()); }
    }

    @FXML private void handleGuardar() {
        String idStr   = txtId.getText().trim();
        String nombre  = txtNombre.getText().trim();
        String pasStr  = txtVagonesPasajeros.getText().trim();
        String cargStr = txtVagonesCarga.getText().trim();
        String fab     = cmbFabricante.getValue();
        String tipo    = cmbTipo.getValue();

        if (idStr.isEmpty() || !idStr.matches("\\d+"))   { mostrarError("ID debe ser un número entero."); return; }
        if (nombre.isEmpty())                             { mostrarError("El nombre es obligatorio."); return; }
        if (pasStr.isEmpty() || !pasStr.matches("\\d+")) { mostrarError("Vagones de pasajeros debe ser un número."); return; }
        if (cargStr.isEmpty() || !cargStr.matches("\\d+")){ mostrarError("Vagones de carga debe ser un número."); return; }
        if (fab == null)                                  { mostrarError("Selecciona un fabricante."); return; }
        if (tipo == null)                                 { mostrarError("Selecciona un tipo."); return; }
        if (trainService == null)                         { mostrarError("Servidor no disponible."); return; }

        int id   = Integer.parseInt(idStr);
        int pas  = Integer.parseInt(pasStr);
        int carg = Integer.parseInt(cargStr);

        try {
            if (trainEnEdicion == null) {
                Train nuevo = new Train(id, nombre, tipo, pas, carg, 0);
                trainService.register(nuevo, adminFicticio());
                dataTrenes.add(nuevo);
            } else {
                trainService.modifyTrain(trainEnEdicion.getId(), nombre, tipo, pas, carg, adminFicticio());
                cargarTabla();
            }
            tablaTrenes.refresh();
            mostrarPanel(false);
        } catch (Exception ex) { mostrarError(ex.getMessage()); }
    }

    @FXML private void handleCancelar() { mostrarPanel(false); }

    // ── Navegación ──
    @FXML private void irRutas()        { ServerFactory.navigateToRoutes  ((Stage) tablaTrenes.getScene().getWindow()); }
    @FXML private void irUsuarios()     { ServerFactory.navigateToUsers   ((Stage) tablaTrenes.getScene().getWindow()); }
    @FXML private void irTrabajadores() { ServerFactory.navigateToWorkers ((Stage) tablaTrenes.getScene().getWindow()); }

    // ── Helpers ──
    private void actualizarTipos() {
        cmbTipo.getItems().clear();
        String fab = cmbFabricante.getValue();
        if ("Mercedes-Benz".equals(fab)) cmbTipo.getItems().addAll(TIPOS_MERCEDES);
        else if ("Arnold".equals(fab))   cmbTipo.getItems().addAll(TIPOS_ARNOLD);
    }

    private void actualizarLimite() {
        String fab = cmbFabricante.getValue();
        if (fab == null) { lblLimite.setText(""); return; }
        int max = fab.equals("Mercedes-Benz") ? 28 : 32;
        int pas = parseIntSafe(txtVagonesPasajeros.getText());
        int carg = parseIntSafe(txtVagonesCarga.getText());
        int total = pas + carg;
        int minCarga = (int) Math.ceil(pas / 2.0);
        String color;
        String msg;
        if (total > max) {
            color = "#e53e3e"; // rojo
            msg = "⚠ Límite superado: " + total + "/" + max + " vagones";
        } else if (pas > 0 && carg < minCarga) {
            color = "#dd6b20"; // naranja
            msg = "⚠ Carga mínima: " + minCarga + " (actual: " + carg + ")";
        } else {
            color = "#38a169"; // verde
            msg = "✓ Vagones: " + total + "/" + max;
        }
        lblLimite.setText(msg);
        lblLimite.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 11px;");
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }

    private String fabricanteDesdeNombre(String tipo) {
        if (tipo == null) return "—";
        if (tipo.toLowerCase().contains("mercedes")) return "Mercedes-Benz";
        if (tipo.toLowerCase().contains("arnold"))   return "Arnold";
        return "—";
    }

    private Admin adminFicticio() {
        return new Admin("0","Admin@project.com","Admin123","Admin","1234","C.C","cr 29#92-49",3);
    }

    private void mostrarPanel(boolean v) { panelForm.setVisible(v); panelForm.setManaged(v); }

    private void limpiarFormulario() {
        txtId.clear(); txtId.setDisable(false);
        txtNombre.clear(); txtVagonesPasajeros.clear(); txtVagonesCarga.clear();
        cmbFabricante.getSelectionModel().clearSelection();
        cmbTipo.getItems().clear();
        lblLimite.setText("");
        ocultarError();
    }

    private void mostrarError(String msg) {
        lblErrorForm.setText(msg); lblErrorForm.setVisible(true); lblErrorForm.setManaged(true);
    }
    private void ocultarError() { lblErrorForm.setVisible(false); lblErrorForm.setManaged(false); }

    public void refreshTrains(){
        cargarTabla();
    }
}
