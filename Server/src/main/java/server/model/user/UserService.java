package server.model.user;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import server.model.carriage.AbstractCarriage;
import server.model.luggage.Luggage;
import edu.uva.app.array.Array;

public class UserService  extends UnicastRemoteObject implements User{

    private LinkedList<AbstractUser> users=new LinkedList<>();
    public UserService() throws RemoteException {
        super();
    }
    @Override
    public AbstractUser register(AbstractUser user) throws RemoteException {
        if (getUserById(user.getId()) != null) {
            throw new RemoteException("Ya existe un usuario con el id: " + user.getId());
        }
        if (userPerEmailAndPassword(user.getMail(), user.getPassword(), null) != null) {
            throw new RemoteException("Ya existe un usuario con el email: " + user.getMail());
        }
        users.add(user);
        return user;
    }

    @Override
    public LinkedList<AbstractUser> seeUserPerCategory(int category) throws RemoteException {
        if (category < 1 || category > 3) {
            throw new IllegalArgumentException("Categoría inválida");
        }
        LinkedList<AbstractUser> result = new LinkedList<>();
        Iterator<AbstractUser> iterator = users.iterator();
        while (iterator.hasNext()) {
            AbstractUser user = iterator.next();
            if (user.getType() == category) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public Array<Luggage> seeLuggagePerPassenger(String passengerId) throws RemoteException {
        Passenger passenger = (Passenger) getUserById(passengerId);
        if (passenger == null) {
            return null;
        }
        return passenger.getLuggage();
    }

    @Override
    public AbstractCarriage seeCarriagePerPassenger(String passengerId) throws RemoteException {
        Passenger passenger = (Passenger) getUserById(passengerId);
        if (passenger == null) return null;
        return passenger.getCarriage();
    }

    @Override
    public AbstractUser userPerEmailAndPassword(String email, String password, Object object) throws RemoteException {
        Iterator<AbstractUser> iterator=users.iterator();
        while(iterator.hasNext()){
            AbstractUser user=iterator.next();
            if(user.getMail().equals(email)&&user.getPassword().equals(password)){
                return user;
            }
        }
        return null;
    }

    @Override
    public AbstractUser getUserById(String id) throws RemoteException {
        Iterator<AbstractUser> iterator = users.iterator();
        while (iterator.hasNext()) {
            AbstractUser user = iterator.next();
            if (user.getId().equals(id)) return user;
        }
        return null;
    }
    @Override
    public AbstractUser removeUser(String id) throws RemoteException {
        AbstractUser user = getUserById(id);
        if (user == null){
            return null;
        }
        users.remove(user);
        return user;
    }
    @Override
    public LinkedList<AbstractUser> getUsers() throws RemoteException {
        return users;
    }

    @Override
    public AbstractUser registerWorker(Worker worker, AbstractUser user) throws RemoteException {
        if (user.getType() != 3) {
            throw new RemoteException("Solo administradores");
        }
        return register(worker);

    }

    @Override
    public AbstractUser removeUser(String id, AbstractUser user) throws RemoteException {
        if (user.getType() != 3) {
            throw new RemoteException("Solo administradores");
        }
        return removeUser(id);
    }

    @Override
    public AbstractUser registerPassenger(Passenger passenger) throws RemoteException {
        return register(passenger);
    }
}
