package server.model.train;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import server.model.carriage.AbstractCarriage;
import server.model.user.AbstractUser;

public class TrainService extends UnicastRemoteObject implements TrainInterface {
    private LinkedList<Train> trains=new LinkedList<>();

    public TrainService() throws RemoteException {
        super();
    }

    @Override
    public Train register(Train train, AbstractUser user) throws RemoteException {
        if(user.getType()==1){
            throw new RemoteException("Sin permisos");
        }
        if (getTrainById(train.getId()) != null) {
            throw new RemoteException("Ya existe un tren con el id: " + train.getId());
        }
        trains.add(train);
        return train;
    }

    @Override
    public Train removeTrain(Train train, AbstractUser user) throws RemoteException {
        if(user.getType()==1){
            throw new RemoteException("Sin permisos");
        }
        if(train==null){
            return null;
        }
        trains.remove(train);
        return train;
    }
    @Override
    public Train getTrainById(int id) throws RemoteException {
        Iterator<Train> iterator = trains.iterator();
        while (iterator.hasNext()) {
            Train train = iterator.next();
            if (train.getId() == id) return train;
        }
        return null;
    }
    @Override
    public LinkedList<AbstractCarriage> seeCarriagesPerTrain(int trainId) throws RemoteException {
        Train train = getTrainById(trainId);
        if (train == null) {
            return null;
        }
        return train.getCarriages();
    }
    @Override
    public boolean addCarriageToTrain(int trainId, AbstractCarriage carriage) throws RemoteException {
        Train train = getTrainById(trainId);
        if (train == null) {
            return false;
        }
        return train.addCarriage(carriage);
    }
    @Override
    public boolean removeCarriageFromTrain(int trainId, AbstractCarriage carriage) throws RemoteException {
        Train train = getTrainById(trainId);
        if (train == null) {
            return false;
        }
        return train.removeCarriage(carriage);
    }
    @Override
    public boolean updateMileage(int trainId, int mileage) throws RemoteException {
        Train train = getTrainById(trainId);
        if (train == null){
            return false;
        }
        train.updateMileage(mileage);
        return true;
    }
    @Override
    public LinkedList<Train> getTrainsByType(String type) throws RemoteException {
        LinkedList<Train> result = new LinkedList<>();
        Iterator<Train> iterator = trains.iterator();
        while (iterator.hasNext()) {
            Train train = iterator.next();
            if (train.getType().equalsIgnoreCase(type)) {
                result.add(train);
            }
        }
        return result;
    }
    @Override
    public LinkedList<Train> getTrainsByCapacity(int minCapacity) throws RemoteException {
        LinkedList<Train> result = new LinkedList<>();
        Iterator<Train> iterator = trains.iterator();
        while (iterator.hasNext()) {
            Train train = iterator.next();
            if (train.getCapacity() >= minCapacity) {
                result.add(train);
            }
        }
        return result;
    }
    @Override
    public LinkedList<Train> getTrains() throws RemoteException {
        return trains;
    }
    @Override
    public LinkedList<Train> getTrainsByMileage(int maxMileage) throws RemoteException {
        LinkedList<Train> result = new LinkedList<>();
        Iterator<Train> iterator = trains.iterator();
        while (iterator.hasNext()) {
            Train train = iterator.next();
            if (train.getMileage() <= maxMileage) {
                result.add(train);
            }
        }
        return result;
    }

    @Override
    public boolean modifyTrain(int id, String name, String type, int capacity, AbstractUser user) throws RemoteException {
        if(user.getType()==1){
            throw new RemoteException("Sin permisos");
        }
        Train train = getTrainById(id);
        if(train==null){
            return false;
        }
        train.setName(name);
        train.setType(type);
        train.setCapacity(capacity);
        return true;

    }
}
