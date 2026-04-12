package server.model.carriage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;

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
    public void seeLuggagesPerCarriage(CarriageLoad carriage, Object object) throws RemoteException {
        Iterator<AbstractCarriage> iterator = carriages.iterator();
        while (iterator.hasNext()) {
            AbstractCarriage next = iterator.next();
            if(next instanceof CarriageLoad){
                if(next == carriage){

                }
            }
        }
    }

    @Override
    public void seePassengersPerCarriage(CarriagePassenger carriage, Object object) throws RemoteException {
        Iterator<AbstractCarriage> iterator = carriages.iterator();
        while (iterator.hasNext()) {
            AbstractCarriage next = iterator.next();
            if(next instanceof CarriagePassenger){
                if(next == carriage){

                }
            }
        }
    }
}
