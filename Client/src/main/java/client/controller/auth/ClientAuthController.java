package client.controller.auth;

import client.factory.ClientFactory;
import client.model.ClientModel;
import client.view.dashboard.DashboardView;
import environment.Environment;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import server.model.observer.Observer;

public class ClientAuthController {

    @FXML private Button btnTabLogin, btnTabRegister;
    @FXML private VBox panelLogin;
    @FXML private TextField txtLoginMail;
    @FXML private PasswordField txtLoginPass;
    @FXML private TextField txtLoginPassVisible;
    @FXML private Button btnOjoLogin;
    @FXML private VBox panelRegister;
    @FXML private TextField txtRegNombre, txtRegApellido, txtRegMail;
    @FXML private TextField txtRegId, txtRegDir;
    @FXML private ComboBox<String> cmbRegTipoId;
    @FXML private PasswordField txtRegPass;
    @FXML private TextField txtRegPassVisible;
    @FXML private Button btnOjoReg;
    @FXML private Label lblError;
    @FXML private Button btnAccion;

    private boolean modoLogin        = true;
    private boolean loginPassVisible = false;
    private boolean regPassVisible   = false;

    private ClientModel model;
    private Observer modelObserver;

    @FXML
    public void initialize() {
        try {
            cmbRegTipoId.setItems(FXCollections.observableArrayList(
                    "Cédula de Ciudadanía (C.C.)",
                    "Tarjeta de Identidad (T.I.)",
                    "Cédula de Extranjería (C.E.)",
                    "Pasaporte",
                    "NIT"
            ));

            Environment env = Environment.getInstance();
            model = new ClientModel(env.getIp(), env.getPort(), env.getServiceName());

            modelObserver = new Observer(model) {
                @Override
                public void update() {
                    Platform.runLater(() -> mostrarError(model.getLogger()));
                }
            };

            new Thread(() -> {
                boolean connected = model.connect();
                Platform.runLater(() -> {
                    if (!connected) {
                        mostrarError("No se pudo conectar al servidor. Verifica que esté activo.");
                        btnAccion.setDisable(true);
                    }
                });
            }).start();

            txtLoginPass.textProperty().addListener((obs, o, n) -> {
                if (!loginPassVisible) txtLoginPassVisible.setText(n);
            });
            txtLoginPassVisible.textProperty().addListener((obs, o, n) -> {
                if (loginPassVisible) txtLoginPass.setText(n);
            });

            txtRegPass.textProperty().addListener((obs, o, n) -> {
                if (!regPassVisible) txtRegPassVisible.setText(n);
            });
            txtRegPassVisible.textProperty().addListener((obs, o, n) -> {
                if (regPassVisible) txtRegPass.setText(n);
            });

            txtLoginMail.setOnAction(e -> txtLoginPass.requestFocus());
            txtLoginPass.setOnAction(e -> handleAccion());
            txtLoginPassVisible.setOnAction(e -> handleAccion());
            showLogin();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al iniciar la aplicación: " + e.getMessage());
        }
    }

    @FXML public void showLogin() {
        modoLogin = true;
        panelLogin.setVisible(true);   panelLogin.setManaged(true);
        panelRegister.setVisible(false); panelRegister.setManaged(false);
        btnTabLogin.getStyleClass().setAll("tab-active");
        btnTabRegister.getStyleClass().setAll("tab-inactive");
        btnAccion.setText("Iniciar sesión");
        lblError.setVisible(false);
    }

    @FXML public void showRegister() {
        modoLogin = false;
        panelLogin.setVisible(false);   panelLogin.setManaged(false);
        panelRegister.setVisible(true); panelRegister.setManaged(true);
        btnTabLogin.getStyleClass().setAll("tab-inactive");
        btnTabRegister.getStyleClass().setAll("tab-active");
        btnAccion.setText("Crear cuenta");
        lblError.setVisible(false);
    }

    @FXML public void toggleLoginPass() {
        loginPassVisible = !loginPassVisible;
        togglePass(txtLoginPass, txtLoginPassVisible, btnOjoLogin, loginPassVisible);
    }

    @FXML public void toggleRegPass() {
        regPassVisible = !regPassVisible;
        togglePass(txtRegPass, txtRegPassVisible, btnOjoReg, regPassVisible);
    }

    private void togglePass(PasswordField hidden, TextField visible, Button ojo, boolean show) {
        hidden.setVisible(!show);
        visible.setVisible(show);
        ojo.setText(show ? "*" : "👁");
        if (show) {
            visible.setText(hidden.getText());
            visible.requestFocus();
            visible.positionCaret(visible.getText().length());
        } else {
            hidden.setText(visible.getText());
            hidden.requestFocus();
        }
    }

    @FXML
    public void handleAccion() {
        lblError.setVisible(false);
        if (modoLogin) doLogin();
        else           doRegister();
    }

    private void doLogin() {
        String mail = txtLoginMail.getText().trim();
        String pass = loginPassVisible ? txtLoginPassVisible.getText() : txtLoginPass.getText();

        if (mail.isEmpty()) { sacudir(txtLoginMail); return; }
        if (pass.isEmpty()) { sacudir(txtLoginPass);  return; }

        btnAccion.setDisable(true);
        btnAccion.setText("Conectando...");
        new Thread(() -> {
            boolean success = model.connect(mail, pass);
            Platform.runLater(() -> {
                btnAccion.setDisable(false);
                btnAccion.setText("Iniciar sesión");
                if (success) abrirDashboard();
                else {
                    sacudir(btnAccion);
                    mostrarError("Credenciales incorrectas o error de conexión");
                }
            });
        }).start();
    }

    private void doRegister() {
        String nombre   = txtRegNombre.getText().trim();
        String apellido = txtRegApellido.getText().trim();
        String mail     = txtRegMail.getText().trim();
        String id       = txtRegId.getText().trim();
        String tipoId   = cmbRegTipoId.getValue();
        String dir      = txtRegDir.getText().trim();
        String pass     = regPassVisible ? txtRegPassVisible.getText() : txtRegPass.getText();

        if (nombre.isEmpty() || apellido.isEmpty() || mail.isEmpty()
                || id.isEmpty() || tipoId == null || dir.isEmpty() || pass.isEmpty()) {
            mostrarError("Completa todos los campos.");
            sacudir(btnAccion);
            return;
        }

        if (model.registerPassenger(id, mail, nombre, apellido, pass, tipoId, dir)) {
            abrirDashboard();
        } else {
            sacudir(btnAccion);
        }
    }

    private void abrirDashboard() {
        try {
            DashboardView dashboard = new DashboardView(model);
            Stage stage = (Stage) btnAccion.getScene().getWindow();
            stage.setTitle("trenes — " + model.getCurrentPassenger().getName());
            stage.setResizable(true);
            stage.setScene(new Scene(dashboard.getView(), 960, 640));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al abrir el dashboard.");
        }
    }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(200), lblError);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    private void sacudir(Node n) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(55), n);
        tt.setFromX(0); tt.setByX(6);
        tt.setCycleCount(6); tt.setAutoReverse(true);
        tt.play();
    }
}
