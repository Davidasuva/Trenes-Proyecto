package server.model.carriage;
import java.rmi.RemoteException;
import java.rmi.Remote;

public interface CarriageInterface extends Remote {

    AbstractCarriage register(AbstractCarriage carriage) throws RemoteException;
    void seeLuggagesPerCarriage(CarriageLoad carriage, Object object) throws RemoteException;
    void seePassengersPerCarriage(CarriagePassenger carriage, Object object) throws RemoteException;

}
