package server.controller.auth;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import server.model.user.AbstractUser;
import server.model.user.Admin;
import server.model.user.UserService;
import server.view.server.ServerView;
import environment.Environment;
import server.model.ServerModel;

public class AuthController {

    private UserService userService;

    @FXML private TextField     txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private TextField     txtPasswordVisible;
    @FXML private Label         lblError;
    @FXML private Button        btnOjo;
    @FXML private Button        btnIngresar;

    private boolean passwordVisible = false;

    @FXML
    public void initialize() {
        try {
            userService = new UserService();
            // Usuario de prueba — reemplazar con carga desde BD o archivo en producción
            userService.register(new Admin(
                    "0", "Admin@project.com", "Admin123",
                    "Admin", "1234", "C.C", "cr 29#92-49", 3
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Sincronizar campos de contraseña
        txtPassword.textProperty().addListener((obs, o, n) -> {
            if (!passwordVisible) txtPasswordVisible.setText(n);
        });
        txtPasswordVisible.textProperty().addListener((obs, o, n) -> {
            if (passwordVisible) txtPassword.setText(n);
        });

        // Enter para avanzar al siguiente campo / hacer login
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
        String mail     = txtUsuario.getText().trim();
        String password = passwordVisible
                ? txtPasswordVisible.getText()
                : txtPassword.getText();

        if (mail.isEmpty()) {
            mostrarError("Ingresa tu mail.");
            sacudir(txtUsuario);
            return;
        }
        if (password.isEmpty()) {
            mostrarError("Ingresa tu contraseña.");
            sacudir(txtPassword);
            return;
        }

        try {
            AbstractUser user = userService.userPerEmailAndPassword(mail, password, null);
            if (user != null) {
                lblError.setVisible(false);
                abrirServerView();           // navega directamente, sin Alert bloqueante
            } else {
                mostrarError("Credenciales incorrectas.");
                sacudir(btnIngresar);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error interno. Revisa la consola.");
        }
    }

    // ──────────────────────────────────────────────────────────
    // Privados
    // ──────────────────────────────────────────────────────────

    private void abrirServerView() {
        try {
            Environment env   = Environment.getInstance();
            ServerModel model = new ServerModel(
                    env.getIp(), env.getPort(), env.getServiceName()
            );

            ServerView serverView = new ServerView(model);   // usa ServerView, no ServerFactory

            Stage stage = (Stage) btnIngresar.getScene().getWindow();
            stage.setTitle("trenes — Server");
            stage.setScene(new Scene(serverView.getView(), 400, 300));

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir la vista del servidor.");
        }
    }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(200), lblError);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void sacudir(Node n) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(55), n);
        tt.setFromX(0);
        tt.setByX(6);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.play();
    }
}