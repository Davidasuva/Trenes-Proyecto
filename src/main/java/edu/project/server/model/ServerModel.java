package edu.project.server.model;

import edu.project.server.model.ticket.Ticket_Interface;
import edu.project.server.model.ticket.Ticket_Service;

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
        this.uri = "//"+ip+":"+port+"/"+serviceName;
    }

    public boolean deploy(){
        try{
            System.setProperty("java.rmi.server.hostname",ip);
            Ticket_Interface service = new Ticket_Service();
            LocateRegistry.createRegistry(port);
            Naming.rebind(uri, service);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
