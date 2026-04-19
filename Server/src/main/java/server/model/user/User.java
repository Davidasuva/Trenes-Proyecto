package server.model.user;
import edu.uva.app.array.Array;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.carriage.AbstractCarriage;
import server.model.luggage.Luggage;


import java.rmi.RemoteException;
import java.rmi.Remote;

public interface User extends Remote {
    AbstractUser register(AbstractUser user) throws RemoteException;

    LinkedList<AbstractUser> seeUserPerCategory(int category) throws RemoteException;
    Array<Luggage> seeLuggagePerPassenger(String passengerId) throws RemoteException;
    AbstractCarriage seeCarriagePerPassenger(String passengerId) throws RemoteException;
    AbstractUser userPerEmailAndPassword(String email,String password, Object object) throws RemoteException;
    AbstractUser getUserById(String id) throws RemoteException;
    AbstractUser removeUser(String id) throws RemoteException;
    LinkedList<AbstractUser> getUsers() throws RemoteException;
    AbstractUser registerWorker(Worker worker, AbstractUser user) throws RemoteException;
    AbstractUser removeUser(String id, AbstractUser user) throws RemoteException;
    AbstractUser registerPassenger(Passenger passenger) throws RemoteException;
}
