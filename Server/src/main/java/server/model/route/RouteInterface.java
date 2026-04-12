package server.model.route;
import server.model.train.Train;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface RouteInterface extends Remote{

    Route register(Route route) throws RemoteException;
    void seeTrainsPerRoute(Route route, Object object) throws RemoteException;
}
