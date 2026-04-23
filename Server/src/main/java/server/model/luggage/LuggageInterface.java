package server.model.luggage;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.user.Passenger;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface LuggageInterface extends Remote{

    Luggage register(Luggage luggage) throws RemoteException;
    Luggage getLuggageById(int id) throws RemoteException;
    LinkedList<Luggage> seeLuggagePerPassenger(Passenger passenger) throws RemoteException;
    Luggage removeLuggage(int id) throws RemoteException;
    LinkedList<Luggage> getLuggageByMaxWeight(int maxWeight) throws RemoteException;
    LinkedList<Luggage> getLuggageByCarriage(int carriageId) throws RemoteException;
    LinkedList<Luggage> getLuggages() throws RemoteException;

}
