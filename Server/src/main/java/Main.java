import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import server.view.auth.login.Login;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Cuando se cierren TODAS las ventanas JavaFX, la app termina sola
        Platform.setImplicitExit(true);

        Login.show(stage);
    }

    @Override
    public void stop() throws Exception {
        // stop() se llama automáticamente al cerrar la última ventana.
        // Forzar salida del proceso para matar los hilos RMI no-daemon.
        System.out.println("Aplicación cerrada — liberando puertos RMI.");
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}