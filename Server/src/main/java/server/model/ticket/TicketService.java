package server.model.ticket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;

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
    public void seeTicketsPerPassenger(Passenger passenger){
        Iterator<Ticket> iterator=tickets.iterator();
        while (iterator.hasNext()){
            if(iterator.next().getPassenger().equals(passenger)){

            }
        }
    }

}
