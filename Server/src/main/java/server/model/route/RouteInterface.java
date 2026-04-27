package server.model.route;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.app.queue.list.Queue;
import server.model.train.Train;
import server.model.user.AbstractUser;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface RouteInterface extends Remote{

    Route register(Route route) throws RemoteException;
    Queue<Train> seeTrainsPerRoute(int id) throws RemoteException;
    LinkedList<Route> getRoutes() throws RemoteException;
    LinkedList<Route> getAvailableRoutes() throws RemoteException;
    LinkedList<Route> getRoutesPerDestiny(Station destiny)throws RemoteException;
    LinkedList<Route> getRoutesPerOrigin(Station origin)throws RemoteException;
    LinkedList<Route> getRoutesPerDestinyAndOrigin(Station origin, Station destiny)throws RemoteException;
    LinkedList<Route> getRoutesPerDistance(double min, double max)throws RemoteException;
    Route createRoute(int id, String name, Queue<Train> trains, java.time.LocalDateTime dateTravel, java.time.LocalDateTime dateArrival, Station origin, Station destiny, AbstractUser user) throws RemoteException;
    Route getRouteById(int id) throws RemoteException;
    boolean deactivateRoute(int id,AbstractUser user) throws RemoteException;
    boolean addTrainToRoute(int routeId, Train train,AbstractUser user) throws RemoteException;
    Train removeTrainFromRoute(int routeId,AbstractUser user) throws RemoteException;
    LinkedList<Station> getShortestPath(Station origin, Station destiny) throws RemoteException;
    LinkedList<Station> getStations() throws RemoteException;
    boolean activateRoute(int id,AbstractUser user) throws RemoteException;
    boolean existsPath(Station origin, Station destiny) throws RemoteException;
    boolean publicateRoute(int id,AbstractUser user)throws RemoteException;
}

