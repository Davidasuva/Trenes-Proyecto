package edu.project.server.model.ticket;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Ticket_Service extends UnicastRemoteObject implements Ticket_Interface {
    private int index=0;
    private Ticket[] tickets = new Ticket[100];

    public Ticket_Service() throws RemoteException{
        super();
    }

    public Ticket register(Ticket ticket) throws RemoteException {
        tickets[index]=ticket;
        index++;
        return ticket;
    }

}
