package server.controller.user;

import edu.uva.model.iterator.Iterator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import server.factory.ServerFactory;
import server.model.ServerModel;
import server.model.user.AbstractUser;
import server.model.user.Passenger;
import server.model.user.UserService;

import java.net.URL;
import java.util.ResourceBundle;

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
            private final Button btnEliminar = new Button("Eliminar");
            {
                btnEliminar.getStyleClass().add("btn-peligro");
                btnEliminar.setOnAction(e -> {
                    AbstractUser u = getTableView().getItems().get(getIndex());
                    eliminarUsuario(u);
                });
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEliminar);
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
            // Solo pasajeros (tipo 1)
            Iterator<AbstractUser> it = userService.seeUserPerCategory(1).iterator();
            while (it.hasNext()) dataUsuarios.add(it.next());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void eliminarUsuario(AbstractUser u) {
        if (userService == null) return;
        try {
            userService.removeUser(u.getId());
            dataUsuarios.remove(u);
            tablaUsuarios.refresh();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

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
        lblTituloForm.setText("Nuevo Usuario");
        limpiarFormulario();
        mostrarPanel(true);
    }

    @FXML private void handleGuardar() {
        String id        = txtId.getText().trim();
        String nombre    = txtNombre.getText().trim();
        String apellido  = txtApellido.getText().trim();
        String correo    = txtCorreo.getText().trim();
        String password  = txtPassword.getText().trim();
        String dir       = txtDireccion.getText().trim();
        String tipoDoc   = cmbTipoDoc.getValue();

        if (id.isEmpty())       { mostrarError("El ID es obligatorio."); return; }
        if (nombre.isEmpty())   { mostrarError("El nombre es obligatorio."); return; }
        if (apellido.isEmpty()) { mostrarError("El apellido es obligatorio."); return; }
        if (correo.isEmpty())   { mostrarError("El correo es obligatorio."); return; }
        if (password.isEmpty()) { mostrarError("La contraseña es obligatoria."); return; }
        if (tipoDoc == null)    { mostrarError("Selecciona el tipo de documento."); return; }
        if (userService == null){ mostrarError("Servidor no disponible."); return; }

        try {
            Passenger p = new Passenger(id, correo, nombre, apellido, password, tipoDoc,
                    dir.isEmpty() ? "—" : dir);
            userService.registerPassenger(p);
            dataUsuarios.add(p);
            tablaUsuarios.refresh();
            mostrarPanel(false);
        } catch (Exception ex) { mostrarError(ex.getMessage()); }
    }

    @FXML private void handleCancelar() { mostrarPanel(false); }

    // ── Navegación ──
    @FXML private void irRutas()        { ServerFactory.navigateToRoutes  ((Stage) tablaUsuarios.getScene().getWindow()); }
    @FXML private void irTrenes()       { ServerFactory.navigateToTrains  ((Stage) tablaUsuarios.getScene().getWindow()); }
    @FXML private void irTrabajadores() { ServerFactory.navigateToWorkers ((Stage) tablaUsuarios.getScene().getWindow()); }

    // ── Helpers ──
    private void mostrarPanel(boolean v) { panelForm.setVisible(v); panelForm.setManaged(v); }
    private void limpiarFormulario() {
        txtId.clear(); txtNombre.clear(); txtApellido.clear();
        txtCorreo.clear(); txtPassword.clear(); txtDireccion.clear();
        cmbTipoDoc.getSelectionModel().clearSelection();
        ocultarError();
    }
    private void mostrarError(String msg) {
        lblErrorForm.setText(msg); lblErrorForm.setVisible(true); lblErrorForm.setManaged(true);
    }
    private void ocultarError() { lblErrorForm.setVisible(false); lblErrorForm.setManaged(false); }

    public void refreshUsers(){
        cargarTabla();
    }
}