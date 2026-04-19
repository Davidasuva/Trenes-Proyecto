package client.controller.dashboard;

import client.factory.ClientFactory;
import client.model.ClientModel;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import server.model.observer.Observer;
import server.model.route.Route;
import server.model.ticket.Ticket;
public class DashboardController {

    @FXML private Label              lblBienvenido;
    @FXML private ListView<Route>    listRoutes;
    @FXML private Label              lblRutaSeleccionada;
    @FXML private ChoiceBox<String>  choiceCategoria;
    @FXML private Label              lblTicketStatus;
    @FXML private Label              lblLog;
    @FXML private Button             btnComprar;

    private ClientModel model;

    public void setModel(ClientModel model) {
        this.model = model;
        initUI();
        subscribeToModel();
        refreshRoutes();
    }

    private void initUI() {
        lblBienvenido.setText("Hola, " + model.getCurrentPassenger().getName() + "  •  "
                + model.getCurrentPassenger().getMail());

        choiceCategoria.setItems(FXCollections.observableArrayList(
                "1 — Económico",
                "2 — Ejecutivo",
                "3 — Primera clase"
        ));
        choiceCategoria.setValue("1 — Económico");

        // Al seleccionar una ruta, mostrar su nombre en el panel de compra
        listRoutes.getSelectionModel().selectedItemProperty().addListener((obs, old, route) -> {
            if (route != null) {
                lblRutaSeleccionada.setText(
                        route.getName() + "\n"
                                + route.getDateTravel() + " → " + route.getDateArrival() + "\n"
                                + String.format("%.0f km", route.getTotalDistance())
                );
            }
        });

        listRoutes.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Route r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r == null) {
                    setText(null);
                } else {
                    setText(r.getName() + "  |  " + r.getDateTravel()
                            + " → " + r.getDateArrival()
                            + "  (" + String.format("%.0f", r.getTotalDistance()) + " km)");
                }
            }
        });
    }

    private void subscribeToModel() {
        new Observer(model) {
            @Override
            public void update() {
                Platform.runLater(() -> lblLog.setText(model.getLogger()));
            }
        };
    }

    @FXML
    public void refreshRoutes() {
        LinkedList<Route> routes = model.getAvailableRoutes();

        ObservableList<Route> observableRoutes = FXCollections.observableArrayList();
        Iterator<Route> iterator=routes.iterator();
        while(iterator.hasNext()){
            observableRoutes.add(iterator.next());
        }
        listRoutes.setItems(observableRoutes);
        if (routes.isEmpty()) {
            lblLog.setText("No hay rutas disponibles. El admin debe publicarlas primero.");
        }
    }

    @FXML
    public void handleComprar() {
        Route ruta = listRoutes.getSelectionModel().getSelectedItem();
        if (ruta == null) {
            lblTicketStatus.setText("Selecciona una ruta primero.");
            return;
        }

        // Extraer el número de categoría del texto "1 — Económico"
        String seleccion = choiceCategoria.getValue();
        int categoria = Integer.parseInt(seleccion.substring(0, 1));

        btnComprar.setDisable(true);
        lblTicketStatus.setText("Procesando...");

        // Ejecutar en hilo separado para no bloquear la UI durante la llamada RMI
        new Thread(() -> {
            Ticket ticket = model.buyTicket(ruta, categoria);
            Platform.runLater(() -> {
                btnComprar.setDisable(false);
                if (ticket != null) {
                    lblTicketStatus.setText(
                            "✔ Ticket confirmado\n"
                                    + "ID: " + ticket.getId() + "\n"
                                    + "Ruta: " + ticket.getRoute().getName() + "\n"
                                    + "Salida: " + ticket.getRoute().getDateTravel()
                    );
                } else {
                    lblTicketStatus.setText("✘ No se pudo completar la compra.");
                }
            });
        }).start();
    }

    @FXML
    public void handleLogout() {
        model.logout();
        try {
            Stage stage = (Stage) btnComprar.getScene().getWindow();
            stage.setResizable(false);
            ClientFactory.showLogin(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
