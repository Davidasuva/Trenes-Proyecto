package edu.project.controller;

import edu.project.view.auth.RegisterView;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AuthController {

    private static final String USUARIO_VALIDO  = "admin";
    private static final String PASSWORD_VALIDA = "1234";

    @FXML private TextField     txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField     txtPasswordVisible;
    @FXML private Label         lblError;
    @FXML private Button        btnOjo;
    @FXML private Button        btnIngresar;
    @FXML private Label         lblCrearCuenta;

    private boolean passwordVisible = false;

    @FXML
    public void initialize() {
        txtPassword.textProperty().addListener((obs, o, n) -> {
            if (!passwordVisible) txtPasswordVisible.setText(n);
        });
        txtPasswordVisible.textProperty().addListener((obs, o, n) -> {
            if (passwordVisible) txtPassword.setText(n);
        });
        txtUsuario.setOnAction(e -> txtPassword.requestFocus());
        txtPassword.setOnAction(e -> handleLogin());
        txtPasswordVisible.setOnAction(e -> handleLogin());
    }

    @FXML
    public void handleTogglePassword() {
        passwordVisible = !passwordVisible;
        txtPassword.setVisible(!passwordVisible);
        txtPasswordVisible.setVisible(passwordVisible);
        btnOjo.setText(passwordVisible ? "*" : "👁");
        if (passwordVisible) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.requestFocus();
            txtPasswordVisible.positionCaret(txtPasswordVisible.getText().length());
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.requestFocus();
        }
    }

    @FXML
    public void handleLogin() {
        String usuario  = txtUsuario.getText().trim();
        String password = passwordVisible
                ? txtPasswordVisible.getText()
                : txtPassword.getText();

        if (usuario.isEmpty()) {
            mostrarError("Ingresa tu usuario.");
            sacudir(txtUsuario);
            return;
        }
        if (password.isEmpty()) {
            mostrarError("Ingresa tu contraseña.");
            sacudir(txtPassword);
            return;
        }
        if (usuario.equals(USUARIO_VALIDO) && password.equals(PASSWORD_VALIDA)) {
            lblError.setVisible(false);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Acceso concedido");
            alert.setHeaderText(null);
            alert.setContentText("¡Bienvenido, " + usuario + "!");
            alert.showAndWait();
            // TODO: navegar a la vista principal
        } else {
            mostrarError("Demasiados intentos, cuenta bloqueada 15 min.");
            sacudir(btnIngresar);
        }
    }

    @FXML
    public void handleCrearCuenta() {
        try {
            Stage stage = (Stage) lblCrearCuenta.getScene().getWindow();
            RegisterView.show(stage);
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