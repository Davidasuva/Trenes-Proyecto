package client;

import client.factory.ClientFactory;
import environment.Environment;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            System.out.println("=== INICIANDO APLICACIÓN ===");
            System.out.println("Directorio de trabajo: " + System.getProperty("user.dir"));

            Environment env = Environment.getInstance();
            System.out.println("Environment - IP: " + env.getIp() + ", Port: " + env.getPort());

            Platform.setImplicitExit(true);
            ClientFactory.showLogin(stage);
            System.out.println("Vista de login mostrada correctamente");

        } catch (Exception e) {
            System.err.println("ERROR FATAL en start():");
            e.printStackTrace();

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR
            );
            alert.setTitle("Error crítico");
            alert.setHeaderText("No se pudo iniciar la aplicación");
            alert.setContentText(e.getMessage() + "\n\nRevisa la consola para más detalles.");
            alert.showAndWait();

            Platform.exit();
        }
    }

    @Override
    public void stop() {
        System.out.println("Aplicación cerrada");
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        System.out.println("=== PUNTO DE ENTRADA PRINCIPAL ===");
        launch(args);
    }
}
