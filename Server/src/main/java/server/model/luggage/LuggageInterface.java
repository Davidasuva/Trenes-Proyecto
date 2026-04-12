package server.model.luggage;
import server.model.user.Passenger;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface LuggageInterface extends Remote{

    Luggage register(Luggage luggage) throws RemoteException;

    void seeLuggagePerPassenger(Passenger passenger, Object object) throws RemoteException;

}
