package server.model.user;

import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.route.Route;

public class Admin extends AbstractHighPermissionUser {

    public Admin(String id, String mail, String name, String lastName, String password, String typeIdetification, String adress, int type) {
        super(id, mail, name, lastName, password, typeIdetification, adress, type);
    }

    public boolean addEmployee(Worker worker) {
        return false;
    }

    public boolean removeEmployee(Worker worker) {
        return false;
    }
    public boolean modifyEmployee(Worker worker) {
        return false;
    }

    public boolean addPassenger(Passenger passenger) {
        return false;
    }
    public boolean removePassenger(Passenger passenger) {
        return false;
    }
    public boolean modifyPassenger(Passenger passenger) {
        return false;
    }
    public boolean publicateRoute(Route route) {
        return false;
    }
    public boolean recommendRoutes(LinkedList<Route> routes) {
        return false;
    }
}
