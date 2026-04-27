package server.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.controller.auth.AuthController;
import server.controller.route.RouteController;
import server.controller.ticket.TicketController;
import server.controller.train.TrainController;
import server.controller.user.UserController;
import server.model.ServerModel;
import environment.Environment;

/**
 * Único punto de creación y navegación de toda la UI del servidor.
 */
public class ServerFactory {

    private ServerFactory() {}

    // ── Escenas pre-creadas ───────────────────────────────────────────────────
    private static Scene sceneRoutes;
    private static Scene sceneTrains;
    private static Scene sceneUsers;
    private static Scene sceneTickets;

    // ── Modelo compartido ─────────────────────────────────────────────────────
    private static ServerModel sharedModel;

    private static UserController   userController   = new UserController();
    private static TicketController ticketController = new TicketController();
    private static RouteController  routeController  = new RouteController();
    private static TrainController  trainController  = new TrainController();

    public static void showLogin(Stage stage) {
        try {
            Environment env = Environment.getInstance();
            ServerModel model = new ServerModel(env.getIp(), env.getPort(), env.getServiceName());

            FXMLLoader loader = new FXMLLoader(
                    ServerFactory.class.getResource("/server/view/auth/login/Login.fxml"));
            Parent root  = loader.load();
            Scene  scene = new Scene(root, 720, 480);

            java.net.URL css = ServerFactory.class.getResource("/server/view/auth/login/Login.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());

            AuthController ctrl = loader.getController();
            ctrl.setModel(model);

            stage.setTitle("trenes — Iniciar sesión");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creando LoginView", e);
        }
    }

    // ── SERVER VIEW (Deploy panel) ─────────────────────────────────────────────

    public static void showServerView(Stage stage, ServerModel model) {
        try {
            server.view.server.ServerView serverView = new server.view.server.ServerView(model);
            stage.setTitle("trenes — Server");
            stage.setResizable(true);
            stage.setScene(new Scene(serverView.getView(), 480, 340));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creando ServerView", e);
        }
    }

    // ── PRE-CREAR ESCENAS DEL MENÚ ─────────────────────────────────────────────

    public static void buildMenuScenes(ServerModel model) {
        if (sceneRoutes != null) return;
        sharedModel = model;
        try {
            sceneRoutes  = buildScene("/server/view/route/RouteView.fxml",
                    server.view.route.RouteView.class,   "route");
            sceneTrains  = buildScene("/server/view/train/TrainView.fxml",
                    server.view.train.TrainView.class,   "train");
            sceneUsers   = buildScene("/server/view/user/UserView.fxml",
                    server.view.user.UserView.class,     "user");
            sceneTickets = buildScene("/server/view/ticket/TicketView.fxml",
                    server.view.ticket.TicketView.class, "ticket");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error pre-creando escenas del menú", e);
        }
    }

    private static Scene buildScene(String fxmlPath, Class<?> refClass, String tipo) throws Exception {
        FXMLLoader loader = new FXMLLoader(refClass.getResource(fxmlPath));
        if (loader.getLocation() == null)
            loader = new FXMLLoader(ServerFactory.class.getResource(fxmlPath));

        Parent root  = loader.load();
        Scene  scene = new Scene(root, 1280, 800);

        Object ctrl = loader.getController();
        switch (tipo) {
            case "route"  -> { routeController  = (RouteController)  ctrl; routeController.setModel(sharedModel); }
            case "train"  -> { trainController  = (TrainController)  ctrl; trainController.setModel(sharedModel); }
            case "user"   -> { userController   = (UserController)   ctrl; userController.setModel(sharedModel);  }
            case "ticket" -> { ticketController = (TicketController) ctrl; ticketController.setModel(sharedModel);}
        }

        // CSS compartido
        java.net.URL css = refClass.getResource("AdminView.css");
        if (css == null) css = ServerFactory.class.getResource(
                fxmlPath.substring(0, fxmlPath.lastIndexOf('/') + 1) + "AdminView.css");
        // Para ticket, el CSS está en route/
        if (css == null)
            css = ServerFactory.class.getResource("/server/view/route/AdminView.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());

        return scene;
    }

    // ── NAVEGACIÓN ──────────────────────────────────────────────────────────────

    public static void navigateToRoutes(Stage stage) {
        if (routeController != null) routeController.refreshRoutes();
        stage.setTitle("trenes — Gestión de Rutas");
        stage.setScene(sceneRoutes);
        stage.setMaximized(true);
    }

    public static void navigateToTrains(Stage stage) {
        if (trainController != null) trainController.refreshTrains();
        stage.setTitle("trenes — Gestión de Trenes");
        stage.setScene(sceneTrains);
        stage.setMaximized(true);
    }

    public static void navigateToUsers(Stage stage) {
        if (userController != null) userController.refreshUsers();
        stage.setTitle("trenes — Usuarios");
        stage.setScene(sceneUsers);
        stage.setMaximized(true);
    }

    public static void navigateToTickets(Stage stage) {
        if (ticketController != null) ticketController.refreshTickets();
        stage.setTitle("trenes — Tickets");
        stage.setScene(sceneTickets);
        stage.setMaximized(true);
    }
}
