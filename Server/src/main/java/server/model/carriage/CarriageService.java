package server.model.carriage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.app.priorityQueue.PriorityQueue;
import edu.uva.app.stack.array.Stack;
import edu.uva.model.iterator.Iterator;
import server.model.luggage.Luggage;
import server.model.ticket.Ticket;

public class CarriageService extends UnicastRemoteObject implements CarriageInterface {
    private LinkedList<AbstractCarriage> carriages = new LinkedList();
    public CarriageService() throws RemoteException {
        super();
    }

    @Override
    public AbstractCarriage register(AbstractCarriage carriage) throws RemoteException {
        carriages.add(carriage);
        return carriage;
    }

    @Override
    public AbstractCarriage getCarriageById(int id) throws RemoteException {
        Iterator<AbstractCarriage> iterator = carriages.iterator();
        while (iterator.hasNext()) {
            AbstractCarriage carriage = iterator.next();
            if (carriage.getId() == id) {
                return carriage;
            }
        }
        return null;
    }
    @Override
    public Stack<Luggage> seeLuggagesPerCarriage(int carriageId) throws RemoteException {
        AbstractCarriage carriage = getCarriageById(carriageId);
        if (!(carriage instanceof CarriageLoad)) {
            return null;
        }
        return ((CarriageLoad) carriage).getLuggages();
    }
    @Override
    public PriorityQueue<Ticket> seePassengersPerCarriage(int carriageId) throws RemoteException {
        AbstractCarriage carriage = getCarriageById(carriageId);
        if (!(carriage instanceof CarriagePassenger)) {
            return null;
        }
        return ((CarriagePassenger) carriage).getPassengers();
    }
    @Override
    public boolean addLuggageToCarriage(int carriageId, Luggage luggage) throws RemoteException {
        AbstractCarriage carriage = getCarriageById(carriageId);
        if (!(carriage instanceof CarriageLoad)) {
            return false;
        }
        return ((CarriageLoad) carriage).addLuggage(luggage);
    }
    @Override
    public boolean addPassengerToCarriage(int carriageId, Ticket ticket) throws RemoteException {
        AbstractCarriage carriage = getCarriageById(carriageId);
        if (!(carriage instanceof CarriagePassenger)) {
            return false;
        }

        return ((CarriagePassenger) carriage).addPassenger(ticket);
    }
    @Override
    public boolean hasCapacity(int carriageId) throws RemoteException {
        AbstractCarriage carriage = getCarriageById(carriageId);
        if (carriage instanceof CarriageLoad) {
            return ((CarriageLoad) carriage).hasMoreCapacity();
        }
        if (carriage instanceof CarriagePassenger) {
            return ((CarriagePassenger) carriage).hasMoreCapacity();
        }
        return false;
    }
    @Override
    public LinkedList<CarriageLoad> getLoadCarriages() throws RemoteException {
        LinkedList<CarriageLoad> result = new LinkedList<>();
        Iterator<AbstractCarriage> iterator = carriages.iterator();
        while (iterator.hasNext()) {
            AbstractCarriage carriage = iterator.next();
            if (carriage instanceof CarriageLoad) {
                result.add((CarriageLoad) carriage);
            }
        }
        return result;
    }
    @Override
    public LinkedList<CarriagePassenger> getPassengerCarriages() throws RemoteException {
        LinkedList<CarriagePassenger> result = new LinkedList<>();
        Iterator<AbstractCarriage> iterator = carriages.iterator();
        while (iterator.hasNext()) {
            AbstractCarriage carriage = iterator.next();
            if (carriage instanceof CarriagePassenger) {
                result.add((CarriagePassenger) carriage);
            }
        }
        return result;
    }
    @Override
    public LinkedList<AbstractCarriage> getCarriages() throws RemoteException {
        return carriages;
    }


}
