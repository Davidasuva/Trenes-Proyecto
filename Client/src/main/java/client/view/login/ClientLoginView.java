package client.view.login;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientLoginView {

    private ClientLoginView() {}

    public static void show(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                ClientLoginView.class.getResource("ClientLoginView.fxml")
        );
        Parent root  = loader.load();
        Scene  scene = new Scene(root, 720, 480);

        java.net.URL css = ClientLoginView.class.getResource("ClientLoginView.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        stage.setTitle("trenes — Acceso");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }
}
