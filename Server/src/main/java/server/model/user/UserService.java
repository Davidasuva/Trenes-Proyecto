package server.model.user;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.uva.app.linkedlist.singly.singly.LinkedList;
import edu.uva.model.iterator.Iterator;
import server.model.ticket.Ticket;

public class UserService  extends UnicastRemoteObject implements User{

    private LinkedList<AbstractUser> users=new LinkedList<>();
    public UserService() throws RemoteException {
        super();
    }
    @Override
    public AbstractUser register(AbstractUser user) throws RemoteException {
        users.add(user);
        return user;
    }

    @Override
    public void seeUserPerCategory(int category,Object object) throws RemoteException{
        if(category<1||category>3){
            throw new IllegalArgumentException("Invalid category");
        }
        Iterator<AbstractUser> iterator=users.iterator();
        while(iterator.hasNext()){
            if(iterator.next().getType()==category){

            }
        }
    }

    @Override
    public void seeLuggagePerPassenger(Passenger passenger, Object object) throws RemoteException {
        Iterator<AbstractUser> iterator=users.iterator();
        while(iterator.hasNext()){
            AbstractUser user=iterator.next();
            if(user instanceof Passenger){
                if(iterator.next().equals(passenger)){

                }
            }
        }
    }

    @Override
    public void seeCarriagePerPassenger(Passenger passenger, Object object) throws RemoteException {
        Iterator<AbstractUser> iterator=users.iterator();
        while(iterator.hasNext()){
            AbstractUser user=iterator.next();
            if(user instanceof Passenger){
                if(iterator.next().equals(passenger)){

                }
            }
        }
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
}
