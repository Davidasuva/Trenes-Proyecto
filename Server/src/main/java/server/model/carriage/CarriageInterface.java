package server.model.carriage;
import edu.uva.app.priorityQueue.PriorityQueue;
import edu.uva.app.stack.array.Stack;
import server.model.luggage.Luggage;
import server.model.ticket.Ticket;
import edu.uva.app.linkedlist.singly.singly.LinkedList;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface CarriageInterface extends Remote {

    AbstractCarriage register(AbstractCarriage carriage) throws RemoteException;
    AbstractCarriage getCarriageById(int id) throws RemoteException;
    Stack<Luggage> seeLuggagesPerCarriage(int carriageId) throws RemoteException;
    PriorityQueue<Ticket> seePassengersPerCarriage(int carriageId) throws RemoteException;
    boolean addLuggageToCarriage(int carriageId, Luggage luggage) throws RemoteException;
    boolean addPassengerToCarriage(int carriageId, Ticket ticket) throws RemoteException;
    boolean hasCapacity(int carriageId) throws RemoteException;
    LinkedList<CarriageLoad> getLoadCarriages() throws RemoteException;
    LinkedList<CarriagePassenger> getPassengerCarriages() throws RemoteException;
    LinkedList<AbstractCarriage> getCarriages() throws RemoteException;

}

