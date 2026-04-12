package server.model.train;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;

public class TrainService extends UnicastRemoteObject implements TrainInterface {
    private LinkedList<Train> trains=new LinkedList<>();

    public TrainService() throws RemoteException {
        super();
    }

    @Override
    public Train register(Train train) throws RemoteException {
        trains.add(train);
        return train;
    }

    @Override
    public Train removeTrain(Train train) throws RemoteException {
        trains.remove(train);
        return train;
    }

    @Override
    public void seeCarriagesPerTrain(Train train, Object Object) throws RemoteException {
        Iterator<Train> iterator=trains.iterator();
        while(iterator.hasNext()){
            if(iterator.next().equals(train)){

            }
        }
    }
}
