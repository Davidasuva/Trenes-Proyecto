package validarTicket.controller;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import validarTicket.factory.ValidarFactory;
import validarTicket.model.ValidarTicketModel;
import validarTicket.model.ValidarTicketModel.ResultadoValidacion;

import java.net.URL;
import java.util.ResourceBundle;

public class ValidarPasajeroController implements Initializable {

    @FXML private Label lblRuta;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnValidar;
    @FXML private VBox panelForm;

    @FXML private VBox panelResultado;
    @FXML private Label lblResultadoIcono;
    @FXML private Label lblResultadoTitulo;
    @FXML private Label lblResultadoDetalle;

    private ValidarTicketModel model;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        panelResultado.setVisible(false);
        panelResultado.setManaged(false);
        lblError.setVisible(false);
    }

    public void setModel(ValidarTicketModel model) {
        this.model = model;
        if (model.getRutaSeleccionada() != null) {
            lblRuta.setText(model.getRutaSeleccionada().getName()
                    + "  ·  " + model.getRutaSeleccionada().getOrigin().getName()
                    + " → " + model.getRutaSeleccionada().getDestiny().getName()
                    + "  ·  Salida: " + model.getRutaSeleccionada().getDateTravelStr());
        }
    }

    @FXML private void handleValidar() {
        String nombre   = txtNombre.getText().trim();
        String correo   = txtCorreo.getText().trim();
        String password = txtPassword.getText().trim();

        lblError.setVisible(false);

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            mostrarError("Completa todos los campos.");
            return;
        }

        btnValidar.setDisable(true);
        btnValidar.setText("Verificando...");

        new Thread(() -> {
            ResultadoValidacion resultado = model.validar(nombre, correo, password);
            Platform.runLater(() -> {
                btnValidar.setDisable(false);
                btnValidar.setText("Validar");
                mostrarResultado(resultado);
            });
        }).start();
    }

    private void mostrarResultado(ResultadoValidacion resultado) {
        panelForm.setVisible(false);
        panelForm.setManaged(false);
        panelResultado.setVisible(true);
        panelResultado.setManaged(true);

        if (resultado == ResultadoValidacion.VALIDADO) {
            panelResultado.getStyleClass().removeAll("panel-rechazado");
            panelResultado.getStyleClass().add("panel-validado");
            lblResultadoIcono.setText("Yes");
            lblResultadoTitulo.setText("¡Validado! Puede pasar");
            lblResultadoDetalle.setText("Pasajero con ticket activo en esta ruta.");
        } else {
            panelResultado.getStyleClass().removeAll("panel-validado");
            panelResultado.getStyleClass().add("panel-rechazado");
            lblResultadoIcono.setText("No");
            lblResultadoTitulo.setText("Acceso denegado");
            lblResultadoDetalle.setText(resultado == ResultadoValidacion.PASAJERO_NO_ENCONTRADO
                    ? "Datos incorrectos o pasajero no encontrado."
                    : "El pasajero no tiene un ticket activo en esta ruta.");
        }

        PauseTransition pausa = new PauseTransition(Duration.seconds(3));
        pausa.setOnFinished(e -> resetFormulario());
        pausa.play();
    }

    private void resetFormulario() {
        txtNombre.clear();
        txtCorreo.clear();
        txtPassword.clear();
        lblError.setVisible(false);
        panelResultado.setVisible(false);
        panelResultado.setManaged(false);
        panelForm.setVisible(true);
        panelForm.setManaged(true);
    }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }

    @FXML private void handleCambiarRuta() {
        Stage stage = (Stage) btnValidar.getScene().getWindow();
        ValidarFactory.showRouteSelect(stage, model);
    }
}
