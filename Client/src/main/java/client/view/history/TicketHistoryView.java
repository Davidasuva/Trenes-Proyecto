package client.view.history;

import client.controller.history.TicketHistoryController;
import client.model.ClientModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class TicketHistoryView {

    private final Parent root;
    private final TicketHistoryController controller;

    public TicketHistoryView(ClientModel model) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("TicketHistoryView.fxml")
        );
        this.root       = loader.load();
        this.controller = loader.getController();
        controller.setModel(model);
    }

    public Parent getView() { return root; }
}
