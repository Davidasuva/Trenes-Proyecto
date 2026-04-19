package server.model.ticket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import server.model.luggage.Luggage;
import server.model.route.Route;
import server.model.user.Passenger;

public class TicketService extends UnicastRemoteObject implements TicketInterface {

    private LinkedList<Ticket> tickets=new LinkedList<>();

    public TicketService() throws RemoteException {
        super();
    }

    @Override
    public Ticket register(Ticket ticket) throws RemoteException {
        tickets.add(ticket);
        return ticket;
    }

    @Override
    public boolean validate(Ticket ticket) throws RemoteException {
        Iterator<Ticket> iterator=tickets.iterator();
        while (iterator.hasNext()){
            if(iterator.next().equals(ticket)){
                return true;
            }
        }
        return false;
    }

    @Override
    public LinkedList<Ticket> seeTicketsPerPassenger(Passenger passenger){
        LinkedList<Ticket> result = new LinkedList<>();
        Iterator<Ticket> iterator=tickets.iterator();
        while (iterator.hasNext()){
            Ticket ticket=iterator.next();
            if(ticket.getPassenger().equals(passenger)){
                result.add(ticket);
            }
        }
        return result;
    }
    @Override
    public Ticket getTicketById(String id) throws RemoteException {
        Iterator<Ticket> iterator = tickets.iterator();
        while (iterator.hasNext()) {
            Ticket ticket = iterator.next();
            if (ticket.getId().equals(id)){
                return ticket;
            }
        }
        return null;
    }
    @Override
    public boolean setTicketStatus(String ticketId, boolean status) throws RemoteException {
        Ticket ticket = getTicketById(ticketId);
        if (ticket == null) return false;
        ticket.setStatus(status);
        return true;
    }
    @Override
    public boolean addLuggageToTicket(String ticketId, Luggage luggage) throws RemoteException {
        Ticket ticket = getTicketById(ticketId);
        if (ticket == null) return false;
        return ticket.addLuggage(luggage);
    }
    @Override
    public LinkedList<Ticket> getTicketsPerRoute(Route route) throws RemoteException {
        LinkedList<Ticket> result = new LinkedList<>();
        Iterator<Ticket> iterator = tickets.iterator();
        while (iterator.hasNext()) {
            Ticket ticket = iterator.next();
            if (ticket.getRoute().equals(route)) {
                result.add(ticket);
            }
        }
        return result;
    }
    @Override
    public LinkedList<Ticket> getActiveTickets() throws RemoteException {
        LinkedList<Ticket> result = new LinkedList<>();
        Iterator<Ticket> iterator = tickets.iterator();
        while (iterator.hasNext()) {
            Ticket ticket = iterator.next();
            if (ticket.Status()) {
                result.add(ticket);
            }
        }
        return result;
    }
    @Override
    public LinkedList<Ticket> getTickets() throws RemoteException {
        return tickets;
    }



}
