import server.controller.ServerController;
import server.factory.ServerFactory;

public class
App {
    public static void main() {
        try {
            ServerController server = ServerFactory.create();
            server.init();
        } catch (Exception e) {
            System.err.println("Failed to start the server application: " + e.getMessage());
        }
    }
}