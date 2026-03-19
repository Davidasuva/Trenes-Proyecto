package edu.project.server.model.ticket;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface Ticket_Interface extends Remote {
    Ticket register(Ticket ticket) throws RemoteException;
}
