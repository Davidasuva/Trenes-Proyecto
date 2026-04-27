package server.view.ticket;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TicketView {
    public static void show(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(TicketView.class.getResource("TicketView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1400, 800);
        stage.setTitle("trenes — Tickets");
        stage.setScene(scene);
        stage.setMaximized(true);
    }
}
