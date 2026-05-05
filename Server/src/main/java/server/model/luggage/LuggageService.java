package server.model.luggage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.bintree.avl.BinAVLTree;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import server.model.user.Passenger;
public class LuggageService extends UnicastRemoteObject implements LuggageInterface {

    private BinAVLTree<Luggage> luggages = new BinAVLTree<>();
    public LuggageService() throws RemoteException{
        super();
    }

    @Override
    public Luggage register(Luggage luggage) throws RemoteException {
        if (getLuggageById(luggage.getId()) != null) {
            throw new RemoteException("Ya existe un equipaje con el id: " + luggage.getId());
        }
        luggages.insert(luggage);
        return luggage;
    }
    @Override
    public Luggage getLuggageById(int id) throws RemoteException {
        LinkedList<Luggage> all = luggages.inorder();
        Iterator<Luggage> iterator = all.iterator();
        while (iterator.hasNext()) {
            Luggage luggage = iterator.next();
            if (luggage.getId() == id) {
                return luggage;
            }
        }
        return null;
    }
    @Override
    public Luggage removeLuggage(int id) throws RemoteException {
        Luggage luggage = getLuggageById(id);
        if (luggage == null) {
            return null;
        }
        luggages.remove(luggage);
        return luggage;
    }

    @Override
    public LinkedList<Luggage> seeLuggagePerPassenger(Passenger passenger) throws RemoteException {
        LinkedList<Luggage> result = new LinkedList<>();
        LinkedList<Luggage> all = luggages.inorder();
        Iterator<Luggage> iterator = all.iterator();
        while (iterator.hasNext()) {
            Luggage luggage = iterator.next();
            if (luggage.getTicket() != null &&
                    luggage.getTicket().getPassenger().equals(passenger)) {
                result.add(luggage);
            }
        }
        return result;
    }
    @Override
    public LinkedList<Luggage> getLuggageByMaxWeight(int maxWeight) throws RemoteException {
        LinkedList<Luggage> result = new LinkedList<>();
        LinkedList<Luggage> all = luggages.inorder();
        Iterator<Luggage> iterator = all.iterator();
        while (iterator.hasNext()) {
            Luggage luggage = iterator.next();
            if (luggage.getWeight() <= maxWeight) {
                result.add(luggage);
            }
        }
        return result;
    }
    @Override
    public LinkedList<Luggage> getLuggageByCarriage(int carriageId) throws RemoteException {
        LinkedList<Luggage> result = new LinkedList<>();
        LinkedList<Luggage> all = luggages.inorder();
        Iterator<Luggage> iterator = all.iterator();
        while (iterator.hasNext()) {
            Luggage luggage = iterator.next();
            if (luggage.getCarriage() != null &&
                    luggage.getCarriage().getId() == carriageId) {
                result.add(luggage);
            }
        }
        return result;
    }
    @Override
    public LinkedList<Luggage> getLuggages() throws RemoteException {
        return luggages.inorder();
    }
}
