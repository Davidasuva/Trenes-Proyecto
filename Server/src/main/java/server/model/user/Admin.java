package server.model.user;

import edu.uva.app.linkedlist.singly.singly.LinkedList;
import server.model.route.Route;

public class Admin extends AbstractHighPermissionUser {

    public Admin(String id, String mail, String name, String lastName, String password, String typeIdetification, String adress, int type) {
        super(id, mail, name, lastName, password, typeIdetification, adress, 3);
    }
}
