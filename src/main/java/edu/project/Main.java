package edu.project;
import edu.project.view.auth.AuthView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        AuthView.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}