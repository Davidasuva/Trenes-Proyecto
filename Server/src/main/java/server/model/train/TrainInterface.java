package server.model.train;

import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.carriage.AbstractCarriage;
import server.model.user.AbstractUser;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface TrainInterface extends Remote {
    Train register(Train train, AbstractUser user) throws RemoteException;
    Train removeTrain(Train train, AbstractUser user) throws RemoteException;
    Train getTrainById(int id) throws RemoteException;
    LinkedList<AbstractCarriage> seeCarriagesPerTrain(int trainId) throws RemoteException;
    boolean modifyTrain(int id, String name, String type, int capacity, int cargoWagons, AbstractUser user) throws RemoteException;
    boolean addCarriageToTrain(int trainId, AbstractCarriage carriage) throws RemoteException;
    boolean removeCarriageFromTrain(int trainId, AbstractCarriage carriage) throws RemoteException;
    boolean updateMileage(int trainId, int mileage) throws RemoteException;
    LinkedList<Train> getTrainsByType(String type) throws RemoteException;
    LinkedList<Train> getTrainsByCapacity(int minCapacity) throws RemoteException;
    LinkedList<Train> getTrains() throws RemoteException;
    LinkedList<Train> getTrainsByMileage(int maxMileage) throws RemoteException;
}
