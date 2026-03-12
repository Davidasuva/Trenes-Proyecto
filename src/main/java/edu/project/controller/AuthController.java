package edu.project.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            lblError.setText("Por favor completa todos los campos.");
            return;
        }

        // Aquí después conectas con AuthService
        System.out.println("Intentando login: " + usuario);
    }
}
