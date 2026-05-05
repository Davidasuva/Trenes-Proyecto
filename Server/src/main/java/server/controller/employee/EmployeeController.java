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
    @FXML private TableColumn<AbstractUser, String> colId, colNombre, colCorreo, colDocumento, colRol;
    @FXML private TableColumn<AbstractUser, String> colAcciones;

    // ── Formulario ──
    @FXML private VBox panelForm;
    @FXML private Label lblTituloForm, lblErrorForm;
    @FXML private TextField txtId, txtNombre, txtApellido, txtCorreo, txtPassword, txtDireccion;
    @FXML private ComboBox<String> cmbTipoDoc;
    @FXML private ComboBox<String> cmbRol;

    private UserService userService;
    private ObservableList<AbstractUser> dataEmpleados = FXCollections.observableArrayList();

    /** Empleado siendo editado; null = modo creación */
    private AbstractUser empleadoEnEdicion = null;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");

    // Roles disponibles (ambos son tipo 2 = Worker en el modelo actual)
    private static final String ROL_PILOTO   = "Piloto";
    private static final String ROL_ABORDO   = "Personal de abordo";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbTipoDoc.getItems().addAll("Cédula de ciudadanía", "Pasaporte", "Tarjeta de identidad");
        cmbRol.getItems().addAll(ROL_PILOTO, ROL_ABORDO);

        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getName() + " " + c.getValue().getLastName()));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMail()));
        colDocumento.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTypeIdetification() + " " + c.getValue().getId()));
        colRol.setCellValueFactory(c -> {
            // El rol lo guardamos en la dirección con un prefijo especial
            String addr = c.getValue().getAdress();
            if (addr != null && addr.startsWith("ROL:")) {
                return new SimpleStringProperty(addr.substring(4));
            }
            return new SimpleStringProperty("Personal de abordo");
        });

        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox box = new HBox(6, btnEditar, btnEliminar);
            {
                btnEditar.getStyleClass().add("btn-editar");
                btnEliminar.getStyleClass().add("btn-peligro");

                btnEditar.setOnAction(e -> {
                    AbstractUser u = getTableView().getItems().get(getIndex());
                    abrirEdicion(u);
                });
                btnEliminar.setOnAction(e -> {
                    AbstractUser u = getTableView().getItems().get(getIndex());
                    confirmarYEliminar(u);
                });
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tablaEmpleados.setItems(dataEmpleados);
    }

    public void setModel(ServerModel model) {
        this.userService = model.getUserService();
        cargarTabla();
    }

    private void cargarTabla() {
        dataEmpleados.clear();
        if (userService == null) return;
        try {
            // Tipo 2 = Worker (empleados)
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

        // Recuperar el rol guardado en la dirección
        String addr = u.getAdress();
        if (addr != null && addr.startsWith("ROL:")) {
            cmbRol.setValue(addr.substring(4));
        } else {
            cmbRol.setValue(ROL_ABORDO);
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

    @FXML private void handleBuscar() {
        String q = txtBuscar.getText().toLowerCase().trim();
        if (q.isEmpty()) { tablaEmpleados.setItems(dataEmpleados); return; }
        ObservableList<AbstractUser> filtrado = FXCollections.observableArrayList();
        for (AbstractUser u : dataEmpleados) {
            if ((u.getName() + " " + u.getLastName()).toLowerCase().contains(q)
                    || u.getMail().toLowerCase().contains(q)
                    || u.getId().contains(q))
                filtrado.add(u);
        }
        tablaEmpleados.setItems(filtrado);
    }

    @FXML private void handleNuevoEmpleado() {
        empleadoEnEdicion = null;
        lblTituloForm.setText("Nuevo Empleado");
        limpiarFormulario();
        mostrarPanel(true);
    }

    @FXML private void handleGuardar() {
        String id       = txtId.getText().trim();
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo   = txtCorreo.getText().trim();
        String password = txtPassword.getText().trim();
        String tipoDoc  = cmbTipoDoc.getValue();
        String rol      = cmbRol.getValue();

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
        if (userService == null)   { mostrarError("Servidor no disponible."); return; }

        // Guardamos el rol en el campo dirección con prefijo "ROL:"
        String adressConRol = "ROL:" + rol;

        try {
            if (empleadoEnEdicion == null) {
                Worker w = new Worker(id, correo, nombre, apellido, password, tipoDoc, adressConRol);
                userService.register(w);
                dataEmpleados.add(w);
            } else {
                empleadoEnEdicion.setName(nombre);
                empleadoEnEdicion.setLastName(apellido);
                empleadoEnEdicion.setMail(correo);
                empleadoEnEdicion.setPassword(password);
                empleadoEnEdicion.setTypeIdetification(tipoDoc);
                empleadoEnEdicion.setAdress(adressConRol);
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

    @FXML private void irRutas()    { ServerFactory.navigateToRoutes  ((Stage) tablaEmpleados.getScene().getWindow()); }
    @FXML private void irTrenes()   { ServerFactory.navigateToTrains  ((Stage) tablaEmpleados.getScene().getWindow()); }
    @FXML private void irUsuarios() { ServerFactory.navigateToUsers   ((Stage) tablaEmpleados.getScene().getWindow()); }
    @FXML private void irTickets()  { ServerFactory.navigateToTickets ((Stage) tablaEmpleados.getScene().getWindow()); }

    private void mostrarPanel(boolean v) { panelForm.setVisible(v); panelForm.setManaged(v); }
    private void limpiarFormulario() {
        txtId.setDisable(false);
        txtId.clear(); txtNombre.clear(); txtApellido.clear();
        txtCorreo.clear(); txtPassword.clear();
        cmbTipoDoc.getSelectionModel().clearSelection();
        cmbRol.getSelectionModel().clearSelection();
        ocultarError();
    }
    private void mostrarError(String msg) {
        lblErrorForm.setText(msg); lblErrorForm.setVisible(true); lblErrorForm.setManaged(true);
    }
    private void ocultarError() { lblErrorForm.setVisible(false); lblErrorForm.setManaged(false); }

    public void refreshEmployees() { cargarTabla(); }
}
