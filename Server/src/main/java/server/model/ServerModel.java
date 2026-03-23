package server.model;

import server.model.ticket.TicketInterface;
import server.model.ticket.TicketService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class ServerModel {
    private String ip;
    private int port;
    private String serviceName;
    private String uri;

    public ServerModel(String ip, int port, String serviceName) {
        this.ip = ip;
        this.port = port;
        this.serviceName = serviceName;
        this.uri="//"+ip+":"+port+"/"+this.serviceName;
        System.out.println("URI: "+this.uri);
    }

    public boolean deploy(){
        try{
            System.setProperty("java.rmi.server.hostname",ip);
            TicketInterface service= new TicketService();
            LocateRegistry.createRegistry(port);
            Naming.rebind(uri,service);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;

        }
    }
}

