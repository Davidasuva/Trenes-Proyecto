package server.controller.employee;

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
import server.model.user.AbstractUser;
import server.model.user.UserService;
import server.model.user.Worker;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class EmployeeController implements Initializable {

    // ── Tabla ──
    @FXML private TextField txtBuscar;
    @FXML private TableView<AbstractUser> tablaEmpleados;
    @FXML private TableColumn<AbstractUser, String> colId, colNombre, colCorreo, colDocumento, colRol, colRuta;
    @FXML private TableColumn<AbstractUser, String> colAcciones;

    // ── Formulario ──
    @FXML private VBox panelForm;
    @FXML private Label lblTituloForm, lblErrorForm;
    @FXML private TextField txtId, txtNombre, txtApellido, txtCorreo, txtPassword, txtDireccion;
    @FXML private ComboBox<String> cmbTipoDoc;
    @FXML private ComboBox<String> cmbRol;
    @FXML private ComboBox<Route>  cmbRuta;

    private UserService  userService;
    private RouteService routeService;
    private ObservableList<AbstractUser> dataEmpleados = FXCollections.observableArrayList();

    /** Empleado siendo editado; null = modo creación */
    private AbstractUser empleadoEnEdicion = null;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");

    private static final String ROL_PILOTO = "Piloto";
    private static final String ROL_ABORDO = "Personal de abordo";

    // Prefijos para el campo dirección (reutilizamos el campo para no romper el modelo)
    private static final String PREF_ROL  = "ROL:";
    private static final String PREF_RUTA = "|RUTA:";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbTipoDoc.getItems().addAll("Cédula de ciudadanía", "Pasaporte", "Tarjeta de identidad");
        cmbRol.getItems().addAll(ROL_PILOTO, ROL_ABORDO);

        // Mostrar rutas con nombre legible
        cmbRuta.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Route r, boolean empty) {
                super.updateItem(r, empty);
                setText((empty || r == null) ? null : r.getId() + " – " + r.getName());
            }
        });
        cmbRuta.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Route r, boolean empty) {
                super.updateItem(r, empty);
                setText((empty || r == null) ? null : r.getId() + " – " + r.getName());
            }
        });

        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getName() + " " + c.getValue().getLastName()));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMail()));
        colDocumento.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTypeIdetification() + " " + c.getValue().getId()));
        colRol.setCellValueFactory(c -> new SimpleStringProperty(extraerRol(c.getValue().getAdress())));
        colRuta.setCellValueFactory(c -> new SimpleStringProperty(extraerNombreRuta(c.getValue().getAdress())));

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox box = new HBox(6, btnEditar, btnEliminar);
            {
                btnEditar.getStyleClass().add("btn-editar");
                btnEliminar.getStyleClass().add("btn-peligro");
                btnEditar.setOnAction(e -> abrirEdicion(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> confirmarYEliminar(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tablaEmpleados.setItems(dataEmpleados);
    }

    public void setModel(ServerModel model) {
        this.userService  = model.getUserService();
        this.routeService = model.getRouteService();
        cargarRutas();
        cargarTabla();
    }

    // ── Carga de datos ───────────────────────────────────────────────────────

    private void cargarRutas() {
        cmbRuta.getItems().clear();
        if (routeService == null) return;
        try {
            Iterator<Route> it = routeService.getRoutes().iterator();
            while (it.hasNext()) cmbRuta.getItems().add(it.next());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void cargarTabla() {
        dataEmpleados.clear();
        if (userService == null) return;
        try {
            Iterator<AbstractUser> it = userService.seeUserPerCategory(2).iterator();
            while (it.hasNext()) dataEmpleados.add(it.next());
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Abrir edición ────────────────────────────────────────────────────────

    private void abrirEdicion(AbstractUser u) {
        empleadoEnEdicion = u;
        lblTituloForm.setText("Editar Empleado");

        txtId.setText(u.getId());
        txtId.setDisable(true);
        txtNombre.setText(u.getName());
        txtApellido.setText(u.getLastName());
        txtCorreo.setText(u.getMail());
        txtPassword.setText(u.getPassword());
        cmbTipoDoc.setValue(u.getTypeIdetification());
        cmbRol.setValue(extraerRol(u.getAdress()));

        // Recuperar ruta guardada y seleccionarla en el ComboBox
        String rutaGuardada = extraerNombreRuta(u.getAdress());
        cmbRuta.getSelectionModel().clearSelection();
        if (!rutaGuardada.isEmpty()) {
            for (Route r : cmbRuta.getItems()) {
                if ((r.getId() + " – " + r.getName()).equals(rutaGuardada)) {
                    cmbRuta.setValue(r);
                    break;
                }
            }
        }

        ocultarError();
        mostrarPanel(true);
    }

    // ── Eliminar con confirmación ─────────────────────────────────────────────

    private void confirmarYEliminar(AbstractUser u) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar empleado?");
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar a "
                + u.getName() + " " + u.getLastName() + "? Esta acción no se puede deshacer.");
        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            eliminarEmpleado(u);
        }
    }

    private void eliminarEmpleado(AbstractUser u) {
        if (userService == null) return;
        try {
            userService.removeUser(u.getId());
            dataEmpleados.remove(u);
            tablaEmpleados.refresh();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // ── Buscar ───────────────────────────────────────────────────────────────

    @FXML private void handleBuscar() {
        String q = txtBuscar.getText().toLowerCase().trim();
        if (q.isEmpty()) { tablaEmpleados.setItems(dataEmpleados); return; }
        ObservableList<AbstractUser> filtrado = FXCollections.observableArrayList();
        for (AbstractUser u : dataEmpleados) {
            if ((u.getName() + " " + u.getLastName()).toLowerCase().contains(q)
                    || u.getMail().toLowerCase().contains(q)
                    || u.getId().contains(q)
                    || extraerNombreRuta(u.getAdress()).toLowerCase().contains(q))
                filtrado.add(u);
        }
        tablaEmpleados.setItems(filtrado);
    }

    // ── Nuevo empleado ───────────────────────────────────────────────────────

    @FXML private void handleNuevoEmpleado() {
        empleadoEnEdicion = null;
        lblTituloForm.setText("Nuevo Empleado");
        cargarRutas(); // refrescar rutas disponibles
        limpiarFormulario();
        mostrarPanel(true);
    }

    // ── Guardar ──────────────────────────────────────────────────────────────

    @FXML private void handleGuardar() {
        String id       = txtId.getText().trim();
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo   = txtCorreo.getText().trim();
        String password = txtPassword.getText().trim();
        String tipoDoc  = cmbTipoDoc.getValue();
        String rol      = cmbRol.getValue();
        Route  ruta     = cmbRuta.getValue();

        if (id.isEmpty())          { mostrarError("El ID es obligatorio."); return; }
        if (nombre.isEmpty())      { mostrarError("El nombre es obligatorio."); return; }
        if (apellido.isEmpty())    { mostrarError("El apellido es obligatorio."); return; }
        if (correo.isEmpty())      { mostrarError("El correo es obligatorio."); return; }
        if (!EMAIL_PATTERN.matcher(correo).matches()) {
            mostrarError("El correo no tiene un formato válido."); return;
        }
        if (password.isEmpty())    { mostrarError("La contraseña es obligatoria."); return; }
        if (password.length() < 8) { mostrarError("La contraseña debe tener mínimo 8 caracteres."); return; }
        if (tipoDoc == null)       { mostrarError("Selecciona el tipo de documento."); return; }
        if (rol == null)           { mostrarError("Selecciona el rol del empleado."); return; }
        if (ruta == null)          { mostrarError("Debes seleccionar una ruta para el empleado."); return; }
        if (userService == null)   { mostrarError("Servidor no disponible."); return; }

        String adress = construirAdress(rol, ruta);

        try {
            if (empleadoEnEdicion == null) {
                Worker w = new Worker(id, correo, nombre, apellido, password, tipoDoc, adress);
                userService.register(w);
                dataEmpleados.add(w);
            } else {
                empleadoEnEdicion.setName(nombre);
                empleadoEnEdicion.setLastName(apellido);
                empleadoEnEdicion.setMail(correo);
                empleadoEnEdicion.setPassword(password);
                empleadoEnEdicion.setTypeIdetification(tipoDoc);
                empleadoEnEdicion.setAdress(adress);
                empleadoEnEdicion = null;
            }
            tablaEmpleados.refresh();
            mostrarPanel(false);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg != null && msg.contains("id")) {
                mostrarError("Ya existe un empleado con ese ID.");
            } else if (msg != null && msg.contains("email")) {
                mostrarError("Ya existe un empleado con ese correo.");
            } else {
                mostrarError(msg != null ? msg : "Error al guardar.");
            }
        }
    }

    @FXML private void handleCancelar() {
        empleadoEnEdicion = null;
        mostrarPanel(false);
    }

    // ── Navegación ───────────────────────────────────────────────────────────

    @FXML private void irRutas()    { ServerFactory.navigateToRoutes   ((Stage) tablaEmpleados.getScene().getWindow()); }
    @FXML private void irTrenes()   { ServerFactory.navigateToTrains   ((Stage) tablaEmpleados.getScene().getWindow()); }
    @FXML private void irUsuarios() { ServerFactory.navigateToUsers    ((Stage) tablaEmpleados.getScene().getWindow()); }
    @FXML private void irTickets()  { ServerFactory.navigateToTickets  ((Stage) tablaEmpleados.getScene().getWindow()); }

    // ── Helpers UI ───────────────────────────────────────────────────────────

    private void mostrarPanel(boolean v) { panelForm.setVisible(v); panelForm.setManaged(v); }

    private void limpiarFormulario() {
        txtId.setDisable(false);
        txtId.clear(); txtNombre.clear(); txtApellido.clear();
        txtCorreo.clear(); txtPassword.clear();
        cmbTipoDoc.getSelectionModel().clearSelection();
        cmbRol.getSelectionModel().clearSelection();
        cmbRuta.getSelectionModel().clearSelection();
        ocultarError();
    }

    private void mostrarError(String msg) {
        lblErrorForm.setText(msg); lblErrorForm.setVisible(true); lblErrorForm.setManaged(true);
    }

    private void ocultarError() { lblErrorForm.setVisible(false); lblErrorForm.setManaged(false); }

    public void refreshEmployees() { cargarRutas(); cargarTabla(); }

    // ── Helpers de codificación del campo dirección ──────────────────────────
    // Formato: "ROL:Piloto|RUTA:3 – Ruta Bogotá"

    private String construirAdress(String rol, Route ruta) {
        return PREF_ROL + rol + PREF_RUTA + ruta.getId() + " – " + ruta.getName();
    }

    private String extraerRol(String adress) {
        if (adress == null) return ROL_ABORDO;
        if (adress.startsWith(PREF_ROL)) {
            int fin = adress.indexOf(PREF_RUTA);
            return fin == -1 ? adress.substring(PREF_ROL.length())
                             : adress.substring(PREF_ROL.length(), fin);
        }
        return ROL_ABORDO;
    }

    private String extraerNombreRuta(String adress) {
        if (adress == null) return "";
        int idx = adress.indexOf(PREF_RUTA);
        return idx == -1 ? "" : adress.substring(idx + PREF_RUTA.length());
    }
}
