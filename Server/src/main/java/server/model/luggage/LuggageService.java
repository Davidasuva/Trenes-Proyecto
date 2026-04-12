package server.model.luggage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import server.model.user.Passenger;
public class LuggageService extends UnicastRemoteObject implements LuggageInterface {

    private LinkedList<Luggage> luggages=new LinkedList<>();
    public LuggageService() throws RemoteException{
        super();
    }

    @Override
    public Luggage register(Luggage luggage) throws RemoteException {
        luggages.add(luggage);
        return luggage;
    }

    @Override
    public void seeLuggagePerPassenger(Passenger passenger, Object object) throws RemoteException {
        Iterator<Luggage> iterator=luggages.iterator();
        while(iterator.hasNext()){
            if(iterator.next().getTicket().getPassenger().equals(passenger)){

            }
        }
    }
}
