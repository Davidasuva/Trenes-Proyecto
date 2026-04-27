package server.controller.user;

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
import server.model.user.Passenger;
import server.model.user.UserService;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class UserController implements Initializable {

    // ── Tabla ──
    @FXML private TextField txtBuscar;
    @FXML private TableView<AbstractUser> tablaUsuarios;
    @FXML private TableColumn<AbstractUser, String> colId, colNombre, colCorreo, colDocumento, colTelefono;
    @FXML private TableColumn<AbstractUser, String> colAcciones;

    // ── Formulario ──
    @FXML private VBox panelForm;
    @FXML private Label lblTituloForm, lblErrorForm;
    @FXML private TextField txtId, txtNombre, txtApellido, txtCorreo, txtPassword, txtDireccion;
    @FXML private ComboBox<String> cmbTipoDoc;

    private UserService userService;
    private ObservableList<AbstractUser> dataUsuarios = FXCollections.observableArrayList();

    /** Usuario siendo editado; null = modo creación */
    private AbstractUser usuarioEnEdicion = null;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbTipoDoc.getItems().addAll("Cédula de ciudadanía", "Pasaporte", "Tarjeta de identidad");

        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getName() + " " + c.getValue().getLastName()));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMail()));
        colDocumento.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTypeIdetification() + " " + c.getValue().getId()));
        colTelefono.setCellValueFactory(c -> {
            var phones = c.getValue().getPhoneNumbers();
            try {
                return new SimpleStringProperty(
                        phones.iterator().hasNext() ? phones.iterator().next() : "—");
            } catch (Exception e) { return new SimpleStringProperty("—"); }
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

        tablaUsuarios.setItems(dataUsuarios);
    }

    public void setModel(ServerModel model) {
        this.userService = model.getUserService();
        cargarTabla();
    }

    private void cargarTabla() {
        dataUsuarios.clear();
        if (userService == null) return;
        try {
            Iterator<AbstractUser> it = userService.seeUserPerCategory(1).iterator();
            while (it.hasNext()) dataUsuarios.add(it.next());
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Abrir edición ────────────────────────────────────────────────────────

    private void abrirEdicion(AbstractUser u) {
        usuarioEnEdicion = u;
        lblTituloForm.setText("Editar Usuario");

        // El ID no se puede cambiar en edición
        txtId.setText(u.getId());
        txtId.setDisable(true);

        txtNombre.setText(u.getName());
        txtApellido.setText(u.getLastName());
        txtCorreo.setText(u.getMail());
        txtPassword.setText(u.getPassword());
        txtDireccion.setText(u.getAdress());
        cmbTipoDoc.setValue(u.getTypeIdetification());

        ocultarError();
        mostrarPanel(true);
    }

    // ── Eliminar con confirmación ─────────────────────────────────────────────

    private void confirmarYEliminar(AbstractUser u) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar usuario?");
        confirmacion.setContentText("¿Estás seguro de que deseas eliminar a "
                + u.getName() + " " + u.getLastName() + "? Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            eliminarUsuario(u);
        }
    }

    private void eliminarUsuario(AbstractUser u) {
        if (userService == null) return;
        try {
            userService.removeUser(u.getId());
            dataUsuarios.remove(u);
            tablaUsuarios.refresh();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // ── Handlers ────────────────────────────────────────────────────────────

    @FXML private void handleBuscar() {
        String q = txtBuscar.getText().toLowerCase().trim();
        if (q.isEmpty()) { tablaUsuarios.setItems(dataUsuarios); return; }
        ObservableList<AbstractUser> filtrado = FXCollections.observableArrayList();
        for (AbstractUser u : dataUsuarios) {
            if ((u.getName() + " " + u.getLastName()).toLowerCase().contains(q)
                    || u.getMail().toLowerCase().contains(q)
                    || u.getId().contains(q))
                filtrado.add(u);
        }
        tablaUsuarios.setItems(filtrado);
    }

    @FXML private void handleNuevoUsuario() {
        usuarioEnEdicion = null;
        lblTituloForm.setText("Nuevo Usuario");
        limpiarFormulario();
        mostrarPanel(true);
    }

    @FXML private void handleGuardar() {
        String id       = txtId.getText().trim();
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String correo   = txtCorreo.getText().trim();
        String password = txtPassword.getText().trim();
        String dir      = txtDireccion.getText().trim();
        String tipoDoc  = cmbTipoDoc.getValue();

        // ── Validaciones ──
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
        if (userService == null)   { mostrarError("Servidor no disponible."); return; }

        try {
            if (usuarioEnEdicion == null) {
                // ── Crear nuevo ──
                Passenger p = new Passenger(id, correo, nombre, apellido, password, tipoDoc,
                        dir.isEmpty() ? "—" : dir);
                userService.registerPassenger(p);
                dataUsuarios.add(p);
            } else {
                // ── Editar existente ──
                usuarioEnEdicion.setName(nombre);
                usuarioEnEdicion.setLastName(apellido);
                usuarioEnEdicion.setMail(correo);
                usuarioEnEdicion.setPassword(password);
                usuarioEnEdicion.setTypeIdetification(tipoDoc);
                usuarioEnEdicion.setAdress(dir.isEmpty() ? "—" : dir);
                usuarioEnEdicion = null;
            }
            tablaUsuarios.refresh();
            mostrarPanel(false);
        } catch (Exception ex) {
            // Mensaje amigable para ID duplicado
            String msg = ex.getMessage();
            if (msg != null && msg.contains("id")) {
                mostrarError("Ya existe un usuario con ese ID.");
            } else if (msg != null && msg.contains("email")) {
                mostrarError("Ya existe un usuario con ese correo.");
            } else {
                mostrarError(msg != null ? msg : "Error al guardar.");
            }
        }
    }

    @FXML private void handleCancelar() {
        usuarioEnEdicion = null;
        mostrarPanel(false);
    }

    // ── Navegación ──
    @FXML private void irRutas()        { ServerFactory.navigateToRoutes  ((Stage) tablaUsuarios.getScene().getWindow()); }
    @FXML private void irTrenes()       { ServerFactory.navigateToTrains  ((Stage) tablaUsuarios.getScene().getWindow()); }
    @FXML private void irTickets() { ServerFactory.navigateToTickets((Stage) tablaUsuarios.getScene().getWindow()); }

    // ── Helpers ──
    private void mostrarPanel(boolean v) { panelForm.setVisible(v); panelForm.setManaged(v); }
    private void limpiarFormulario() {
        txtId.setDisable(false);
        txtId.clear(); txtNombre.clear(); txtApellido.clear();
        txtCorreo.clear(); txtPassword.clear(); txtDireccion.clear();
        cmbTipoDoc.getSelectionModel().clearSelection();
        ocultarError();
    }
    private void mostrarError(String msg) {
        lblErrorForm.setText(msg); lblErrorForm.setVisible(true); lblErrorForm.setManaged(true);
    }
    private void ocultarError() { lblErrorForm.setVisible(false); lblErrorForm.setManaged(false); }

    public void refreshUsers() { cargarTabla(); }
}
