package server.model.user;
import java.rmi.RemoteException;
import java.rmi.Remote;

public interface User extends Remote {
    AbstractUser register(AbstractUser user) throws RemoteException;

    void seeUserPerCategory(int category,Object object) throws RemoteException;
    void seeLuggagePerPassenger(Passenger passenger, Object object) throws RemoteException;
    void seeCarriagePerPassenger(Passenger passenger,Object object) throws RemoteException;
    AbstractUser userPerEmailAndPassword(String email,String password, Object object) throws RemoteException;


}
