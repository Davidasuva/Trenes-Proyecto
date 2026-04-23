package server.factory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import server.controller.auth.AuthController;
import server.controller.route.RouteController;
import server.controller.train.TrainController;
import server.controller.user.UserController;
import server.controller.user.WorkerController;
import server.model.ServerModel;
import environment.Environment;
import server.model.route.RouteService;
import server.model.train.TrainService;
import server.model.user.UserService;

/**
 * Único punto de creación y navegación de toda la UI del servidor.
 *
 * Flujo:
 *   Main  ──► showLogin(stage)
 *   Login ──► showServerView(stage, model)   [tras autenticación]
 *   ServerView ──► buildMenuScenes(model)     [tras deploy exitoso]
 *   Menú  ──► navigateTo*                    [intercambio instantáneo de escena]
 *
 * Las escenas del menú se pre-crean UNA sola vez; cada navegación
 * solo hace stage.setScene() sin recargar FXML ni recrear objetos.
 */
public class ServerFactory {

    private ServerFactory() {}

    // ── Escenas pre-creadas ───────────────────────────────────────────────────
    private static Scene sceneRoutes;
    private static Scene sceneTrains;
    private static Scene sceneUsers;
    private static Scene sceneWorkers;

    // ── Modelo compartido ─────────────────────────────────────────────────────
    private static ServerModel sharedModel;

    private static UserController userController=new UserController();
    private static WorkerController workerController=new WorkerController();
    private static RouteController routeController=new RouteController();
    private static TrainController trainController=new TrainController();

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

    // ─────────────────────────────────────────────────────────────────────────
    // SERVER VIEW  (Deploy panel)
    // ─────────────────────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────────────────────
    // PRE-CREAR ESCENAS DEL MENÚ  (una sola vez, tras deploy)
    // ─────────────────────────────────────────────────────────────────────────

    public static void buildMenuScenes(ServerModel model) {
        if (sceneRoutes != null) return;   // ya construidas
        sharedModel = model;
        try {
            sceneRoutes  = buildScene("/server/view/route/RouteView.fxml",
                    server.view.route.RouteView.class, "route");
            sceneTrains  = buildScene("/server/view/train/TrainView.fxml",
                    server.view.train.TrainView.class, "train");
            sceneUsers   = buildScene("/server/view/user/UserView.fxml",
                    server.view.user.UserView.class, "user");
            sceneWorkers = buildScene("/server/view/user/WorkerView.fxml",
                    server.view.user.WorkerView.class, "worker");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error pre-creando escenas del menú", e);
        }
    }

    private static Scene buildScene(String fxmlPath, Class<?> refClass, String tipo) throws Exception {
        FXMLLoader loader = new FXMLLoader(refClass.getResource(fxmlPath));
        if (loader.getLocation() == null){
            loader = new FXMLLoader(ServerFactory.class.getResource(fxmlPath));
        }
        Parent root  = loader.load();
        Scene  scene = new Scene(root, 1100, 700);

        // Inyectar modelo al controller correspondiente
        Object ctrl = loader.getController();
        switch (tipo) {
            case "route":
                routeController = (RouteController) ctrl;
                routeController.setModel(sharedModel);
                break;
            case "train":
                trainController = (TrainController) ctrl;
                trainController.setModel(sharedModel);
                break;
            case "user":
                userController = (UserController) ctrl;
                userController.setModel(sharedModel);
                break;
            case "worker":
                workerController.setModel(sharedModel);
                workerController.setModel(sharedModel);
                break;
        }

        // CSS relativo al paquete del refClass
        String cssName = fxmlPath.contains("route") || fxmlPath.contains("train")
                || fxmlPath.contains("user") ? "AdminView.css" : null;
        if (cssName != null) {
            java.net.URL css = refClass.getResource(cssName);
            if (css == null) css = ServerFactory.class.getResource(
                    fxmlPath.substring(0, fxmlPath.lastIndexOf('/') + 1) + cssName);
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
        }
        return scene;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NAVEGACIÓN
    // ─────────────────────────────────────────────────────────────────────────

    public static void navigateToRoutes(Stage stage) {
        if(routeController != null){
            routeController.refreshRoutes();
        };
        stage.setTitle("trenes — Gestión de Rutas");
        stage.setScene(sceneRoutes);
        stage.centerOnScreen();
    }

    public static void navigateToTrains(Stage stage) {
        if(trainController != null){
            trainController.refreshTrains();
        }
        stage.setTitle("trenes — Gestión de Trenes");
        stage.setScene(sceneTrains);
        stage.centerOnScreen();
    }

    public static void navigateToUsers(Stage stage) {
        if(userController != null){
            userController.refreshUsers();
        }
        stage.setTitle("trenes — Usuarios");
        stage.setScene(sceneUsers);
        stage.centerOnScreen();
    }

    public static void navigateToWorkers(Stage stage) {
        if(workerController != null){
            workerController.refreshWorkers();
        }
        stage.setTitle("trenes — Trabajadores");
        stage.setScene(sceneWorkers);
        stage.centerOnScreen();
    }
}
