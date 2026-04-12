package server.controller;
import server.model.ServerModel;
import server.view.ServerView;

public class ServerController {

    ServerView view;
    ServerModel model;

    public ServerController(ServerModel model, ServerView view) {
        this.model = model;
        this.view = view;
    }

    public void init(){
        if(model.deploy()){
            view.initComponents(event ->{
                view.startStatus("Server is already");
                return null;
            });
        }else{
            view.setMessage("Failed to deploy the server.");
        }
    }
}
