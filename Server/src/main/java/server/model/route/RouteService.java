package server.model.route;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import server.model.train.Train;
public class RouteService extends UnicastRemoteObject implements RouteInterface{

    private LinkedList<Route> routes=new LinkedList<>();
    public RouteService() throws RemoteException {
        super();
    }

    @Override
    public Route register(Route route) throws RemoteException {
        routes.add(route);
        return route;
    }

    @Override
    public void seeTrainsPerRoute(Route route, Object object) throws RemoteException {
        Iterator<Route> iterator=routes.iterator();
        while(iterator.hasNext()){
            if(iterator.next().equals(route)){

            }
        }
    }
}
