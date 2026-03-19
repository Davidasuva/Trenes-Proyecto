package server.model.ticket;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface TicketInterface extends Remote {

    Ticket register(Ticket ticket) throws RemoteException;

    boolean validate(Ticket ticket) throws RemoteException;

}