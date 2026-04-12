package server.model.train;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface TrainInterface extends Remote {
    Train register(Train train) throws RemoteException;
    Train removeTrain(Train train) throws RemoteException;
    void seeCarriagesPerTrain(Train train, Object Object) throws RemoteException;
}
