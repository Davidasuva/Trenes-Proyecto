package edu.project.model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Ticket_Service extends UnicastRemoteObject implements Ticket_Interface {
    private int index=0;
    private Ticket[] tickets = new Ticket[100];

    public Ticket_Service() throws RemoteException{
        super();
    }

    public Ticket register(String id) throws RemoteException {
        Ticket ticket=new Ticket(id, "Ticket"+id);
        tickets[index++]=ticket;
        return ticket;
    }

}
