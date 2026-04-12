package server.model.ticket;

import java.rmi.RemoteException;
import java.rmi.Remote;
import server.model.user.Passenger;

public interface TicketInterface extends Remote {

    Ticket register(Ticket ticket) throws RemoteException;

    boolean validate(Ticket ticket) throws RemoteException;

    void seeTicketsPerPassenger(Passenger Passenger) throws RemoteException;



}