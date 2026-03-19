package edu.project.server.factory;
import edu.project.server.controller.ServerController;
public class ServerFactory {

    public static ServerController create(){
        ServerController controller =new ServerController();
        if(controller==null){
            throw new IllegalStateException("Failed to create ServerController");
        }
        return controller;
    }
}
