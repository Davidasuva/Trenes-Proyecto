package server.model.ticket;

import java.rmi.RemoteException;
import java.rmi.Remote;

import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.luggage.Luggage;
import server.model.route.Route;
import server.model.user.Passenger;

public interface TicketInterface extends Remote {

    Ticket register(Ticket ticket) throws RemoteException;
    Ticket getTicketById(String id) throws RemoteException;
    boolean setTicketStatus(String ticketId, boolean status) throws RemoteException;
    boolean addLuggageToTicket(String ticketId, Luggage luggage) throws RemoteException;
    LinkedList<Ticket> getTicketsPerRoute(Route route) throws RemoteException;
    LinkedList<Ticket> getActiveTickets() throws RemoteException;
    LinkedList<Ticket> getTickets() throws RemoteException;
    boolean validate(Ticket ticket) throws RemoteException;
    LinkedList<Ticket> seeTicketsPerPassenger(Passenger Passenger) throws RemoteException;
    Ticket modifyTicket(Ticket ticket, String id) throws RemoteException;



}