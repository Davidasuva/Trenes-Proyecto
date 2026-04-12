package edu.project.controller;

import edu.project.view.auth.AuthView;
import edu.project.view.auth.RegisterView;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RegisterController {

    @FXML private TextField     txtUsuario;
    @FXML private TextField     txtCorreo;
    @FXML private PasswordField txtPassword;
    @FXML private TextField     txtPasswordVisible;
    @FXML private PasswordField txtConfirm;
    @FXML private TextField     txtConfirmVisible;
    @FXML private Label         lblError;
    @FXML private Button        btnOjo1;
    @FXML private Button        btnOjo2;
    @FXML private Button        btnRegistrar;
    @FXML private Label         lblIniciarSesion;

    private boolean pass1Visible = false;
    private boolean pass2Visible = false;

    @FXML
    public void initialize() {
        // Sync contraseña 1
        txtPassword.textProperty().addListener((obs, o, n) -> {
            if (!pass1Visible) txtPasswordVisible.setText(n);
        });
        txtPasswordVisible.textProperty().addListener((obs, o, n) -> {
            if (pass1Visible) txtPassword.setText(n);
        });
        // Sync contraseña 2
        txtConfirm.textProperty().addListener((obs, o, n) -> {
            if (!pass2Visible) txtConfirmVisible.setText(n);
        });
        txtConfirmVisible.textProperty().addListener((obs, o, n) -> {
            if (pass2Visible) txtConfirm.setText(n);
        });

        // Enter navega entre campos
        txtUsuario.setOnAction(e -> txtCorreo.requestFocus());
        txtCorreo.setOnAction(e -> txtPassword.requestFocus());
        txtPassword.setOnAction(e -> txtConfirm.requestFocus());
        txtPasswordVisible.setOnAction(e -> txtConfirm.requestFocus());
        txtConfirm.setOnAction(e -> handleRegister());
        txtConfirmVisible.setOnAction(e -> handleRegister());
    }

    @FXML
    public void handleTogglePassword1() {
        pass1Visible = !pass1Visible;
        txtPassword.setVisible(!pass1Visible);
        txtPasswordVisible.setVisible(pass1Visible);
        btnOjo1.setText(pass1Visible ? "🙈" : "👁");
        if (pass1Visible) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.requestFocus();
        }
    }

    @FXML
    public void handleTogglePassword2() {
        pass2Visible = !pass2Visible;
        txtConfirm.setVisible(!pass2Visible);
        txtConfirmVisible.setVisible(pass2Visible);
        btnOjo2.setText(pass2Visible ? "*" : "👁");
        if (pass2Visible) {
            txtConfirmVisible.setText(txtConfirm.getText());
            txtConfirmVisible.requestFocus();
            txtConfirmVisible.positionCaret(txtConfirmVisible.getText().length());
        } else {
            txtConfirm.setText(txtConfirmVisible.getText());
            txtConfirm.requestFocus();
        }
    }

    @FXML
    public void handleRegister() {
        String usuario  = txtUsuario.getText().trim();
        String correo   = txtCorreo.getText().trim();
        String password = pass1Visible ? txtPasswordVisible.getText() : txtPassword.getText();
        String confirm  = pass2Visible ? txtConfirmVisible.getText()  : txtConfirm.getText();

        if (usuario.isEmpty()) { mostrarError("Ingresa un usuario."); sacudir(txtUsuario); return; }
        if (correo.isEmpty())  { mostrarError("Ingresa tu correo.");  sacudir(txtCorreo);  return; }
        if (!correo.contains("@")) { mostrarError("Correo no válido."); sacudir(txtCorreo); return; }
        if (password.isEmpty()) { mostrarError("Ingresa una contraseña."); sacudir(txtPassword); return; }
        if (!password.equals(confirm)) {
            mostrarError("Las contraseñas no coinciden.");
            sacudir(txtConfirm);
            return;
        }

        // TODO: conectar con tu servicio de registro real
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro exitoso");
        alert.setHeaderText(null);
        alert.setContentText("¡Cuenta creada! Ya puedes iniciar sesión.");
        alert.showAndWait();

        // Volver al login tras registrarse
        try {
            Stage stage = (Stage) btnRegistrar.getScene().getWindow();
            AuthView.show(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void handleVolverLogin() {
        try {
            Stage stage = (Stage) lblIniciarSesion.getScene().getWindow();
            AuthView.show(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(200), lblError);
        ft.setFromValue(0); ft.setToValue(1);
        ft.play();
    }

    private void sacudir(Node n) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(55), n);
        tt.setFromX(0); tt.setByX(6);
        tt.setCycleCount(6); tt.setAutoReverse(true);
        tt.play();
    }
}